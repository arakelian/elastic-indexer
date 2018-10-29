// Generated from com/arakelian/elastic/search/parser/QueryStringParser.g4 by ANTLR 4.7.1

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

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryStringParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

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
	public static final String[] ruleNames = {
		"mainQ", "clauseDefault", "clauseOr", "clauseAnd", "clauseNot", "clauseBasic", 
		"atom", "field", "value", "anything", "two_sided_range_term", "range_term", 
		"range_value", "multi_value", "normal", "truncated", "quoted_truncated", 
		"quoted", "modifier", "term_modifier", "boost", "fuzzy", "not_", "and_", 
		"or_", "date", "sep"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'['", "']'", "':'", "'+'", null, "'*'", null, "'{'", 
		"'}'", null, null, "'\"'", "'''", "'TO'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "LPAREN", "RPAREN", "LBRACK", "RBRACK", "COLON", "PLUS", "MINUS", 
		"STAR", "QMARK", "LCURLY", "RCURLY", "CARAT", "TILDE", "DQUOTE", "SQUOTE", 
		"TO", "AND", "OR", "NOT", "WS", "NUMBER", "DATE_TOKEN", "TERM_NORMAL", 
		"TERM_TRUNCATED", "PHRASE", "PHRASE_ANYTHING", "OPERATOR", "ATOM", "MODIFIER", 
		"TMODIFIER", "CLAUSE", "FIELD", "FUZZY", "BOOST", "QNORMAL", "QPHRASE", 
		"QPHRASETRUNC", "QTRUNCATED", "QRANGEIN", "QRANGEEX", "QANYTHING", "QDATE"
	};
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
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << TO) | (1L << WS) | (1L << NUMBER) | (1L << DATE_TOKEN) | (1L << TERM_NORMAL) | (1L << TERM_TRUNCATED) | (1L << PHRASE) | (1L << PHRASE_ANYTHING))) != 0)) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3,\u0105\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\5\2:\n\2\3\2\3\2\5\2>\n\2\3\2\3\2\3"+
		"\3\3\3\5\3D\n\3\3\3\7\3G\n\3\f\3\16\3J\13\3\3\4\3\4\3\4\3\4\7\4P\n\4\f"+
		"\4\16\4S\13\4\3\5\3\5\3\5\3\5\7\5Y\n\5\f\5\16\5\\\13\5\3\6\3\6\3\6\3\6"+
		"\7\6b\n\6\f\6\16\6e\13\6\3\7\5\7h\n\7\3\7\5\7k\n\7\3\7\3\7\3\7\5\7p\n"+
		"\7\3\7\3\7\5\7t\n\7\3\7\5\7w\n\7\3\7\5\7z\n\7\3\b\5\b}\n\b\3\b\3\b\3\b"+
		"\5\b\u0082\n\b\3\b\5\b\u0085\n\b\3\b\5\b\u0088\n\b\3\b\3\b\5\b\u008c\n"+
		"\b\5\b\u008e\n\b\3\t\3\t\3\t\5\t\u0093\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\5\n\u009d\n\n\3\13\3\13\3\13\3\13\3\f\3\f\5\f\u00a5\n\f\3\f\3\f\5"+
		"\f\u00a9\n\f\3\f\5\f\u00ac\n\f\3\f\5\f\u00af\n\f\3\f\3\f\5\f\u00b3\n\f"+
		"\5\f\u00b5\n\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00c1"+
		"\n\16\3\17\3\17\3\17\5\17\u00c6\n\17\3\17\3\17\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\5\25\u00d6\n\25\3\25\3\25\5\25\u00da"+
		"\n\25\5\25\u00dc\n\25\3\26\3\26\5\26\u00e0\n\26\3\27\3\27\5\27\u00e4\n"+
		"\27\3\30\5\30\u00e7\n\30\3\30\3\30\5\30\u00eb\n\30\3\30\3\30\5\30\u00ef"+
		"\n\30\3\30\5\30\u00f2\n\30\3\31\5\31\u00f5\n\31\3\31\3\31\3\32\5\32\u00fa"+
		"\n\32\3\32\3\32\3\33\3\33\3\34\6\34\u0101\n\34\r\34\16\34\u0102\3\34\2"+
		"\2\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66\2\6"+
		"\4\2\5\5\f\f\4\2\6\6\r\r\4\2\27\27\31\31\3\2\b\t\2\u011c\29\3\2\2\2\4"+
		"A\3\2\2\2\6K\3\2\2\2\bT\3\2\2\2\n]\3\2\2\2\fy\3\2\2\2\16\u008d\3\2\2\2"+
		"\20\u008f\3\2\2\2\22\u009c\3\2\2\2\24\u009e\3\2\2\2\26\u00a2\3\2\2\2\30"+
		"\u00b8\3\2\2\2\32\u00c0\3\2\2\2\34\u00c2\3\2\2\2\36\u00c9\3\2\2\2 \u00cb"+
		"\3\2\2\2\"\u00cd\3\2\2\2$\u00cf\3\2\2\2&\u00d1\3\2\2\2(\u00db\3\2\2\2"+
		"*\u00dd\3\2\2\2,\u00e1\3\2\2\2.\u00f1\3\2\2\2\60\u00f4\3\2\2\2\62\u00f9"+
		"\3\2\2\2\64\u00fd\3\2\2\2\66\u0100\3\2\2\28:\5\66\34\298\3\2\2\29:\3\2"+
		"\2\2:;\3\2\2\2;=\5\4\3\2<>\5\66\34\2=<\3\2\2\2=>\3\2\2\2>?\3\2\2\2?@\7"+
		"\2\2\3@\3\3\2\2\2AH\5\6\4\2BD\5\66\34\2CB\3\2\2\2CD\3\2\2\2DE\3\2\2\2"+
		"EG\5\6\4\2FC\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2I\5\3\2\2\2JH\3\2\2"+
		"\2KQ\5\b\5\2LM\5\62\32\2MN\5\b\5\2NP\3\2\2\2OL\3\2\2\2PS\3\2\2\2QO\3\2"+
		"\2\2QR\3\2\2\2R\7\3\2\2\2SQ\3\2\2\2TZ\5\n\6\2UV\5\60\31\2VW\5\n\6\2WY"+
		"\3\2\2\2XU\3\2\2\2Y\\\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2[\t\3\2\2\2\\Z\3\2\2"+
		"\2]c\5\f\7\2^_\5.\30\2_`\5\f\7\2`b\3\2\2\2a^\3\2\2\2be\3\2\2\2ca\3\2\2"+
		"\2cd\3\2\2\2d\13\3\2\2\2ec\3\2\2\2fh\5\66\34\2gf\3\2\2\2gh\3\2\2\2hj\3"+
		"\2\2\2ik\5&\24\2ji\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\7\3\2\2mo\5\4\3\2np\5"+
		"\66\34\2on\3\2\2\2op\3\2\2\2pq\3\2\2\2qs\7\4\2\2rt\5(\25\2sr\3\2\2\2s"+
		"t\3\2\2\2tz\3\2\2\2uw\5\66\34\2vu\3\2\2\2vw\3\2\2\2wx\3\2\2\2xz\5\16\b"+
		"\2yg\3\2\2\2yv\3\2\2\2z\r\3\2\2\2{}\5&\24\2|{\3\2\2\2|}\3\2\2\2}~\3\2"+
		"\2\2~\177\5\20\t\2\177\u0081\5\34\17\2\u0080\u0082\5(\25\2\u0081\u0080"+
		"\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u008e\3\2\2\2\u0083\u0085\5&\24\2\u0084"+
		"\u0083\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0088\5\20"+
		"\t\2\u0087\u0086\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u008b\5\22\n\2\u008a\u008c\5(\25\2\u008b\u008a\3\2\2\2\u008b\u008c\3"+
		"\2\2\2\u008c\u008e\3\2\2\2\u008d|\3\2\2\2\u008d\u0084\3\2\2\2\u008e\17"+
		"\3\2\2\2\u008f\u0090\7\31\2\2\u0090\u0092\7\7\2\2\u0091\u0093\5\66\34"+
		"\2\u0092\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\21\3\2\2\2\u0094\u009d"+
		"\5\30\r\2\u0095\u009d\5\36\20\2\u0096\u009d\5 \21\2\u0097\u009d\5$\23"+
		"\2\u0098\u009d\5\"\22\2\u0099\u009d\7\13\2\2\u009a\u009d\5\24\13\2\u009b"+
		"\u009d\7\n\2\2\u009c\u0094\3\2\2\2\u009c\u0095\3\2\2\2\u009c\u0096\3\2"+
		"\2\2\u009c\u0097\3\2\2\2\u009c\u0098\3\2\2\2\u009c\u0099\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009b\3\2\2\2\u009d\23\3\2\2\2\u009e\u009f\7\n\2"+
		"\2\u009f\u00a0\7\7\2\2\u00a0\u00a1\7\n\2\2\u00a1\25\3\2\2\2\u00a2\u00a4"+
		"\t\2\2\2\u00a3\u00a5\5\66\34\2\u00a4\u00a3\3\2\2\2\u00a4\u00a5\3\2\2\2"+
		"\u00a5\u00a6\3\2\2\2\u00a6\u00a8\5\32\16\2\u00a7\u00a9\5\66\34\2\u00a8"+
		"\u00a7\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00b4\3\2\2\2\u00aa\u00ac\7\22"+
		"\2\2\u00ab\u00aa\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ae\3\2\2\2\u00ad"+
		"\u00af\5\66\34\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b0\3"+
		"\2\2\2\u00b0\u00b2\5\32\16\2\u00b1\u00b3\5\66\34\2\u00b2\u00b1\3\2\2\2"+
		"\u00b2\u00b3\3\2\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00ab\3\2\2\2\u00b4\u00b5"+
		"\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\t\3\2\2\u00b7\27\3\2\2\2\u00b8"+
		"\u00b9\5\26\f\2\u00b9\31\3\2\2\2\u00ba\u00c1\5 \21\2\u00bb\u00c1\5$\23"+
		"\2\u00bc\u00c1\5\"\22\2\u00bd\u00c1\5\64\33\2\u00be\u00c1\5\36\20\2\u00bf"+
		"\u00c1\7\n\2\2\u00c0\u00ba\3\2\2\2\u00c0\u00bb\3\2\2\2\u00c0\u00bc\3\2"+
		"\2\2\u00c0\u00bd\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1"+
		"\33\3\2\2\2\u00c2\u00c3\7\3\2\2\u00c3\u00c5\5\4\3\2\u00c4\u00c6\5\66\34"+
		"\2\u00c5\u00c4\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c8"+
		"\7\4\2\2\u00c8\35\3\2\2\2\u00c9\u00ca\t\4\2\2\u00ca\37\3\2\2\2\u00cb\u00cc"+
		"\7\32\2\2\u00cc!\3\2\2\2\u00cd\u00ce\7\34\2\2\u00ce#\3\2\2\2\u00cf\u00d0"+
		"\7\33\2\2\u00d0%\3\2\2\2\u00d1\u00d2\t\5\2\2\u00d2\'\3\2\2\2\u00d3\u00d5"+
		"\5*\26\2\u00d4\u00d6\5,\27\2\u00d5\u00d4\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6"+
		"\u00dc\3\2\2\2\u00d7\u00d9\5,\27\2\u00d8\u00da\5*\26\2\u00d9\u00d8\3\2"+
		"\2\2\u00d9\u00da\3\2\2\2\u00da\u00dc\3\2\2\2\u00db\u00d3\3\2\2\2\u00db"+
		"\u00d7\3\2\2\2\u00dc)\3\2\2\2\u00dd\u00df\7\16\2\2\u00de\u00e0\7\27\2"+
		"\2\u00df\u00de\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0+\3\2\2\2\u00e1\u00e3"+
		"\7\17\2\2\u00e2\u00e4\7\27\2\2\u00e3\u00e2\3\2\2\2\u00e3\u00e4\3\2\2\2"+
		"\u00e4-\3\2\2\2\u00e5\u00e7\5\66\34\2\u00e6\u00e5\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00ea\7\23\2\2\u00e9\u00eb\5\66\34"+
		"\2\u00ea\u00e9\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00f2"+
		"\7\25\2\2\u00ed\u00ef\5\66\34\2\u00ee\u00ed\3\2\2\2\u00ee\u00ef\3\2\2"+
		"\2\u00ef\u00f0\3\2\2\2\u00f0\u00f2\7\25\2\2\u00f1\u00e6\3\2\2\2\u00f1"+
		"\u00ee\3\2\2\2\u00f2/\3\2\2\2\u00f3\u00f5\5\66\34\2\u00f4\u00f3\3\2\2"+
		"\2\u00f4\u00f5\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f7\7\23\2\2\u00f7"+
		"\61\3\2\2\2\u00f8\u00fa\5\66\34\2\u00f9\u00f8\3\2\2\2\u00f9\u00fa\3\2"+
		"\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fc\7\24\2\2\u00fc\63\3\2\2\2\u00fd\u00fe"+
		"\7\30\2\2\u00fe\65\3\2\2\2\u00ff\u0101\7\26\2\2\u0100\u00ff\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\67\3\2\2"+
		"\2+9=CHQZcgjosvy|\u0081\u0084\u0087\u008b\u008d\u0092\u009c\u00a4\u00a8"+
		"\u00ab\u00ae\u00b2\u00b4\u00c0\u00c5\u00d5\u00d9\u00db\u00df\u00e3\u00e6"+
		"\u00ea\u00ee\u00f1\u00f4\u00f9\u0102";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}