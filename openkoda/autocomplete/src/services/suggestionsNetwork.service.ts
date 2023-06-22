import {FlowResult} from "../GrammarVisitor";

const serviceDataMap = fetch('/autocomplete/service-data').then(res => res.json()).then(json => (new Map(Object.entries(json).sort())));
//var obj = {"test hint": "test documenatation","test hint4": "test documenatation4","test hint2": "test documenatation2","test hint3": "test documenatation3"}
//const serviceDataMap = Promise.resolve(new Map(Object.entries(obj).sort()));
const serviceSuggestions = serviceDataMap.then(map => Array.from(map.keys()));
export const serviceHintsDocumentation = serviceDataMap.then(map => Array.from(map.values()));

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
        return serviceSuggestions;
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
