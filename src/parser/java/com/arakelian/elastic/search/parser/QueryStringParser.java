// Generated from com/arakelian/elastic/search/parser/QueryStringParser.g4 by ANTLR 4.12.0

// @formatter:off
package com.arakelian.elastic.search.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class QueryStringParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LPAREN=1, RPAREN=2, LBRACK=3, RBRACK=4, COLON=5, PLUS=6, MINUS=7, STAR=8, 
		QMARK=9, LCURLY=10, RCURLY=11, CARAT=12, TILDE=13, DQUOTE=14, SQUOTE=15, 
		TO=16, AND=17, OR=18, NOT=19, WS=20, NUMBER=21, DATE_TOKEN=22, TERM_NORMAL=23, 
		TERM_TRUNCATED=24, PHRASE=25, PHRASE_ANYTHING=26, OPERATOR=27, ATOM=28, 
		MODIFIER=29, TMODIFIER=30, CLAUSE=31, FIELD=32, FUZZY=33, BOOST=34, QNORMAL=35, 
		QPHRASE=36, QPHRASETRUNC=37, QTRUNCATED=38, QRANGEIN=39, QRANGEEX=40, 
		QANYTHING=41, QDATE=42;
	public static final int
		RULE_mainQ = 0, RULE_clauseDefault = 1, RULE_clauseOr = 2, RULE_clauseAnd = 3, 
		RULE_clauseNot = 4, RULE_clauseBasic = 5, RULE_atom = 6, RULE_field = 7, 
		RULE_value = 8, RULE_anything = 9, RULE_two_sided_range_term = 10, RULE_range_term = 11, 
		RULE_range_value = 12, RULE_multi_value = 13, RULE_normal = 14, RULE_truncated = 15, 
		RULE_quoted_truncated = 16, RULE_quoted = 17, RULE_modifier = 18, RULE_term_modifier = 19, 
		RULE_boost = 20, RULE_fuzzy = 21, RULE_not_ = 22, RULE_and_ = 23, RULE_or_ = 24, 
		RULE_date = 25, RULE_sep = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"mainQ", "clauseDefault", "clauseOr", "clauseAnd", "clauseNot", "clauseBasic", 
			"atom", "field", "value", "anything", "two_sided_range_term", "range_term", 
			"range_value", "multi_value", "normal", "truncated", "quoted_truncated", 
			"quoted", "modifier", "term_modifier", "boost", "fuzzy", "not_", "and_", 
			"or_", "date", "sep"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'['", "']'", "':'", "'+'", null, "'*'", null, "'{'", 
			"'}'", null, null, "'\"'", "'''", "'TO'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LPAREN", "RPAREN", "LBRACK", "RBRACK", "COLON", "PLUS", "MINUS", 
			"STAR", "QMARK", "LCURLY", "RCURLY", "CARAT", "TILDE", "DQUOTE", "SQUOTE", 
			"TO", "AND", "OR", "NOT", "WS", "NUMBER", "DATE_TOKEN", "TERM_NORMAL", 
			"TERM_TRUNCATED", "PHRASE", "PHRASE_ANYTHING", "OPERATOR", "ATOM", "MODIFIER", 
			"TMODIFIER", "CLAUSE", "FIELD", "FUZZY", "BOOST", "QNORMAL", "QPHRASE", 
			"QPHRASETRUNC", "QTRUNCATED", "QRANGEIN", "QRANGEEX", "QANYTHING", "QDATE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "QueryStringParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QueryStringParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MainQContext extends ParserRuleContext {
		public ClauseDefaultContext clause;
		public TerminalNode EOF() { return getToken(QueryStringParser.EOF, 0); }
		public ClauseDefaultContext clauseDefault() {
			return getRuleContext(ClauseDefaultContext.class,0);
		}
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public MainQContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainQ; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterMainQ(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitMainQ(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitMainQ(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MainQContext mainQ() throws RecognitionException {
		MainQContext _localctx = new MainQContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_mainQ);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(54);
				sep();
				}
				break;
			}
			setState(57);
			((MainQContext)_localctx).clause = clauseDefault();
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(58);
				sep();
				}
			}

			setState(61);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClauseDefaultContext extends ParserRuleContext {
		public List<ClauseOrContext> clauseOr() {
			return getRuleContexts(ClauseOrContext.class);
		}
		public ClauseOrContext clauseOr(int i) {
			return getRuleContext(ClauseOrContext.class,i);
		}
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public ClauseDefaultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauseDefault; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterClauseDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitClauseDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitClauseDefault(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseDefaultContext clauseDefault() throws RecognitionException {
		ClauseDefaultContext _localctx = new ClauseDefaultContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_clauseDefault);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			clauseOr();
			setState(70);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(65);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						setState(64);
						sep();
						}
						break;
					}
					setState(67);
					clauseOr();
					}
					} 
				}
				setState(72);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClauseOrContext extends ParserRuleContext {
		public List<ClauseAndContext> clauseAnd() {
			return getRuleContexts(ClauseAndContext.class);
		}
		public ClauseAndContext clauseAnd(int i) {
			return getRuleContext(ClauseAndContext.class,i);
		}
		public List<Or_Context> or_() {
			return getRuleContexts(Or_Context.class);
		}
		public Or_Context or_(int i) {
			return getRuleContext(Or_Context.class,i);
		}
		public ClauseOrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauseOr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterClauseOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitClauseOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitClauseOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseOrContext clauseOr() throws RecognitionException {
		ClauseOrContext _localctx = new ClauseOrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_clauseOr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			clauseAnd();
			setState(79);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(74);
					or_();
					setState(75);
					clauseAnd();
					}
					} 
				}
				setState(81);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClauseAndContext extends ParserRuleContext {
		public List<ClauseNotContext> clauseNot() {
			return getRuleContexts(ClauseNotContext.class);
		}
		public ClauseNotContext clauseNot(int i) {
			return getRuleContext(ClauseNotContext.class,i);
		}
		public List<And_Context> and_() {
			return getRuleContexts(And_Context.class);
		}
		public And_Context and_(int i) {
			return getRuleContext(And_Context.class,i);
		}
		public ClauseAndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauseAnd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterClauseAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitClauseAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitClauseAnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseAndContext clauseAnd() throws RecognitionException {
		ClauseAndContext _localctx = new ClauseAndContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_clauseAnd);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			clauseNot();
			setState(88);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(83);
					and_();
					setState(84);
					clauseNot();
					}
					} 
				}
				setState(90);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClauseNotContext extends ParserRuleContext {
		public List<ClauseBasicContext> clauseBasic() {
			return getRuleContexts(ClauseBasicContext.class);
		}
		public ClauseBasicContext clauseBasic(int i) {
			return getRuleContext(ClauseBasicContext.class,i);
		}
		public List<Not_Context> not_() {
			return getRuleContexts(Not_Context.class);
		}
		public Not_Context not_(int i) {
			return getRuleContext(Not_Context.class,i);
		}
		public ClauseNotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauseNot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterClauseNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitClauseNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitClauseNot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseNotContext clauseNot() throws RecognitionException {
		ClauseNotContext _localctx = new ClauseNotContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_clauseNot);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			clauseBasic();
			setState(97);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(92);
					not_();
					setState(93);
					clauseBasic();
					}
					} 
				}
				setState(99);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClauseBasicContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(QueryStringParser.LPAREN, 0); }
		public ClauseDefaultContext clauseDefault() {
			return getRuleContext(ClauseDefaultContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryStringParser.RPAREN, 0); }
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public ModifierContext modifier() {
			return getRuleContext(ModifierContext.class,0);
		}
		public Term_modifierContext term_modifier() {
			return getRuleContext(Term_modifierContext.class,0);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public ClauseBasicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clauseBasic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterClauseBasic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitClauseBasic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitClauseBasic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClauseBasicContext clauseBasic() throws RecognitionException {
		ClauseBasicContext _localctx = new ClauseBasicContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_clauseBasic);
		int _la;
		try {
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(100);
					sep();
					}
				}

				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(103);
					modifier();
					}
				}

				setState(106);
				match(LPAREN);
				setState(107);
				clauseDefault();
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(108);
					sep();
					}
				}

				setState(111);
				match(RPAREN);
				setState(113);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CARAT || _la==TILDE) {
					{
					setState(112);
					term_modifier();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(115);
					sep();
					}
				}

				setState(118);
				atom();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public Multi_valueContext multi_value() {
			return getRuleContext(Multi_valueContext.class,0);
		}
		public ModifierContext modifier() {
			return getRuleContext(ModifierContext.class,0);
		}
		public Term_modifierContext term_modifier() {
			return getRuleContext(Term_modifierContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_atom);
		int _la;
		try {
			setState(139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(121);
					modifier();
					}
				}

				setState(124);
				field();
				setState(125);
				multi_value();
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CARAT || _la==TILDE) {
					{
					setState(126);
					term_modifier();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PLUS || _la==MINUS) {
					{
					setState(129);
					modifier();
					}
				}

				setState(133);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(132);
					field();
					}
					break;
				}
				setState(135);
				value();
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CARAT || _la==TILDE) {
					{
					setState(136);
					term_modifier();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldContext extends ParserRuleContext {
		public TerminalNode TERM_NORMAL() { return getToken(QueryStringParser.TERM_NORMAL, 0); }
		public TerminalNode COLON() { return getToken(QueryStringParser.COLON, 0); }
		public SepContext sep() {
			return getRuleContext(SepContext.class,0);
		}
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(TERM_NORMAL);
			setState(142);
			match(COLON);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(143);
				sep();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValueContext extends ParserRuleContext {
		public Range_termContext range_term() {
			return getRuleContext(Range_termContext.class,0);
		}
		public NormalContext normal() {
			return getRuleContext(NormalContext.class,0);
		}
		public TruncatedContext truncated() {
			return getRuleContext(TruncatedContext.class,0);
		}
		public QuotedContext quoted() {
			return getRuleContext(QuotedContext.class,0);
		}
		public Quoted_truncatedContext quoted_truncated() {
			return getRuleContext(Quoted_truncatedContext.class,0);
		}
		public TerminalNode QMARK() { return getToken(QueryStringParser.QMARK, 0); }
		public AnythingContext anything() {
			return getRuleContext(AnythingContext.class,0);
		}
		public TerminalNode STAR() { return getToken(QueryStringParser.STAR, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_value);
		try {
			setState(154);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				range_term();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				normal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(148);
				truncated();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(149);
				quoted();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(150);
				quoted_truncated();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(151);
				match(QMARK);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(152);
				anything();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(153);
				match(STAR);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AnythingContext extends ParserRuleContext {
		public List<TerminalNode> STAR() { return getTokens(QueryStringParser.STAR); }
		public TerminalNode STAR(int i) {
			return getToken(QueryStringParser.STAR, i);
		}
		public TerminalNode COLON() { return getToken(QueryStringParser.COLON, 0); }
		public AnythingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anything; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterAnything(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitAnything(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitAnything(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnythingContext anything() throws RecognitionException {
		AnythingContext _localctx = new AnythingContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_anything);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(STAR);
			setState(157);
			match(COLON);
			setState(158);
			match(STAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Two_sided_range_termContext extends ParserRuleContext {
		public Token start_type;
		public Range_valueContext a;
		public Range_valueContext b;
		public Token end_type;
		public TerminalNode LBRACK() { return getToken(QueryStringParser.LBRACK, 0); }
		public TerminalNode LCURLY() { return getToken(QueryStringParser.LCURLY, 0); }
		public TerminalNode RBRACK() { return getToken(QueryStringParser.RBRACK, 0); }
		public TerminalNode RCURLY() { return getToken(QueryStringParser.RCURLY, 0); }
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public List<Range_valueContext> range_value() {
			return getRuleContexts(Range_valueContext.class);
		}
		public Range_valueContext range_value(int i) {
			return getRuleContext(Range_valueContext.class,i);
		}
		public TerminalNode TO() { return getToken(QueryStringParser.TO, 0); }
		public Two_sided_range_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_two_sided_range_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterTwo_sided_range_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitTwo_sided_range_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitTwo_sided_range_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Two_sided_range_termContext two_sided_range_term() throws RecognitionException {
		Two_sided_range_termContext _localctx = new Two_sided_range_termContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_two_sided_range_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			((Two_sided_range_termContext)_localctx).start_type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==LBRACK || _la==LCURLY) ) {
				((Two_sided_range_termContext)_localctx).start_type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(161);
				sep();
				}
			}

			{
			setState(164);
			((Two_sided_range_termContext)_localctx).a = range_value();
			}
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(165);
				sep();
				}
				break;
			}
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 133234944L) != 0)) {
				{
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TO) {
					{
					setState(168);
					match(TO);
					}
				}

				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(171);
					sep();
					}
				}

				setState(174);
				((Two_sided_range_termContext)_localctx).b = range_value();
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(175);
					sep();
					}
				}

				}
			}

			setState(180);
			((Two_sided_range_termContext)_localctx).end_type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==RBRACK || _la==RCURLY) ) {
				((Two_sided_range_termContext)_localctx).end_type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Range_termContext extends ParserRuleContext {
		public Two_sided_range_termContext two_sided_range_term() {
			return getRuleContext(Two_sided_range_termContext.class,0);
		}
		public Range_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterRange_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitRange_term(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitRange_term(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Range_termContext range_term() throws RecognitionException {
		Range_termContext _localctx = new Range_termContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_range_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			two_sided_range_term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Range_valueContext extends ParserRuleContext {
		public TruncatedContext truncated() {
			return getRuleContext(TruncatedContext.class,0);
		}
		public QuotedContext quoted() {
			return getRuleContext(QuotedContext.class,0);
		}
		public Quoted_truncatedContext quoted_truncated() {
			return getRuleContext(Quoted_truncatedContext.class,0);
		}
		public DateContext date() {
			return getRuleContext(DateContext.class,0);
		}
		public NormalContext normal() {
			return getRuleContext(NormalContext.class,0);
		}
		public TerminalNode STAR() { return getToken(QueryStringParser.STAR, 0); }
		public Range_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterRange_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitRange_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitRange_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Range_valueContext range_value() throws RecognitionException {
		Range_valueContext _localctx = new Range_valueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_range_value);
		try {
			setState(190);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TERM_TRUNCATED:
				enterOuterAlt(_localctx, 1);
				{
				setState(184);
				truncated();
				}
				break;
			case PHRASE:
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				quoted();
				}
				break;
			case PHRASE_ANYTHING:
				enterOuterAlt(_localctx, 3);
				{
				setState(186);
				quoted_truncated();
				}
				break;
			case DATE_TOKEN:
				enterOuterAlt(_localctx, 4);
				{
				setState(187);
				date();
				}
				break;
			case NUMBER:
			case TERM_NORMAL:
				enterOuterAlt(_localctx, 5);
				{
				setState(188);
				normal();
				}
				break;
			case STAR:
				enterOuterAlt(_localctx, 6);
				{
				setState(189);
				match(STAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Multi_valueContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(QueryStringParser.LPAREN, 0); }
		public ClauseDefaultContext clauseDefault() {
			return getRuleContext(ClauseDefaultContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(QueryStringParser.RPAREN, 0); }
		public SepContext sep() {
			return getRuleContext(SepContext.class,0);
		}
		public Multi_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multi_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterMulti_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitMulti_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitMulti_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Multi_valueContext multi_value() throws RecognitionException {
		Multi_valueContext _localctx = new Multi_valueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_multi_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(LPAREN);
			setState(193);
			clauseDefault();
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(194);
				sep();
				}
			}

			setState(197);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalContext extends ParserRuleContext {
		public TerminalNode TERM_NORMAL() { return getToken(QueryStringParser.TERM_NORMAL, 0); }
		public TerminalNode NUMBER() { return getToken(QueryStringParser.NUMBER, 0); }
		public NormalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitNormal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NormalContext normal() throws RecognitionException {
		NormalContext _localctx = new NormalContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_normal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==TERM_NORMAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TruncatedContext extends ParserRuleContext {
		public TerminalNode TERM_TRUNCATED() { return getToken(QueryStringParser.TERM_TRUNCATED, 0); }
		public TruncatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_truncated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterTruncated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitTruncated(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitTruncated(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TruncatedContext truncated() throws RecognitionException {
		TruncatedContext _localctx = new TruncatedContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_truncated);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			match(TERM_TRUNCATED);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Quoted_truncatedContext extends ParserRuleContext {
		public TerminalNode PHRASE_ANYTHING() { return getToken(QueryStringParser.PHRASE_ANYTHING, 0); }
		public Quoted_truncatedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quoted_truncated; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterQuoted_truncated(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitQuoted_truncated(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitQuoted_truncated(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Quoted_truncatedContext quoted_truncated() throws RecognitionException {
		Quoted_truncatedContext _localctx = new Quoted_truncatedContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_quoted_truncated);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			match(PHRASE_ANYTHING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QuotedContext extends ParserRuleContext {
		public TerminalNode PHRASE() { return getToken(QueryStringParser.PHRASE, 0); }
		public QuotedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quoted; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterQuoted(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitQuoted(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitQuoted(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuotedContext quoted() throws RecognitionException {
		QuotedContext _localctx = new QuotedContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_quoted);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			match(PHRASE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ModifierContext extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(QueryStringParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(QueryStringParser.MINUS, 0); }
		public ModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitModifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModifierContext modifier() throws RecognitionException {
		ModifierContext _localctx = new ModifierContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_modifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			_la = _input.LA(1);
			if ( !(_la==PLUS || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Term_modifierContext extends ParserRuleContext {
		public BoostContext boost() {
			return getRuleContext(BoostContext.class,0);
		}
		public FuzzyContext fuzzy() {
			return getRuleContext(FuzzyContext.class,0);
		}
		public Term_modifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term_modifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterTerm_modifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitTerm_modifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitTerm_modifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Term_modifierContext term_modifier() throws RecognitionException {
		Term_modifierContext _localctx = new Term_modifierContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_term_modifier);
		int _la;
		try {
			setState(217);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CARAT:
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				boost();
				setState(211);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==TILDE) {
					{
					setState(210);
					fuzzy();
					}
				}

				}
				break;
			case TILDE:
				enterOuterAlt(_localctx, 2);
				{
				setState(213);
				fuzzy();
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CARAT) {
					{
					setState(214);
					boost();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoostContext extends ParserRuleContext {
		public TerminalNode CARAT() { return getToken(QueryStringParser.CARAT, 0); }
		public TerminalNode NUMBER() { return getToken(QueryStringParser.NUMBER, 0); }
		public BoostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boost; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterBoost(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitBoost(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitBoost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoostContext boost() throws RecognitionException {
		BoostContext _localctx = new BoostContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_boost);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(219);
			match(CARAT);
			}
			setState(221);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(220);
				match(NUMBER);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuzzyContext extends ParserRuleContext {
		public TerminalNode TILDE() { return getToken(QueryStringParser.TILDE, 0); }
		public TerminalNode NUMBER() { return getToken(QueryStringParser.NUMBER, 0); }
		public FuzzyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fuzzy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterFuzzy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitFuzzy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitFuzzy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuzzyContext fuzzy() throws RecognitionException {
		FuzzyContext _localctx = new FuzzyContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_fuzzy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(223);
			match(TILDE);
			}
			setState(225);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(224);
				match(NUMBER);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Not_Context extends ParserRuleContext {
		public TerminalNode AND() { return getToken(QueryStringParser.AND, 0); }
		public TerminalNode NOT() { return getToken(QueryStringParser.NOT, 0); }
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public Not_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterNot_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitNot_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitNot_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Not_Context not_() throws RecognitionException {
		Not_Context _localctx = new Not_Context(_ctx, getState());
		enterRule(_localctx, 44, RULE_not_);
		int _la;
		try {
			setState(239);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(227);
					sep();
					}
				}

				setState(230);
				match(AND);
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(231);
					sep();
					}
				}

				setState(234);
				match(NOT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==WS) {
					{
					setState(235);
					sep();
					}
				}

				setState(238);
				match(NOT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class And_Context extends ParserRuleContext {
		public TerminalNode AND() { return getToken(QueryStringParser.AND, 0); }
		public SepContext sep() {
			return getRuleContext(SepContext.class,0);
		}
		public And_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterAnd_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitAnd_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitAnd_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final And_Context and_() throws RecognitionException {
		And_Context _localctx = new And_Context(_ctx, getState());
		enterRule(_localctx, 46, RULE_and_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(241);
				sep();
				}
			}

			setState(244);
			match(AND);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Or_Context extends ParserRuleContext {
		public TerminalNode OR() { return getToken(QueryStringParser.OR, 0); }
		public SepContext sep() {
			return getRuleContext(SepContext.class,0);
		}
		public Or_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterOr_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitOr_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitOr_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Or_Context or_() throws RecognitionException {
		Or_Context _localctx = new Or_Context(_ctx, getState());
		enterRule(_localctx, 48, RULE_or_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WS) {
				{
				setState(246);
				sep();
				}
			}

			setState(249);
			match(OR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DateContext extends ParserRuleContext {
		public TerminalNode DATE_TOKEN() { return getToken(QueryStringParser.DATE_TOKEN, 0); }
		public DateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_date; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterDate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitDate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitDate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DateContext date() throws RecognitionException {
		DateContext _localctx = new DateContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_date);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(DATE_TOKEN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SepContext extends ParserRuleContext {
		public List<TerminalNode> WS() { return getTokens(QueryStringParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(QueryStringParser.WS, i);
		}
		public SepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).enterSep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryStringParserListener ) ((QueryStringParserListener)listener).exitSep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryStringParserVisitor ) return ((QueryStringParserVisitor<? extends T>)visitor).visitSep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SepContext sep() throws RecognitionException {
		SepContext _localctx = new SepContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_sep);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(254); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(253);
					match(WS);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(256); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001*\u0103\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0001\u0000\u0003\u0000"+
		"8\b\u0000\u0001\u0000\u0001\u0000\u0003\u0000<\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0003\u0001B\b\u0001\u0001\u0001\u0005"+
		"\u0001E\b\u0001\n\u0001\f\u0001H\t\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002N\b\u0002\n\u0002\f\u0002Q\t\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003W\b\u0003\n\u0003"+
		"\f\u0003Z\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005"+
		"\u0004`\b\u0004\n\u0004\f\u0004c\t\u0004\u0001\u0005\u0003\u0005f\b\u0005"+
		"\u0001\u0005\u0003\u0005i\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005n\b\u0005\u0001\u0005\u0001\u0005\u0003\u0005r\b\u0005\u0001"+
		"\u0005\u0003\u0005u\b\u0005\u0001\u0005\u0003\u0005x\b\u0005\u0001\u0006"+
		"\u0003\u0006{\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006"+
		"\u0080\b\u0006\u0001\u0006\u0003\u0006\u0083\b\u0006\u0001\u0006\u0003"+
		"\u0006\u0086\b\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u008a\b\u0006"+
		"\u0003\u0006\u008c\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		"\u0091\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0003\b\u009b\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0003\n\u00a3\b\n\u0001\n\u0001\n\u0003\n\u00a7\b\n\u0001\n\u0003\n"+
		"\u00aa\b\n\u0001\n\u0003\n\u00ad\b\n\u0001\n\u0001\n\u0003\n\u00b1\b\n"+
		"\u0003\n\u00b3\b\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u00bf\b\f\u0001\r\u0001\r\u0001"+
		"\r\u0003\r\u00c4\b\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0003\u0013\u00d4\b\u0013\u0001\u0013"+
		"\u0001\u0013\u0003\u0013\u00d8\b\u0013\u0003\u0013\u00da\b\u0013\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u00de\b\u0014\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u00e2\b\u0015\u0001\u0016\u0003\u0016\u00e5\b\u0016\u0001\u0016"+
		"\u0001\u0016\u0003\u0016\u00e9\b\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u00ed\b\u0016\u0001\u0016\u0003\u0016\u00f0\b\u0016\u0001\u0017\u0003"+
		"\u0017\u00f3\b\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0003\u0018\u00f8"+
		"\b\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0004"+
		"\u001a\u00ff\b\u001a\u000b\u001a\f\u001a\u0100\u0001\u001a\u0000\u0000"+
		"\u001b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.024\u0000\u0004\u0002\u0000\u0003\u0003\n\n"+
		"\u0002\u0000\u0004\u0004\u000b\u000b\u0002\u0000\u0015\u0015\u0017\u0017"+
		"\u0001\u0000\u0006\u0007\u011a\u00007\u0001\u0000\u0000\u0000\u0002?\u0001"+
		"\u0000\u0000\u0000\u0004I\u0001\u0000\u0000\u0000\u0006R\u0001\u0000\u0000"+
		"\u0000\b[\u0001\u0000\u0000\u0000\nw\u0001\u0000\u0000\u0000\f\u008b\u0001"+
		"\u0000\u0000\u0000\u000e\u008d\u0001\u0000\u0000\u0000\u0010\u009a\u0001"+
		"\u0000\u0000\u0000\u0012\u009c\u0001\u0000\u0000\u0000\u0014\u00a0\u0001"+
		"\u0000\u0000\u0000\u0016\u00b6\u0001\u0000\u0000\u0000\u0018\u00be\u0001"+
		"\u0000\u0000\u0000\u001a\u00c0\u0001\u0000\u0000\u0000\u001c\u00c7\u0001"+
		"\u0000\u0000\u0000\u001e\u00c9\u0001\u0000\u0000\u0000 \u00cb\u0001\u0000"+
		"\u0000\u0000\"\u00cd\u0001\u0000\u0000\u0000$\u00cf\u0001\u0000\u0000"+
		"\u0000&\u00d9\u0001\u0000\u0000\u0000(\u00db\u0001\u0000\u0000\u0000*"+
		"\u00df\u0001\u0000\u0000\u0000,\u00ef\u0001\u0000\u0000\u0000.\u00f2\u0001"+
		"\u0000\u0000\u00000\u00f7\u0001\u0000\u0000\u00002\u00fb\u0001\u0000\u0000"+
		"\u00004\u00fe\u0001\u0000\u0000\u000068\u00034\u001a\u000076\u0001\u0000"+
		"\u0000\u000078\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u00009;\u0003"+
		"\u0002\u0001\u0000:<\u00034\u001a\u0000;:\u0001\u0000\u0000\u0000;<\u0001"+
		"\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0005\u0000\u0000\u0001"+
		">\u0001\u0001\u0000\u0000\u0000?F\u0003\u0004\u0002\u0000@B\u00034\u001a"+
		"\u0000A@\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000"+
		"\u0000\u0000CE\u0003\u0004\u0002\u0000DA\u0001\u0000\u0000\u0000EH\u0001"+
		"\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000"+
		"G\u0003\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IO\u0003\u0006"+
		"\u0003\u0000JK\u00030\u0018\u0000KL\u0003\u0006\u0003\u0000LN\u0001\u0000"+
		"\u0000\u0000MJ\u0001\u0000\u0000\u0000NQ\u0001\u0000\u0000\u0000OM\u0001"+
		"\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000P\u0005\u0001\u0000\u0000"+
		"\u0000QO\u0001\u0000\u0000\u0000RX\u0003\b\u0004\u0000ST\u0003.\u0017"+
		"\u0000TU\u0003\b\u0004\u0000UW\u0001\u0000\u0000\u0000VS\u0001\u0000\u0000"+
		"\u0000WZ\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000"+
		"\u0000\u0000Y\u0007\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000"+
		"[a\u0003\n\u0005\u0000\\]\u0003,\u0016\u0000]^\u0003\n\u0005\u0000^`\u0001"+
		"\u0000\u0000\u0000_\\\u0001\u0000\u0000\u0000`c\u0001\u0000\u0000\u0000"+
		"a_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b\t\u0001\u0000\u0000"+
		"\u0000ca\u0001\u0000\u0000\u0000df\u00034\u001a\u0000ed\u0001\u0000\u0000"+
		"\u0000ef\u0001\u0000\u0000\u0000fh\u0001\u0000\u0000\u0000gi\u0003$\u0012"+
		"\u0000hg\u0001\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000ij\u0001\u0000"+
		"\u0000\u0000jk\u0005\u0001\u0000\u0000km\u0003\u0002\u0001\u0000ln\u0003"+
		"4\u001a\u0000ml\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0001"+
		"\u0000\u0000\u0000oq\u0005\u0002\u0000\u0000pr\u0003&\u0013\u0000qp\u0001"+
		"\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rx\u0001\u0000\u0000\u0000"+
		"su\u00034\u001a\u0000ts\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000"+
		"uv\u0001\u0000\u0000\u0000vx\u0003\f\u0006\u0000we\u0001\u0000\u0000\u0000"+
		"wt\u0001\u0000\u0000\u0000x\u000b\u0001\u0000\u0000\u0000y{\u0003$\u0012"+
		"\u0000zy\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{|\u0001\u0000"+
		"\u0000\u0000|}\u0003\u000e\u0007\u0000}\u007f\u0003\u001a\r\u0000~\u0080"+
		"\u0003&\u0013\u0000\u007f~\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000"+
		"\u0000\u0000\u0080\u008c\u0001\u0000\u0000\u0000\u0081\u0083\u0003$\u0012"+
		"\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000"+
		"\u0000\u0083\u0085\u0001\u0000\u0000\u0000\u0084\u0086\u0003\u000e\u0007"+
		"\u0000\u0085\u0084\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000"+
		"\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0089\u0003\u0010\b\u0000"+
		"\u0088\u008a\u0003&\u0013\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089"+
		"\u008a\u0001\u0000\u0000\u0000\u008a\u008c\u0001\u0000\u0000\u0000\u008b"+
		"z\u0001\u0000\u0000\u0000\u008b\u0082\u0001\u0000\u0000\u0000\u008c\r"+
		"\u0001\u0000\u0000\u0000\u008d\u008e\u0005\u0017\u0000\u0000\u008e\u0090"+
		"\u0005\u0005\u0000\u0000\u008f\u0091\u00034\u001a\u0000\u0090\u008f\u0001"+
		"\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u000f\u0001"+
		"\u0000\u0000\u0000\u0092\u009b\u0003\u0016\u000b\u0000\u0093\u009b\u0003"+
		"\u001c\u000e\u0000\u0094\u009b\u0003\u001e\u000f\u0000\u0095\u009b\u0003"+
		"\"\u0011\u0000\u0096\u009b\u0003 \u0010\u0000\u0097\u009b\u0005\t\u0000"+
		"\u0000\u0098\u009b\u0003\u0012\t\u0000\u0099\u009b\u0005\b\u0000\u0000"+
		"\u009a\u0092\u0001\u0000\u0000\u0000\u009a\u0093\u0001\u0000\u0000\u0000"+
		"\u009a\u0094\u0001\u0000\u0000\u0000\u009a\u0095\u0001\u0000\u0000\u0000"+
		"\u009a\u0096\u0001\u0000\u0000\u0000\u009a\u0097\u0001\u0000\u0000\u0000"+
		"\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u0099\u0001\u0000\u0000\u0000"+
		"\u009b\u0011\u0001\u0000\u0000\u0000\u009c\u009d\u0005\b\u0000\u0000\u009d"+
		"\u009e\u0005\u0005\u0000\u0000\u009e\u009f\u0005\b\u0000\u0000\u009f\u0013"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a2\u0007\u0000\u0000\u0000\u00a1\u00a3"+
		"\u00034\u001a\u0000\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a6\u0003"+
		"\u0018\f\u0000\u00a5\u00a7\u00034\u001a\u0000\u00a6\u00a5\u0001\u0000"+
		"\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00b2\u0001\u0000"+
		"\u0000\u0000\u00a8\u00aa\u0005\u0010\u0000\u0000\u00a9\u00a8\u0001\u0000"+
		"\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00ac\u0001\u0000"+
		"\u0000\u0000\u00ab\u00ad\u00034\u001a\u0000\u00ac\u00ab\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b0\u0003\u0018\f\u0000\u00af\u00b1\u00034\u001a\u0000"+
		"\u00b0\u00af\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000"+
		"\u00b1\u00b3\u0001\u0000\u0000\u0000\u00b2\u00a9\u0001\u0000\u0000\u0000"+
		"\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000"+
		"\u00b4\u00b5\u0007\u0001\u0000\u0000\u00b5\u0015\u0001\u0000\u0000\u0000"+
		"\u00b6\u00b7\u0003\u0014\n\u0000\u00b7\u0017\u0001\u0000\u0000\u0000\u00b8"+
		"\u00bf\u0003\u001e\u000f\u0000\u00b9\u00bf\u0003\"\u0011\u0000\u00ba\u00bf"+
		"\u0003 \u0010\u0000\u00bb\u00bf\u00032\u0019\u0000\u00bc\u00bf\u0003\u001c"+
		"\u000e\u0000\u00bd\u00bf\u0005\b\u0000\u0000\u00be\u00b8\u0001\u0000\u0000"+
		"\u0000\u00be\u00b9\u0001\u0000\u0000\u0000\u00be\u00ba\u0001\u0000\u0000"+
		"\u0000\u00be\u00bb\u0001\u0000\u0000\u0000\u00be\u00bc\u0001\u0000\u0000"+
		"\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00bf\u0019\u0001\u0000\u0000"+
		"\u0000\u00c0\u00c1\u0005\u0001\u0000\u0000\u00c1\u00c3\u0003\u0002\u0001"+
		"\u0000\u00c2\u00c4\u00034\u001a\u0000\u00c3\u00c2\u0001\u0000\u0000\u0000"+
		"\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000"+
		"\u00c5\u00c6\u0005\u0002\u0000\u0000\u00c6\u001b\u0001\u0000\u0000\u0000"+
		"\u00c7\u00c8\u0007\u0002\u0000\u0000\u00c8\u001d\u0001\u0000\u0000\u0000"+
		"\u00c9\u00ca\u0005\u0018\u0000\u0000\u00ca\u001f\u0001\u0000\u0000\u0000"+
		"\u00cb\u00cc\u0005\u001a\u0000\u0000\u00cc!\u0001\u0000\u0000\u0000\u00cd"+
		"\u00ce\u0005\u0019\u0000\u0000\u00ce#\u0001\u0000\u0000\u0000\u00cf\u00d0"+
		"\u0007\u0003\u0000\u0000\u00d0%\u0001\u0000\u0000\u0000\u00d1\u00d3\u0003"+
		"(\u0014\u0000\u00d2\u00d4\u0003*\u0015\u0000\u00d3\u00d2\u0001\u0000\u0000"+
		"\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00da\u0001\u0000\u0000"+
		"\u0000\u00d5\u00d7\u0003*\u0015\u0000\u00d6\u00d8\u0003(\u0014\u0000\u00d7"+
		"\u00d6\u0001\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8"+
		"\u00da\u0001\u0000\u0000\u0000\u00d9\u00d1\u0001\u0000\u0000\u0000\u00d9"+
		"\u00d5\u0001\u0000\u0000\u0000\u00da\'\u0001\u0000\u0000\u0000\u00db\u00dd"+
		"\u0005\f\u0000\u0000\u00dc\u00de\u0005\u0015\u0000\u0000\u00dd\u00dc\u0001"+
		"\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000\u0000\u00de)\u0001\u0000"+
		"\u0000\u0000\u00df\u00e1\u0005\r\u0000\u0000\u00e0\u00e2\u0005\u0015\u0000"+
		"\u0000\u00e1\u00e0\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000"+
		"\u0000\u00e2+\u0001\u0000\u0000\u0000\u00e3\u00e5\u00034\u001a\u0000\u00e4"+
		"\u00e3\u0001\u0000\u0000\u0000\u00e4\u00e5\u0001\u0000\u0000\u0000\u00e5"+
		"\u00e6\u0001\u0000\u0000\u0000\u00e6\u00e8\u0005\u0011\u0000\u0000\u00e7"+
		"\u00e9\u00034\u001a\u0000\u00e8\u00e7\u0001\u0000\u0000\u0000\u00e8\u00e9"+
		"\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001\u0000\u0000\u0000\u00ea\u00f0"+
		"\u0005\u0013\u0000\u0000\u00eb\u00ed\u00034\u001a\u0000\u00ec\u00eb\u0001"+
		"\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000\u0000\u0000\u00ed\u00ee\u0001"+
		"\u0000\u0000\u0000\u00ee\u00f0\u0005\u0013\u0000\u0000\u00ef\u00e4\u0001"+
		"\u0000\u0000\u0000\u00ef\u00ec\u0001\u0000\u0000\u0000\u00f0-\u0001\u0000"+
		"\u0000\u0000\u00f1\u00f3\u00034\u001a\u0000\u00f2\u00f1\u0001\u0000\u0000"+
		"\u0000\u00f2\u00f3\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000"+
		"\u0000\u00f4\u00f5\u0005\u0011\u0000\u0000\u00f5/\u0001\u0000\u0000\u0000"+
		"\u00f6\u00f8\u00034\u001a\u0000\u00f7\u00f6\u0001\u0000\u0000\u0000\u00f7"+
		"\u00f8\u0001\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000\u0000\u0000\u00f9"+
		"\u00fa\u0005\u0012\u0000\u0000\u00fa1\u0001\u0000\u0000\u0000\u00fb\u00fc"+
		"\u0005\u0016\u0000\u0000\u00fc3\u0001\u0000\u0000\u0000\u00fd\u00ff\u0005"+
		"\u0014\u0000\u0000\u00fe\u00fd\u0001\u0000\u0000\u0000\u00ff\u0100\u0001"+
		"\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0100\u0101\u0001"+
		"\u0000\u0000\u0000\u01015\u0001\u0000\u0000\u0000)7;AFOXaehmqtwz\u007f"+
		"\u0082\u0085\u0089\u008b\u0090\u009a\u00a2\u00a6\u00a9\u00ac\u00b0\u00b2"+
		"\u00be\u00c3\u00d3\u00d7\u00d9\u00dd\u00e1\u00e4\u00e8\u00ec\u00ef\u00f2"+
		"\u00f7\u0100";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}