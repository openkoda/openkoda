import {CharStreams, CommonTokenStream} from 'antlr4ts';
import {FlowResult, GrammarVisitor} from "../GrammarVisitor";
import {FlowLexer} from "../grammar/FlowLexer";
import {FlowParser} from "../grammar/FlowParser";

export const parseQuery = (query: string, index: number): FlowResult => {
    // Create input stream from the given query string
    const inputStream = CharStreams.fromString(prepareQuery(query));
    // Create lexer
    const lexer = new FlowLexer(inputStream);
    const tokenStream = new CommonTokenStream(lexer);
    // Create parser based on the tokens from lexer
    const parser = new FlowParser(tokenStream);

    // Create Abstract Syntax Tree based on the root 'expression' from the parser
    const tree = parser.expression();

    // Visit the tree to gather the result
    const visitor = new GrammarVisitor(index);
    return visitor.visit(tree);
};

const prepareQuery = (query: string): string => {
    // Remove whitespaces at the end of query string
    return query.trimEnd();
};
