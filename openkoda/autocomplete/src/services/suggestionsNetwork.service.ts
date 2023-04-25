import {FlowResult} from "../GrammarVisitor";

export const fetchSuggestions = (result: FlowResult): Promise<string[]> => {
    if (!result) {
        return Promise.resolve([]);
    }
    if (result.type === 'KeyResult') {
        return Promise.resolve(
            result.keys
        );
    } else if (result.type === 'LambdaResult') {
        return Promise.resolve(['result', 'services', 'model', 'params', 'form']);
    } else if (result.type === 'ServicesResult') {
        return Promise.resolve(['data.getRepository(entityKey)','data.saveForm(form)','data.getForm(frontendMappingName)', 'data.getForm(frontendMappingName,entity)',`data.saveForm(frontendMappingName,entity)`,
        'integrations.sendMessageToSlack(message, webhook)',
        'messages.sendEmail(recipient, emailTemplateName, model, organizationId)', 'messages.sendToWebsocketUser(user,channelName,payload)', 'messages.sendToWebsocketChannel(channelName,payload)']);
    } else if (result.type === 'FlowStartResult') {
        return Promise.resolve(['flow']);
    } else if (result.type === 'ChainResult') {
        return Promise.resolve(['then(a => a)', 'thenSet("attr", a => a)']);
    } else {
        return Promise.resolve(
            ['Value1', 'Value2', 'Value3', 'Value4', 'Value5']
        );
    }
};
