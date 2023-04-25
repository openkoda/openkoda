import { AbstractParseTreeVisitor } from 'antlr4ts/tree/AbstractParseTreeVisitor';
import { ErrorNode } from 'antlr4ts/tree/ErrorNode';
import {FlowVisitor} from "./grammar/FlowVisitor";
import {ExpressionContext, ServicesContext, OperationContext, ChainContext, NameContext, DotContext, ModelGetContext, LambdaContext, FlowContext, ThenSetContext, KeyContext, FlowExpressionContext} from "./grammar/FlowParser";

// Token range - start and end included
export interface Range {
    start: number;
    end: number;
    offset: number;
}

// Search key
interface KeyResult {
    type: 'KeyResult';
    key: string;
    keys: string[];
    range: Range;
}

interface LambdaResult {
    type: 'LambdaResult';
    name: string;
    range: Range;
}

interface ChainResult {
    type: 'ChainResult';
    range: Range;
}

interface ServicesResult {
    type: 'ServicesResult';
    range: Range;
}

// Search flow
interface FlowStartResult {
    type: 'FlowStartResult';
    range: Range;
}

export type FlowResult = FlowStartResult | ChainResult | ServicesResult | KeyResult | LambdaResult | undefined;

export class GrammarVisitor extends AbstractParseTreeVisitor<FlowResult> implements FlowVisitor<FlowResult> {

    private modelKeys: string[];

    constructor(private index: number) {
        super();
        this.modelKeys = [];
    }

    defaultResult(): FlowResult {
        return;
    }

    aggregateResult(aggregate: FlowResult, nextResult: FlowResult) {
        return nextResult ?? aggregate;
    }

    // Visit root node
    visitExpression(node: ExpressionContext): FlowResult {
        this.modelKeys = [];
        const flow = node.children?.find((child) => child instanceof FlowContext);

        if (flow !== undefined) {
            const flowErr = (flow as FlowContext).children?.find((child) => child instanceof ErrorNode);

            if (flowErr !== undefined) {
                return {
                    type: 'FlowStartResult',
                    range: {
                        start: 0,
                        end: 0,
                        offset: 1
                    }
                };
            }
        }
        // Otherwise, proceed with visiting the child nodes
        const result = this.visitChildren(node);
        let k = this.modelKeys.length;
        return result;
    }

	visitDot(node: DotContext): FlowResult {
        const isFlow = node.parent instanceof ExpressionContext || node.parent instanceof ChainContext;
        const range = this.getRange(node);
        const inCtx = range.start + 1 === this.index;
        if (!inCtx) {
           return this.visitChildren(node);
        }

        if (isFlow) {
            return {
                type: 'ChainResult',
                range: {
                        start: node.start.startIndex,
                        end: node.start.startIndex,
                        offset: 0,
                        }
            };
        }

        const prev = this.findPrevious(node);
        if (prev != null) {
            if (prev instanceof NameContext) {
                const parentLambda = this.findLambda(node);
                if(parentLambda !== undefined && parentLambda?.getChild(0).text === prev.text) {
                    return {
                        type: 'LambdaResult',
                        name: prev.text,
                        range: this.getRange(node),
                    };
                }
            } else if (prev instanceof ServicesContext) {
                return {
                    type: 'ServicesResult',
                    range: this.getRange(node),
                };
            }
        }

        return this.visitChildren(node);
    }

    visitThenSet(node: ThenSetContext): FlowResult {
        const key = node.children?.find((child) => child instanceof KeyContext);
        if (key !== undefined && node.start.startIndex < this.index) {
            this.modelKeys.push(key.text);//.replaceAll('"', ''));
        }
        return this.visitChildren(node);
    }

    // Visit 'key'
    visitKey(node: KeyContext): FlowResult {
        const isModelGet = node.parent instanceof ModelGetContext;
        if (isModelGet && this.isWithinIndex(node)) {
            return {
                type: 'KeyResult',
                key: node.text,
                keys: this.modelKeys,
                range: this.getRange(node),
            };
        }
        return this.visitChildren(node);
    }

    private isWithinIndex(node: any /* TODO */): boolean {
        const range = this.getRange(node);
        return range.start <= this.index && this.index <= range.end;
    }

    private findPrevious(node: DotContext): any {
        let prev = null;
        for (let c of node.parent?.children ?? []) {
            if (c === node) {
                return prev;
            }
            prev = c;
        }
        return null;
    }

    private findLambda(node: DotContext): LambdaContext | undefined {
        let parent: any = node;
        while (parent != null && !(parent instanceof ExpressionContext)) {
            parent = parent.parent;
            if (parent instanceof LambdaContext) {
                return (parent as LambdaContext);
            }
        }
        return undefined;
    }

    private getRange(node: any /* TODO */): Range {
        return {
            start: node.start.startIndex,
            end: Math.max(
                (node.stop ?? node.start).stopIndex,
                node.start.startIndex,
            ),
            offset: node.start.startIndex - this.index + 1,
        };
    }
}
