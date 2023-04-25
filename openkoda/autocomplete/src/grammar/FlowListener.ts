// Generated from ./src/grammar/Flow.g4 by ANTLR 4.9.0-SNAPSHOT


import { ParseTreeListener } from "antlr4ts/tree/ParseTreeListener";

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
 * This interface defines a complete listener for a parse tree produced by
 * `FlowParser`.
 */
export interface FlowListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by `FlowParser.expression`.
	 * @param ctx the parse tree
	 */
	enterExpression?: (ctx: ExpressionContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.expression`.
	 * @param ctx the parse tree
	 */
	exitExpression?: (ctx: ExpressionContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.chain`.
	 * @param ctx the parse tree
	 */
	enterChain?: (ctx: ChainContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.chain`.
	 * @param ctx the parse tree
	 */
	exitChain?: (ctx: ChainContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.flowExpression`.
	 * @param ctx the parse tree
	 */
	enterFlowExpression?: (ctx: FlowExpressionContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.flowExpression`.
	 * @param ctx the parse tree
	 */
	exitFlowExpression?: (ctx: FlowExpressionContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.flow`.
	 * @param ctx the parse tree
	 */
	enterFlow?: (ctx: FlowContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.flow`.
	 * @param ctx the parse tree
	 */
	exitFlow?: (ctx: FlowContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.dot`.
	 * @param ctx the parse tree
	 */
	enterDot?: (ctx: DotContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.dot`.
	 * @param ctx the parse tree
	 */
	exitDot?: (ctx: DotContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.then`.
	 * @param ctx the parse tree
	 */
	enterThen?: (ctx: ThenContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.then`.
	 * @param ctx the parse tree
	 */
	exitThen?: (ctx: ThenContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.thenSet`.
	 * @param ctx the parse tree
	 */
	enterThenSet?: (ctx: ThenSetContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.thenSet`.
	 * @param ctx the parse tree
	 */
	exitThenSet?: (ctx: ThenSetContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.key`.
	 * @param ctx the parse tree
	 */
	enterKey?: (ctx: KeyContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.key`.
	 * @param ctx the parse tree
	 */
	exitKey?: (ctx: KeyContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.name`.
	 * @param ctx the parse tree
	 */
	enterName?: (ctx: NameContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.name`.
	 * @param ctx the parse tree
	 */
	exitName?: (ctx: NameContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.string`.
	 * @param ctx the parse tree
	 */
	enterString?: (ctx: StringContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.string`.
	 * @param ctx the parse tree
	 */
	exitString?: (ctx: StringContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.lambda`.
	 * @param ctx the parse tree
	 */
	enterLambda?: (ctx: LambdaContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.lambda`.
	 * @param ctx the parse tree
	 */
	exitLambda?: (ctx: LambdaContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.modelGet`.
	 * @param ctx the parse tree
	 */
	enterModelGet?: (ctx: ModelGetContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.modelGet`.
	 * @param ctx the parse tree
	 */
	exitModelGet?: (ctx: ModelGetContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.services`.
	 * @param ctx the parse tree
	 */
	enterServices?: (ctx: ServicesContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.services`.
	 * @param ctx the parse tree
	 */
	exitServices?: (ctx: ServicesContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.result`.
	 * @param ctx the parse tree
	 */
	enterResult?: (ctx: ResultContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.result`.
	 * @param ctx the parse tree
	 */
	exitResult?: (ctx: ResultContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.body`.
	 * @param ctx the parse tree
	 */
	enterBody?: (ctx: BodyContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.body`.
	 * @param ctx the parse tree
	 */
	exitBody?: (ctx: BodyContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.poperation`.
	 * @param ctx the parse tree
	 */
	enterPoperation?: (ctx: PoperationContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.poperation`.
	 * @param ctx the parse tree
	 */
	exitPoperation?: (ctx: PoperationContext) => void;

	/**
	 * Enter a parse tree produced by `FlowParser.operation`.
	 * @param ctx the parse tree
	 */
	enterOperation?: (ctx: OperationContext) => void;
	/**
	 * Exit a parse tree produced by `FlowParser.operation`.
	 * @param ctx the parse tree
	 */
	exitOperation?: (ctx: OperationContext) => void;
}

