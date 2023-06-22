import {FunctionComponent, useCallback, useEffect, useState} from "react";
import {parseQuery} from "../../services/parseQuery.service";
// react-codemirror2
import {UnControlled as CodeMirrorEditor} from 'react-codemirror2'
// codemirror
import CodeMirror, {Editor} from 'codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material-ocean.css';
// codemirror - show-hint
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/show-hint.css';
import {createPortal} from 'react-dom';

import {usePrevious} from "../../hooks/usePrevious.hook";
import {fetchSuggestions, serviceHintsDocumentation} from "../../services/suggestionsNetwork.service";
import {FlowResult} from "../../GrammarVisitor";


// CodeMirror's base and show-hint type
type CodeMirrorEditorType = CodeMirror.Editor & Editor;

// Query info encapsulating the query string and current caret position
interface QueryInfo {
    query: string;
    index: number;
    line: number;
    caretPosition: number;
}

// Store for the fetched suggestions together with the result of parsing
interface SuggestionsInfo {
    result: FlowResult;
    suggestions: string[];
}

interface HintDocumentationInfo{
    visible: boolean;
    selectedHint: number;
    leftPosition: number;
    topPosition: number;
    message: any;
}

export const AutoComplete: FunctionComponent = () => {
    const [codeMirrorEditor, setCodeMirrorEditor] = useState<CodeMirrorEditorType>();

    const [queryInfo, setQueryInfo] = useState<QueryInfo>({
        query: '',
        index: 0,
        line: 0,
        caretPosition: 0
    });

    const [suggestionsInfo, setSuggestionsInfo] = useState<SuggestionsInfo>({
        result: undefined,
        suggestions: [],
    });

    const [hintDocumentationInfo, setHintDocumentationInfo] = useState<HintDocumentationInfo>({
        visible: false,
        selectedHint: -1,
        leftPosition: -1,
        topPosition: -1,
        message:''
    });
    const onChange = useCallback((editor: CodeMirrorEditorType) => {

        const idx = editor.getCursor().line === 0 ? editor.getCursor().ch : Array.from({ length: editor.getCursor().line}, (a,b) => b).map(a => editor.lineInfo(a).text.length).reduce((a,b) => a + b) + editor.getCursor().line + editor.getCursor().ch;
        setQueryInfo({
            query: editor.getValue(),
//ultra crazy hack to get absolute carret position
            index: idx,
            line: editor.getCursor().line,
            caretPosition: editor.getCursor().ch,
        });
    }, []);

    const prevQueryInfo = usePrevious(queryInfo);
    useEffect(() => {
        if (
            !codeMirrorEditor ||
            !prevQueryInfo ||
            !queryInfo ||
            (
                prevQueryInfo.query === queryInfo.query &&
                prevQueryInfo.index === queryInfo.index &&
                prevQueryInfo.line === queryInfo.line &&
                prevQueryInfo.caretPosition === queryInfo.caretPosition
            )
        ) {
            return;
        }
        const result = parseQuery(queryInfo.query, queryInfo.index);

        console.debug('Query', queryInfo.query);
        console.debug('Caret position', queryInfo.caretPosition);
        console.debug('Result', result);

        fetchSuggestions(result).then((fetchedSuggestions) => setSuggestionsInfo({
            result,
            suggestions: fetchedSuggestions,
        }));
    }, [codeMirrorEditor, prevQueryInfo, queryInfo]);
    useEffect(() => {
        if (!codeMirrorEditor) {
            return;
        }
        const isKeyResult = suggestionsInfo.result?.type === 'KeyResult';
        const options = {
            // Don't complete automatically in case of only one suggestion
            completeSingle: false,
            hint: () => ({
                from: { line: queryInfo.line, ch: queryInfo.caretPosition + (suggestionsInfo.result?.range.offset ?? 0)},
                to: {
                        line: queryInfo.line, ch: queryInfo.caretPosition
                            + (suggestionsInfo.result?.range.offset ?? 0) + (suggestionsInfo.result?.range.end ?? 0) - (suggestionsInfo.result?.range.start ?? 0)
                },
                list: suggestionsInfo.suggestions.map((text, index) => ({
                    text: `${text}`,
                })),
            }),
            extraKeys: {"Ctrl-Q": (cm:Editor, handle:any) => {
               if(suggestionsInfo.result?.type === 'ServicesResult' && !!cm.state.completionActive&& !!cm.state.completionActive.widget && window.innerWidth > 610){
                    serviceHintsDocumentation.then(
                      array =>
                         setHintDocumentationInfo(prevState => {
                            const selectedHint = !prevState.visible ? cm.state.completionActive.widget.selectedHint : -1;
                            const hintsWindow = document.getElementById(cm.state.completionActive.widget.id);
                            var top:number=0;
                            var left:number = 0;
                            if(!!hintsWindow){
                               var hintsTop:number = Number(hintsWindow.style.top.replace("px", ""));
                               var hintsLeft:number = Number(hintsWindow.style.left.replace("px", ""));
                               top = hintsTop;
                               if(hintsLeft > 410){
                                left = hintsLeft - 410;
                               } else {
                                var hintsWidth:number = Number(getComputedStyle(hintsWindow).width.replace("px",""));
                                left = hintsLeft + hintsWidth+10;
                               }
                            }
                            return {visible: !prevState.visible, selectedHint: selectedHint, leftPosition: left, topPosition: top, message:array[selectedHint]}
                         })
                      );
                    }
               }
            },
            setHintDocumentationInfo: setHintDocumentationInfo,
            serviceHintsDocumentation: serviceHintsDocumentation
        };
        codeMirrorEditor.showHint(options);
    }, [codeMirrorEditor, suggestionsInfo]);
    return (
        <div>
        <CodeMirrorEditor
            options={{
                theme: 'material',
                lineNumbers: true,
                mode: "javascript"
            }}
            editorDidMount={setCodeMirrorEditor}
            onCursorActivity={onChange}
        />
        {hintDocumentationInfo.visible
        ?
        createPortal(<HintDocumentation selectedHint={hintDocumentationInfo.selectedHint}
                           leftPosition={hintDocumentationInfo.leftPosition}
                           topPosition={hintDocumentationInfo.topPosition}
                           message={hintDocumentationInfo.message}
        />, document.body)
        : null}
        </div>
    );
};

function HintDocumentation(props:any){
return (<div className={"CodeMirror-hints CodeMirror-hints-documentation"} style={{width:'400px', height:'200px', position:'absolute', background:'white', left:props.leftPosition + 'px', top:props.topPosition + 'px'}}>{props.message}</div>)
}