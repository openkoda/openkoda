// Generated from ./src/grammar/Flow.g4 by ANTLR 4.9.0-SNAPSHOT


import { ATN } from "antlr4ts/atn/ATN";
import { ATNDeserializer } from "antlr4ts/atn/ATNDeserializer";
import { FailedPredicateException } from "antlr4ts/FailedPredicateException";
import { NotNull } from "antlr4ts/Decorators";
import { NoViableAltException } from "antlr4ts/NoViableAltException";
import { Override } from "antlr4ts/Decorators";
import { Parser } from "antlr4ts/Parser";
import { ParserRuleContext } from "antlr4ts/ParserRuleContext";
import { ParserATNSimulator } from "antlr4ts/atn/ParserATNSimulator";
import { ParseTreeListener } from "antlr4ts/tree/ParseTreeListener";
import { ParseTreeVisitor } from "antlr4ts/tree/ParseTreeVisitor";
import { RecognitionException } from "antlr4ts/RecognitionException";
import { RuleContext } from "antlr4ts/RuleContext";
//import { RuleVersion } from "antlr4ts/RuleVersion";
import { TerminalNode } from "antlr4ts/tree/TerminalNode";
import { Token } from "antlr4ts/Token";
import { TokenStream } from "antlr4ts/TokenStream";
import { Vocabulary } from "antlr4ts/Vocabulary";
import { VocabularyImpl } from "antlr4ts/VocabularyImpl";

import * as Utils from "antlr4ts/misc/Utils";

import { FlowListener } from "./FlowListener";
import { FlowVisitor } from "./FlowVisitor";


export class FlowParser extends Parser {
	public static readonly T__0 = 1;
	public static readonly T__1 = 2;
	public static readonly T__2 = 3;
	public static readonly T__3 = 4;
	public static readonly T__4 = 5;
	public static readonly T__5 = 6;
	public static readonly T__6 = 7;
	public static readonly T__7 = 8;
	public static readonly T__8 = 9;
	public static readonly MGET = 10;
	public static readonly SERVICES = 11;
	public static readonly RESULT = 12;
	public static readonly THEN = 13;
	public static readonly THENSET = 14;
	public static readonly FLOW = 15;
	public static readonly NAME = 16;
	public static readonly KEY = 17;
	public static readonly STRING = 18;
	public static readonly SP = 19;
	public static readonly WS = 20;
	public static readonly RULE_expression = 0;
	public static readonly RULE_chain = 1;
	public static readonly RULE_flowExpression = 2;
	public static readonly RULE_flow = 3;
	public static readonly RULE_dot = 4;
	public static readonly RULE_then = 5;
	public static readonly RULE_thenSet = 6;
	public static readonly RULE_key = 7;
	public static readonly RULE_name = 8;
	public static readonly RULE_string = 9;
	public static readonly RULE_lambda = 10;
	public static readonly RULE_modelGet = 11;
	public static readonly RULE_services = 12;
	public static readonly RULE_result = 13;
	public static readonly RULE_body = 14;
	public static readonly RULE_poperation = 15;
	public static readonly RULE_operation = 16;
	// tslint:disable:no-trailing-whitespace
	public static readonly ruleNames: string[] = [
		"expression", "chain", "flowExpression", "flow", "dot", "then", "thenSet", 
		"key", "name", "string", "lambda", "modelGet", "services", "result", "body", 
		"poperation", "operation",
	];

	private static readonly _LITERAL_NAMES: Array<string | undefined> = [
		undefined, "'.'", "'('", "')'", "','", "'=>'", "'{'", "'}'", "'\"'", "'='", 
		"'model.get'", "'services'", "'result'", "'then'", "'thenSet'", "'flow'",
	];
	private static readonly _SYMBOLIC_NAMES: Array<string | undefined> = [
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, "MGET", "SERVICES", "RESULT", "THEN", 
		"THENSET", "FLOW", "NAME", "KEY", "STRING", "SP", "WS",
	];
	public static readonly VOCABULARY: Vocabulary = new VocabularyImpl(FlowParser._LITERAL_NAMES, FlowParser._SYMBOLIC_NAMES, []);

	// @Override
	// @NotNull
	public get vocabulary(): Vocabulary {
		return FlowParser.VOCABULARY;
	}
	// tslint:enable:no-trailing-whitespace

	// @Override
	public get grammarFileName(): string { return "Flow.g4"; }

	// @Override
	public get ruleNames(): string[] { return FlowParser.ruleNames; }

	// @Override
	public get serializedATN(): string { return FlowParser._serializedATN; }

	protected createFailedPredicateException(predicate?: string, message?: string): FailedPredicateException {
		return new FailedPredicateException(this, predicate, message);
	}

	constructor(input: TokenStream) {
		super(input);
		this._interp = new ParserATNSimulator(FlowParser._ATN, this);
	}
	// @RuleVersion(0)
	public expression(): ExpressionContext {
		let _localctx: ExpressionContext = new ExpressionContext(this._ctx, this.state);
		this.enterRule(_localctx, 0, FlowParser.RULE_expression);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 34;
			this.flow();
			this.state = 35;
			this.dot();
			this.state = 36;
			this.chain();
			this.state = 37;
			this.match(FlowParser.EOF);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public chain(): ChainContext {
		let _localctx: ChainContext = new ChainContext(this._ctx, this.state);
		this.enterRule(_localctx, 2, FlowParser.RULE_chain);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 39;
			this.flowExpression();
			this.state = 45;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			while (_la === FlowParser.T__0) {
				{
				{
				this.state = 40;
				this.dot();
				this.state = 41;
				this.flowExpression();
				}
				}
				this.state = 47;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public flowExpression(): FlowExpressionContext {
		let _localctx: FlowExpressionContext = new FlowExpressionContext(this._ctx, this.state);
		this.enterRule(_localctx, 4, FlowParser.RULE_flowExpression);
		try {
			this.state = 50;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case FlowParser.THEN:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 48;
				this.then();
				}
				break;
			case FlowParser.THENSET:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 49;
				this.thenSet();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public flow(): FlowContext {
		let _localctx: FlowContext = new FlowContext(this._ctx, this.state);
		this.enterRule(_localctx, 6, FlowParser.RULE_flow);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 52;
			this.match(FlowParser.FLOW);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public dot(): DotContext {
		let _localctx: DotContext = new DotContext(this._ctx, this.state);
		this.enterRule(_localctx, 8, FlowParser.RULE_dot);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 54;
			this.match(FlowParser.T__0);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public then(): ThenContext {
		let _localctx: ThenContext = new ThenContext(this._ctx, this.state);
		this.enterRule(_localctx, 10, FlowParser.RULE_then);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 56;
			this.match(FlowParser.THEN);
			this.state = 57;
			this.match(FlowParser.T__1);
			this.state = 58;
			this.lambda();
			this.state = 59;
			this.match(FlowParser.T__2);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public thenSet(): ThenSetContext {
		let _localctx: ThenSetContext = new ThenSetContext(this._ctx, this.state);
		this.enterRule(_localctx, 12, FlowParser.RULE_thenSet);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 61;
			this.match(FlowParser.THENSET);
			this.state = 62;
			this.match(FlowParser.T__1);
			this.state = 63;
			this.key();
			this.state = 64;
			this.match(FlowParser.T__3);
			this.state = 65;
			this.lambda();
			this.state = 66;
			this.match(FlowParser.T__2);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public key(): KeyContext {
		let _localctx: KeyContext = new KeyContext(this._ctx, this.state);
		this.enterRule(_localctx, 14, FlowParser.RULE_key);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 68;
			this.match(FlowParser.KEY);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public name(): NameContext {
		let _localctx: NameContext = new NameContext(this._ctx, this.state);
		this.enterRule(_localctx, 16, FlowParser.RULE_name);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 70;
			this.match(FlowParser.NAME);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public string(): StringContext {
		let _localctx: StringContext = new StringContext(this._ctx, this.state);
		this.enterRule(_localctx, 18, FlowParser.RULE_string);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 72;
			_la = this._input.LA(1);
			if (!(_la === FlowParser.KEY || _la === FlowParser.STRING)) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public lambda(): LambdaContext {
		let _localctx: LambdaContext = new LambdaContext(this._ctx, this.state);
		this.enterRule(_localctx, 20, FlowParser.RULE_lambda);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 74;
			this.name();
			this.state = 75;
			this.match(FlowParser.T__4);
			this.state = 76;
			this.body();
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public modelGet(): ModelGetContext {
		let _localctx: ModelGetContext = new ModelGetContext(this._ctx, this.state);
		this.enterRule(_localctx, 22, FlowParser.RULE_modelGet);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 78;
			this.match(FlowParser.MGET);
			this.state = 79;
			this.match(FlowParser.T__1);
			this.state = 80;
			this.key();
			this.state = 81;
			this.match(FlowParser.T__2);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public services(): ServicesContext {
		let _localctx: ServicesContext = new ServicesContext(this._ctx, this.state);
		this.enterRule(_localctx, 24, FlowParser.RULE_services);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 83;
			this.match(FlowParser.SERVICES);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public result(): ResultContext {
		let _localctx: ResultContext = new ResultContext(this._ctx, this.state);
		this.enterRule(_localctx, 26, FlowParser.RULE_result);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 85;
			this.match(FlowParser.RESULT);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public body(): BodyContext {
		let _localctx: BodyContext = new BodyContext(this._ctx, this.state);
		this.enterRule(_localctx, 28, FlowParser.RULE_body);
		let _la: number;
		try {
			this.state = 93;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case FlowParser.T__0:
			case FlowParser.T__1:
			case FlowParser.T__3:
			case FlowParser.T__4:
			case FlowParser.T__7:
			case FlowParser.T__8:
			case FlowParser.MGET:
			case FlowParser.SERVICES:
			case FlowParser.RESULT:
			case FlowParser.NAME:
			case FlowParser.KEY:
			case FlowParser.STRING:
			case FlowParser.SP:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 87;
				this.operation();
				}
				break;
			case FlowParser.T__5:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 88;
				this.match(FlowParser.T__5);
				this.state = 90;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if ((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << FlowParser.T__0) | (1 << FlowParser.T__1) | (1 << FlowParser.T__3) | (1 << FlowParser.T__4) | (1 << FlowParser.T__7) | (1 << FlowParser.T__8) | (1 << FlowParser.MGET) | (1 << FlowParser.SERVICES) | (1 << FlowParser.RESULT) | (1 << FlowParser.NAME) | (1 << FlowParser.KEY) | (1 << FlowParser.STRING) | (1 << FlowParser.SP))) !== 0)) {
					{
					this.state = 89;
					this.operation();
					}
				}

				this.state = 92;
				this.match(FlowParser.T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public poperation(): PoperationContext {
		let _localctx: PoperationContext = new PoperationContext(this._ctx, this.state);
		this.enterRule(_localctx, 30, FlowParser.RULE_poperation);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 95;
			this.match(FlowParser.T__1);
			this.state = 97;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if ((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << FlowParser.T__0) | (1 << FlowParser.T__1) | (1 << FlowParser.T__3) | (1 << FlowParser.T__4) | (1 << FlowParser.T__7) | (1 << FlowParser.T__8) | (1 << FlowParser.MGET) | (1 << FlowParser.SERVICES) | (1 << FlowParser.RESULT) | (1 << FlowParser.NAME) | (1 << FlowParser.KEY) | (1 << FlowParser.STRING) | (1 << FlowParser.SP))) !== 0)) {
				{
				this.state = 96;
				this.operation();
				}
			}

			this.state = 99;
			this.match(FlowParser.T__2);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public operation(): OperationContext {
		let _localctx: OperationContext = new OperationContext(this._ctx, this.state);
		this.enterRule(_localctx, 32, FlowParser.RULE_operation);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 113;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			do {
				{
				this.state = 113;
				this._errHandler.sync(this);
				switch (this._input.LA(1)) {
				case FlowParser.MGET:
					{
					this.state = 101;
					this.modelGet();
					}
					break;
				case FlowParser.SERVICES:
					{
					this.state = 102;
					this.services();
					}
					break;
				case FlowParser.RESULT:
					{
					this.state = 103;
					this.result();
					}
					break;
				case FlowParser.KEY:
				case FlowParser.STRING:
					{
					this.state = 104;
					this.string();
					}
					break;
				case FlowParser.T__3:
					{
					this.state = 105;
					this.match(FlowParser.T__3);
					}
					break;
				case FlowParser.T__7:
					{
					this.state = 106;
					this.match(FlowParser.T__7);
					}
					break;
				case FlowParser.T__8:
					{
					this.state = 107;
					this.match(FlowParser.T__8);
					}
					break;
				case FlowParser.T__4:
					{
					this.state = 108;
					this.match(FlowParser.T__4);
					}
					break;
				case FlowParser.NAME:
					{
					this.state = 109;
					this.name();
					}
					break;
				case FlowParser.SP:
					{
					this.state = 110;
					this.match(FlowParser.SP);
					}
					break;
				case FlowParser.T__0:
					{
					this.state = 111;
					this.dot();
					}
					break;
				case FlowParser.T__1:
					{
					this.state = 112;
					this.poperation();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				this.state = 115;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			} while ((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << FlowParser.T__0) | (1 << FlowParser.T__1) | (1 << FlowParser.T__3) | (1 << FlowParser.T__4) | (1 << FlowParser.T__7) | (1 << FlowParser.T__8) | (1 << FlowParser.MGET) | (1 << FlowParser.SERVICES) | (1 << FlowParser.RESULT) | (1 << FlowParser.NAME) | (1 << FlowParser.KEY) | (1 << FlowParser.STRING) | (1 << FlowParser.SP))) !== 0));
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}

	public static readonly _serializedATN: string =
		"\x03\uC91D\uCABA\u058D\uAFBA\u4F53\u0607\uEA8B\uC241\x03\x16x\x04\x02" +
		"\t\x02\x04\x03\t\x03\x04\x04\t\x04\x04\x05\t\x05\x04\x06\t\x06\x04\x07" +
		"\t\x07\x04\b\t\b\x04\t\t\t\x04\n\t\n\x04\v\t\v\x04\f\t\f\x04\r\t\r\x04" +
		"\x0E\t\x0E\x04\x0F\t\x0F\x04\x10\t\x10\x04\x11\t\x11\x04\x12\t\x12\x03" +
		"\x02\x03\x02\x03\x02\x03\x02\x03\x02\x03\x03\x03\x03\x03\x03\x03\x03\x07" +
		"\x03.\n\x03\f\x03\x0E\x031\v\x03\x03\x04\x03\x04\x05\x045\n\x04\x03\x05" +
		"\x03\x05\x03\x06\x03\x06\x03\x07\x03\x07\x03\x07\x03\x07\x03\x07\x03\b" +
		"\x03\b\x03\b\x03\b\x03\b\x03\b\x03\b\x03\t\x03\t\x03\n\x03\n\x03\v\x03" +
		"\v\x03\f\x03\f\x03\f\x03\f\x03\r\x03\r\x03\r\x03\r\x03\r\x03\x0E\x03\x0E" +
		"\x03\x0F\x03\x0F\x03\x10\x03\x10\x03\x10\x05\x10]\n\x10\x03\x10\x05\x10" +
		"`\n\x10\x03\x11\x03\x11\x05\x11d\n\x11\x03\x11\x03\x11\x03\x12\x03\x12" +
		"\x03\x12\x03\x12\x03\x12\x03\x12\x03\x12\x03\x12\x03\x12\x03\x12\x03\x12" +
		"\x03\x12\x06\x12t\n\x12\r\x12\x0E\x12u\x03\x12\x02\x02\x02\x13\x02\x02" +
		"\x04\x02\x06\x02\b\x02\n\x02\f\x02\x0E\x02\x10\x02\x12\x02\x14\x02\x16" +
		"\x02\x18\x02\x1A\x02\x1C\x02\x1E\x02 \x02\"\x02\x02\x03\x03\x02\x13\x14" +
		"\x02w\x02$\x03\x02\x02\x02\x04)\x03\x02\x02\x02\x064\x03\x02\x02\x02\b" +
		"6\x03\x02\x02\x02\n8\x03\x02\x02\x02\f:\x03\x02\x02\x02\x0E?\x03\x02\x02" +
		"\x02\x10F\x03\x02\x02\x02\x12H\x03\x02\x02\x02\x14J\x03\x02\x02\x02\x16" +
		"L\x03\x02\x02\x02\x18P\x03\x02\x02\x02\x1AU\x03\x02\x02\x02\x1CW\x03\x02" +
		"\x02\x02\x1E_\x03\x02\x02\x02 a\x03\x02\x02\x02\"s\x03\x02\x02\x02$%\x05" +
		"\b\x05\x02%&\x05\n\x06\x02&\'\x05\x04\x03\x02\'(\x07\x02\x02\x03(\x03" +
		"\x03\x02\x02\x02)/\x05\x06\x04\x02*+\x05\n\x06\x02+,\x05\x06\x04\x02," +
		".\x03\x02\x02\x02-*\x03\x02\x02\x02.1\x03\x02\x02\x02/-\x03\x02\x02\x02" +
		"/0\x03\x02\x02\x020\x05\x03\x02\x02\x021/\x03\x02\x02\x0225\x05\f\x07" +
		"\x0235\x05\x0E\b\x0242\x03\x02\x02\x0243\x03\x02\x02\x025\x07\x03\x02" +
		"\x02\x0267\x07\x11\x02\x027\t\x03\x02\x02\x0289\x07\x03\x02\x029\v\x03" +
		"\x02\x02\x02:;\x07\x0F\x02\x02;<\x07\x04\x02\x02<=\x05\x16\f\x02=>\x07" +
		"\x05\x02\x02>\r\x03\x02\x02\x02?@\x07\x10\x02\x02@A\x07\x04\x02\x02AB" +
		"\x05\x10\t\x02BC\x07\x06\x02\x02CD\x05\x16\f\x02DE\x07\x05\x02\x02E\x0F" +
		"\x03\x02\x02\x02FG\x07\x13\x02\x02G\x11\x03\x02\x02\x02HI\x07\x12\x02" +
		"\x02I\x13\x03\x02\x02\x02JK\t\x02\x02\x02K\x15\x03\x02\x02\x02LM\x05\x12" +
		"\n\x02MN\x07\x07\x02\x02NO\x05\x1E\x10\x02O\x17\x03\x02\x02\x02PQ\x07" +
		"\f\x02\x02QR\x07\x04\x02\x02RS\x05\x10\t\x02ST\x07\x05\x02\x02T\x19\x03" +
		"\x02\x02\x02UV\x07\r\x02\x02V\x1B\x03\x02\x02\x02WX\x07\x0E\x02\x02X\x1D" +
		"\x03\x02\x02\x02Y`\x05\"\x12\x02Z\\\x07\b\x02\x02[]\x05\"\x12\x02\\[\x03" +
		"\x02\x02\x02\\]\x03\x02\x02\x02]^\x03\x02\x02\x02^`\x07\t\x02\x02_Y\x03" +
		"\x02\x02\x02_Z\x03\x02\x02\x02`\x1F\x03\x02\x02\x02ac\x07\x04\x02\x02" +
		"bd\x05\"\x12\x02cb\x03\x02\x02\x02cd\x03\x02\x02\x02de\x03\x02\x02\x02" +
		"ef\x07\x05\x02\x02f!\x03\x02\x02\x02gt\x05\x18\r\x02ht\x05\x1A\x0E\x02" +
		"it\x05\x1C\x0F\x02jt\x05\x14\v\x02kt\x07\x06\x02\x02lt\x07\n\x02\x02m" +
		"t\x07\v\x02\x02nt\x07\x07\x02\x02ot\x05\x12\n\x02pt\x07\x15\x02\x02qt" +
		"\x05\n\x06\x02rt\x05 \x11\x02sg\x03\x02\x02\x02sh\x03\x02\x02\x02si\x03" +
		"\x02\x02\x02sj\x03\x02\x02\x02sk\x03\x02\x02\x02sl\x03\x02\x02\x02sm\x03" +
		"\x02\x02\x02sn\x03\x02\x02\x02so\x03\x02\x02\x02sp\x03\x02\x02\x02sq\x03" +
		"\x02\x02\x02sr\x03\x02\x02\x02tu\x03\x02\x02\x02us\x03\x02\x02\x02uv\x03" +
		"\x02\x02\x02v#\x03\x02\x02\x02\t/4\\_csu";
	public static __ATN: ATN;
	public static get _ATN(): ATN {
		if (!FlowParser.__ATN) {
			FlowParser.__ATN = new ATNDeserializer().deserialize(Utils.toCharArray(FlowParser._serializedATN));
		}

		return FlowParser.__ATN;
	}

}

export class ExpressionContext extends ParserRuleContext {
	public flow(): FlowContext {
		return this.getRuleContext(0, FlowContext);
	}
	public dot(): DotContext {
		return this.getRuleContext(0, DotContext);
	}
	public chain(): ChainContext {
		return this.getRuleContext(0, ChainContext);
	}
	public EOF(): TerminalNode { return this.getToken(FlowParser.EOF, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_expression; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterExpression) {
			listener.enterExpression(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitExpression) {
			listener.exitExpression(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitExpression) {
			return visitor.visitExpression(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ChainContext extends ParserRuleContext {
	public flowExpression(): FlowExpressionContext[];
	public flowExpression(i: number): FlowExpressionContext;
	public flowExpression(i?: number): FlowExpressionContext | FlowExpressionContext[] {
		if (i === undefined) {
			return this.getRuleContexts(FlowExpressionContext);
		} else {
			return this.getRuleContext(i, FlowExpressionContext);
		}
	}
	public dot(): DotContext[];
	public dot(i: number): DotContext;
	public dot(i?: number): DotContext | DotContext[] {
		if (i === undefined) {
			return this.getRuleContexts(DotContext);
		} else {
			return this.getRuleContext(i, DotContext);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_chain; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterChain) {
			listener.enterChain(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitChain) {
			listener.exitChain(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitChain) {
			return visitor.visitChain(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class FlowExpressionContext extends ParserRuleContext {
	public then(): ThenContext | undefined {
		return this.tryGetRuleContext(0, ThenContext);
	}
	public thenSet(): ThenSetContext | undefined {
		return this.tryGetRuleContext(0, ThenSetContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_flowExpression; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterFlowExpression) {
			listener.enterFlowExpression(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitFlowExpression) {
			listener.exitFlowExpression(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitFlowExpression) {
			return visitor.visitFlowExpression(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class FlowContext extends ParserRuleContext {
	public FLOW(): TerminalNode { return this.getToken(FlowParser.FLOW, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_flow; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterFlow) {
			listener.enterFlow(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitFlow) {
			listener.exitFlow(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitFlow) {
			return visitor.visitFlow(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class DotContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_dot; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterDot) {
			listener.enterDot(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitDot) {
			listener.exitDot(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitDot) {
			return visitor.visitDot(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ThenContext extends ParserRuleContext {
	public THEN(): TerminalNode { return this.getToken(FlowParser.THEN, 0); }
	public lambda(): LambdaContext {
		return this.getRuleContext(0, LambdaContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_then; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterThen) {
			listener.enterThen(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitThen) {
			listener.exitThen(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitThen) {
			return visitor.visitThen(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ThenSetContext extends ParserRuleContext {
	public THENSET(): TerminalNode { return this.getToken(FlowParser.THENSET, 0); }
	public key(): KeyContext {
		return this.getRuleContext(0, KeyContext);
	}
	public lambda(): LambdaContext {
		return this.getRuleContext(0, LambdaContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_thenSet; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterThenSet) {
			listener.enterThenSet(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitThenSet) {
			listener.exitThenSet(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitThenSet) {
			return visitor.visitThenSet(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class KeyContext extends ParserRuleContext {
	public KEY(): TerminalNode { return this.getToken(FlowParser.KEY, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_key; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterKey) {
			listener.enterKey(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitKey) {
			listener.exitKey(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitKey) {
			return visitor.visitKey(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class NameContext extends ParserRuleContext {
	public NAME(): TerminalNode { return this.getToken(FlowParser.NAME, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_name; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterName) {
			listener.enterName(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitName) {
			listener.exitName(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitName) {
			return visitor.visitName(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class StringContext extends ParserRuleContext {
	public KEY(): TerminalNode | undefined { return this.tryGetToken(FlowParser.KEY, 0); }
	public STRING(): TerminalNode | undefined { return this.tryGetToken(FlowParser.STRING, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_string; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterString) {
			listener.enterString(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitString) {
			listener.exitString(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitString) {
			return visitor.visitString(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class LambdaContext extends ParserRuleContext {
	public name(): NameContext {
		return this.getRuleContext(0, NameContext);
	}
	public body(): BodyContext {
		return this.getRuleContext(0, BodyContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_lambda; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterLambda) {
			listener.enterLambda(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitLambda) {
			listener.exitLambda(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitLambda) {
			return visitor.visitLambda(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ModelGetContext extends ParserRuleContext {
	public MGET(): TerminalNode { return this.getToken(FlowParser.MGET, 0); }
	public key(): KeyContext {
		return this.getRuleContext(0, KeyContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_modelGet; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterModelGet) {
			listener.enterModelGet(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitModelGet) {
			listener.exitModelGet(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitModelGet) {
			return visitor.visitModelGet(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ServicesContext extends ParserRuleContext {
	public SERVICES(): TerminalNode { return this.getToken(FlowParser.SERVICES, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_services; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterServices) {
			listener.enterServices(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitServices) {
			listener.exitServices(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitServices) {
			return visitor.visitServices(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResultContext extends ParserRuleContext {
	public RESULT(): TerminalNode { return this.getToken(FlowParser.RESULT, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_result; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterResult) {
			listener.enterResult(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitResult) {
			listener.exitResult(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitResult) {
			return visitor.visitResult(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class BodyContext extends ParserRuleContext {
	public operation(): OperationContext | undefined {
		return this.tryGetRuleContext(0, OperationContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_body; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterBody) {
			listener.enterBody(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitBody) {
			listener.exitBody(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitBody) {
			return visitor.visitBody(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class PoperationContext extends ParserRuleContext {
	public operation(): OperationContext | undefined {
		return this.tryGetRuleContext(0, OperationContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_poperation; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterPoperation) {
			listener.enterPoperation(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitPoperation) {
			listener.exitPoperation(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitPoperation) {
			return visitor.visitPoperation(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class OperationContext extends ParserRuleContext {
	public modelGet(): ModelGetContext[];
	public modelGet(i: number): ModelGetContext;
	public modelGet(i?: number): ModelGetContext | ModelGetContext[] {
		if (i === undefined) {
			return this.getRuleContexts(ModelGetContext);
		} else {
			return this.getRuleContext(i, ModelGetContext);
		}
	}
	public services(): ServicesContext[];
	public services(i: number): ServicesContext;
	public services(i?: number): ServicesContext | ServicesContext[] {
		if (i === undefined) {
			return this.getRuleContexts(ServicesContext);
		} else {
			return this.getRuleContext(i, ServicesContext);
		}
	}
	public result(): ResultContext[];
	public result(i: number): ResultContext;
	public result(i?: number): ResultContext | ResultContext[] {
		if (i === undefined) {
			return this.getRuleContexts(ResultContext);
		} else {
			return this.getRuleContext(i, ResultContext);
		}
	}
	public string(): StringContext[];
	public string(i: number): StringContext;
	public string(i?: number): StringContext | StringContext[] {
		if (i === undefined) {
			return this.getRuleContexts(StringContext);
		} else {
			return this.getRuleContext(i, StringContext);
		}
	}
	public name(): NameContext[];
	public name(i: number): NameContext;
	public name(i?: number): NameContext | NameContext[] {
		if (i === undefined) {
			return this.getRuleContexts(NameContext);
		} else {
			return this.getRuleContext(i, NameContext);
		}
	}
	public SP(): TerminalNode[];
	public SP(i: number): TerminalNode;
	public SP(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(FlowParser.SP);
		} else {
			return this.getToken(FlowParser.SP, i);
		}
	}
	public dot(): DotContext[];
	public dot(i: number): DotContext;
	public dot(i?: number): DotContext | DotContext[] {
		if (i === undefined) {
			return this.getRuleContexts(DotContext);
		} else {
			return this.getRuleContext(i, DotContext);
		}
	}
	public poperation(): PoperationContext[];
	public poperation(i: number): PoperationContext;
	public poperation(i?: number): PoperationContext | PoperationContext[] {
		if (i === undefined) {
			return this.getRuleContexts(PoperationContext);
		} else {
			return this.getRuleContext(i, PoperationContext);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return FlowParser.RULE_operation; }
	// @Override
	public enterRule(listener: FlowListener): void {
		if (listener.enterOperation) {
			listener.enterOperation(this);
		}
	}
	// @Override
	public exitRule(listener: FlowListener): void {
		if (listener.exitOperation) {
			listener.exitOperation(this);
		}
	}
	// @Override
	public accept<Result>(visitor: FlowVisitor<Result>): Result {
		if (visitor.visitOperation) {
			return visitor.visitOperation(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


