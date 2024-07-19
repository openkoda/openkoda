package com.openkoda.uicomponent.live;

import com.openkoda.repository.SearchableRepositories;
import com.openkoda.service.openai.ChatGPTPromptService;
import com.openkoda.service.openai.ChatGPTService;
import com.openkoda.uicomponent.OpenAIServices;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;


@Component
public class LiveOpenAIServices implements OpenAIServices {
    @Inject
    ChatGPTService chatGPTService;
    @Inject
    ChatGPTPromptService promptService;
    @Override
    public String sendMessageToGPT(String message, String model, String temperature, String... repositoryNames) {
        return chatGPTService.sendMessageToGPT(null, message, model, temperature, null, repositoryNames);
    }

    @Override
    public String sendMessageToGPT(String message, String conversationId) {
        return chatGPTService.sendMessageToGPT(message, conversationId, null);
    }

    @Override
    public String sendMessageToGPTWithPrompt(String promptFileName, String message, String model, String temperature,
                                             String... repositoryNames) {
        return chatGPTService.sendMessageToGPT(promptFileName, message, model, temperature, null, repositoryNames);
    }

    @Override
    public String getCompleteDataSchemaPrompt() {
        return promptService.getDataSchemas(SearchableRepositories.getDynamicSearchableRepositoriesEntityKeys());
    }
}