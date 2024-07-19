package com.openkoda.service.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.AuditService;
import com.openkoda.core.service.WebsocketService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.theokanning.openai.service.OpenAiService.*;

@Service
public class ChatGPTService implements LoggingComponentWithRequestId {

    public static final int MAX_GPT_REQUEST_LENGTH = 8000;
    private static OpenAiService openAiService;

    @Inject
    WebsocketService websocketService;
    @Inject
    ChatGPTPromptService promptService;
    @Value("${chat.gpt.prompt.cacheFile}") String cacheFileLocation;
    @Value("${chat.gpt.prompt.cacheEnabled}") Boolean cacheGPTMessages;

    private static final int REQUEST_TIMEOUT_SECONDS = 120;
    private static ExecutorService executors = Executors.newFixedThreadPool(5);

    private static final Map<String /* conversationId */, Conversation /* conversation */> messageMap = new HashMap<>();


    private record ConversationCache(
            @Expose Map<String /* concatenatedUserPrompts */, String /* conversationId */> prompts,
            @Expose Map<String /* conversationId */, String /* response */> responses
            ){};
    private record Message(@Expose String role, @Expose String content){};
    private static ConversationCache conversationCache;

    public record Conversation(@Expose String id, @Expose String userEmail, @Expose Long userId, @Expose String model, @Expose Double temperature, @Expose ArrayList<Message> messages){
        private void addSystemMessage(String systemMessage) {
            messages.add(new Message("system", systemMessage));
        }
        public void addMessages(String userMessage, String assistantMessage) {
            messages.add(new Message("user", userMessage));
            messages.add(new Message("assistant", assistantMessage));
        }

        public String getCacheKey(String message) {
            StringBuffer sb = new StringBuffer();
            for (Message m: messages) {
                if ("user".equals(m.role)) {
                    sb.append(m.content);
                    sb.append("|||");
                }
            }
            sb.append(message);
            return sb.toString();
        }

        public String getLastAssistantMessage() {
            Message m = messages.get(messages.size() - 1);
            if ("assistant".equals(m.role)) {
                return m.content;
            }
            return null;
        }

        public Conversation(String id, String userEmail, Long userId, String model, Double temperature, String systemMessage) {
            this( id, userEmail, userId, model, temperature, new ArrayList<>());
            addSystemMessage(systemMessage);
        }
    }

    @Autowired
    public ChatGPTService(@Value("${chat.gpt.api.key:apiKey}") String gptApiKey) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(gptApiKey, Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS));
        Retrofit retrofit = defaultRetrofit(client, mapper);

        OpenAiApi api = retrofit.create(OpenAiApi.class);
        openAiService = new OpenAiService(api);
    }

    public String sendMessageToGPT(String promptFileName, String message, String model, String temperature, String channelName, String ... repositoryNames) {
        debug("[sendMessageToGPT-1] {} {} {} {}", message, model, temperature, repositoryNames);
        String systemPrompt = promptService.getPromptFromFileForEntities(promptFileName, repositoryNames);
        OrganizationUser ou = UserProvider.getFromContext().orElseThrow(RuntimeException::new);
        String id = UUID.randomUUID().toString();
        debug("[sendMessageToGPT-1] user {} conversation {}", ou.getUser().getEmail(), id);
        Conversation c = new Conversation(id, ou.getUser().getEmail(), ou.getUserId(), model, Double.parseDouble(temperature), systemPrompt);
        messageMap.put(id, c);
        return sendMessageToGPT(message, id, channelName);
    }

    public String sendInitMessageToGPT(String systemPrompt, String message, String model, String temperature, String channelName) {
        debug("[sendInitMessageToGPT] {} {} {} {}", message, model, temperature);
        OrganizationUser ou = UserProvider.getFromContext().orElseThrow(RuntimeException::new);
        String id = UUID.randomUUID().toString();
        debug("[sendInitMessageToGPT] user {} conversation {}", ou.getUser().getEmail(), id);
        Conversation c = new Conversation(id, ou.getUser().getEmail(), ou.getUserId(), model, Double.parseDouble(temperature), systemPrompt);
        messageMap.put(id, c);
        return sendMessageToGPT(message, id, channelName);
    }

    public String sendMessageToGPT(String message, String conversationId, String channelName) {
        debug("[sendMessageToGPT-2] {} {}", message, conversationId);
        Conversation c = messageMap.get(conversationId);
        executors.submit(() -> {
            debug("[sendMessageToGPT Task] {} {}", conversationId, message);
            String responseContent = null;
            String cacheKey = null;
            //trying to get conversation from the cache
            if(cacheGPTMessages) {
                cacheKey = c.getCacheKey(message);
                String responseKey = conversationCache.prompts.get(cacheKey);
                responseContent = responseKey == null ? null : conversationCache.responses.get(responseKey);
            }
            boolean cacheHit = responseContent != null;
            if (cacheHit) {
                debug("[sendMessageToGPT Task] found in cache {} {}", conversationId, message);
                c.addMessages(message, responseContent);
            } else {
                debug("[sendMessageToGPT Task] not found in cache. Sending request to GPT {} {}", conversationId, message);
                responseContent = sendMessage(buildCompletionRequest(message, c));
                debug("[sendMessageToGPT Task] entering synchronized block {} {}", conversationId, message);
                synchronized (ChatGPTService.class) {
                    debug("[sendMessageToGPT Task] entered synchronized block {} {}", conversationId, message);
                    c.addMessages(message, responseContent);
                    if(cacheGPTMessages) {
                        String newResponseKey = conversationCache.responses.size() + "";
                        conversationCache.prompts.put(cacheKey, newResponseKey);
                        conversationCache.responses.put(newResponseKey, responseContent);
                        try {
                            FileUtils.write(new File(cacheFileLocation), JsonHelper.to(conversationCache), "UTF-8");
                            debug("[sendMessageToGPT Task] updated cache file {} {}", conversationId, message);
                        } catch (Exception e) {
                            error("Error writing conversation cache", e);
                        }
                    }
                }

                debug("[sendMessageToGPT Task] create audit log {} {}", conversationId, message);
                AuditService.createSimpleInfoAudit("GPT conversation id: " + conversationId, c.messages.toString());
            }
            debug("[sendMessageToGPT Task] sending response to websocket {} {}", conversationId, message);
            if(StringUtils.isNotEmpty(channelName)) {
                websocketService.sendToChannel(channelName, Map.of("conversationId", conversationId, "response", responseContent));
            } else {
                websocketService.sendToUserChannel(c.userEmail, "/queue/ai", Map.of("conversationId", conversationId, "response", responseContent));
            }
            debug("[sendMessageToGPT Task] exiting {} {}", conversationId, message);

        });
        return conversationId;
    }

    private String sendMessage(ChatCompletionRequest chatCompletionRequest) {
        debug("[sendMessage] {}", chatCompletionRequest);
        Optional<ChatCompletionChoice> chatCompletionChoice = sendCompletionRequest(chatCompletionRequest);
        if (chatCompletionChoice.isPresent()) {
            return chatCompletionChoice.get().getMessage().getContent();
        } else {
            throw new RuntimeException("Error occurred, try to resend your message.");
        }
    }

    public Optional<ChatCompletionChoice> sendCompletionRequest(ChatCompletionRequest request) {
        long timestamp = System.currentTimeMillis();
        try {
            ChatCompletionChoice chatCompletionChoice = openAiService.createChatCompletion(request).getChoices().get(0);
            if (chatCompletionChoice.getMessage().getContent().length() > MAX_GPT_REQUEST_LENGTH) {
                error("Chat responded with excessive message: {}", chatCompletionChoice.getMessage().getContent());
            }
            debug("Completion request completed in {} seconds", ((System.currentTimeMillis() - timestamp) / 1000));
            return Optional.of(chatCompletionChoice);
        } catch (Exception e) {
            error("Completion request failed in {} seconds. Reason: {}", ((System.currentTimeMillis() - timestamp) / 1000), e.getMessage());
        }
        return Optional.empty();
    }

    private ChatCompletionRequest buildCompletionRequest(String message, Conversation c) {
        ChatMessage newUserMessage = new ChatMessage("user", message);
        List<ChatMessage> messages = new ArrayList<>(c.messages.size() + 1);
        c.messages.forEach(a -> messages.add(new ChatMessage(a.role, a.content)));
        messages.add(newUserMessage);

        return ChatCompletionRequest.builder()
                .messages(messages)
                .user("stratoflow")
                .model(c.model)
                .temperature(c.temperature)
                .build();
    }

    @PostConstruct void init() {
        if(cacheGPTMessages) {
            try {
                String cache = FileUtils.readFileToString(new File(cacheFileLocation), "UTF-8");
                conversationCache = JsonHelper.from(cache, ConversationCache.class);
                debug("Read {} conversations", conversationCache.prompts.size());
            } catch (Exception e) {
                conversationCache = new ConversationCache(new HashMap<>(), new HashMap<>());
                warn("Error reading cache file {}. Don't worry, it may just not exist.", cacheFileLocation);
            }
        }
    }
}
