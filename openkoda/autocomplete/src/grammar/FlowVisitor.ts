// Generated from ./src/grammar/Flow.g4 by ANTLR 4.9.0-SNAPSHOT


import { ParseTreeVisitor } from "antlr4ts/tree/ParseTreeVisitor";

import { ExpressionContext } from "./FlowParser";
import { ChainContext } from "./FlowParser";
import { FlowExpressionContext } from "./FlowParser";
import { FlowContext } from "./FlowParser";
import { DotContext } from "./FlowParser";
import { ThenContext } from "./FlowParser";
import { ThenSetContext } from "./FlowParser";
import { KeyContext } from "./FlowParser";
import { NameContext } from "./FlowParser";
import { StringContext } from "./FlowParser";
import { LambdaContext } from "./FlowParser";
import { ModelGetContext } from "./FlowParser";
import { ServicesContext } from "./FlowParser";
import { ResultContext } from "./FlowParser";
import { BodyContext } from "./FlowParser";
import { PoperationContext } from "./FlowParser";
import { OperationContext } from "./FlowParser";


/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by `FlowParser`.
 *
 * @param <Result> The return type of the visit operation. Use `void` for
 * operations with no return type.
 */
export interface FlowVisitor<Result> extends ParseTreeVisitor<Result> {
	/**
	 * Visit a parse tree produced by `FlowParser.expression`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitExpression?: (ctx: ExpressionContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.chain`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitChain?: (ctx: ChainContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.flowExpression`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitFlowExpression?: (ctx: FlowExpressionContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.flow`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitFlow?: (ctx: FlowContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.dot`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitDot?: (ctx: DotContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.then`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitThen?: (ctx: ThenContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.thenSet`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitThenSet?: (ctx: ThenSetContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.key`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitKey?: (ctx: KeyContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.name`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitName?: (ctx: NameContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.string`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitString?: (ctx: StringContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.lambda`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitLambda?: (ctx: LambdaContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.modelGet`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitModelGet?: (ctx: ModelGetContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.services`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitServices?: (ctx: ServicesContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.result`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitResult?: (ctx: ResultContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.body`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitBody?: (ctx: BodyContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.poperation`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitPoperation?: (ctx: PoperationContext) => Result;

	/**
	 * Visit a parse tree produced by `FlowParser.operation`.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	visitOperation?: (ctx: OperationContext) => Result;
}

