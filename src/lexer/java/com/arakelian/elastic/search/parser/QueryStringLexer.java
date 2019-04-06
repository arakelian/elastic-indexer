// Generated from com/arakelian/elastic/search/parser/QueryStringLexer.g4 by ANTLR 4.7.2

// @formatter:off
package com.arakelian.elastic.search.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryStringLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LPAREN=1, RPAREN=2, LBRACK=3, RBRACK=4, COLON=5, PLUS=6, MINUS=7, STAR=8, 
		QMARK=9, LCURLY=10, RCURLY=11, CARAT=12, TILDE=13, DQUOTE=14, SQUOTE=15, 
		TO=16, AND=17, OR=18, NOT=19, WS=20, NUMBER=21, DATE_TOKEN=22, TERM_NORMAL=23, 
		TERM_TRUNCATED=24, PHRASE=25, PHRASE_ANYTHING=26;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "COLON", "PLUS", "MINUS", "STAR", 
			"QMARK", "VBAR", "AMPER", "LCURLY", "RCURLY", "CARAT", "TILDE", "DQUOTE", 
			"SQUOTE", "TO", "AND", "OR", "NOT", "WS", "INT", "ESC_CHAR", "TERM_START_CHAR", 
			"TERM_CHAR", "NUMBER", "DATE_TOKEN", "TERM_NORMAL", "TERM_TRUNCATED", 
			"PHRASE", "PHRASE_ANYTHING"
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
			"TERM_TRUNCATED", "PHRASE", "PHRASE_ANYTHING"
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


	public QueryStringLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QueryStringLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\34\u0119\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3"+
		"\t\3\n\6\nU\n\n\r\n\16\nV\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\6\17c\n\17\r\17\16\17d\3\17\3\17\6\17i\n\17\r\17\16\17j\5\17m\n\17\5"+
		"\17o\n\17\3\20\3\20\6\20s\n\20\r\20\16\20t\3\20\3\20\6\20y\n\20\r\20\16"+
		"\20z\5\20}\n\20\5\20\177\n\20\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\5\24\u008d\n\24\5\24\u008f\n\24\3\25\3\25\3\25\3"+
		"\25\5\25\u0095\n\25\5\25\u0097\n\25\3\26\3\26\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\31\3\32\3\32\5\32\u00a6\n\32\3\33\3\33\5\33\u00aa\n"+
		"\33\3\34\6\34\u00ad\n\34\r\34\16\34\u00ae\3\34\3\34\6\34\u00b3\n\34\r"+
		"\34\16\34\u00b4\5\34\u00b7\n\34\3\35\3\35\5\35\u00bb\n\35\3\35\3\35\3"+
		"\35\5\35\u00c0\n\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u00c8\n\35\3\36"+
		"\3\36\7\36\u00cc\n\36\f\36\16\36\u00cf\13\36\3\37\3\37\5\37\u00d3\n\37"+
		"\3\37\6\37\u00d6\n\37\r\37\16\37\u00d7\3\37\3\37\5\37\u00dc\n\37\6\37"+
		"\u00de\n\37\r\37\16\37\u00df\3\37\7\37\u00e3\n\37\f\37\16\37\u00e6\13"+
		"\37\3\37\3\37\7\37\u00ea\n\37\f\37\16\37\u00ed\13\37\3\37\3\37\5\37\u00f1"+
		"\n\37\6\37\u00f3\n\37\r\37\16\37\u00f4\3\37\7\37\u00f8\n\37\f\37\16\37"+
		"\u00fb\13\37\3\37\3\37\5\37\u00ff\n\37\3\37\6\37\u0102\n\37\r\37\16\37"+
		"\u0103\5\37\u0106\n\37\3 \3 \3 \6 \u010b\n \r \16 \u010c\3 \3 \3!\3!\3"+
		"!\6!\u0114\n!\r!\16!\u0115\3!\3!\2\2\"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\2\27\2\31\f\33\r\35\16\37\17!\20#\21%\22\'\23)\24+\25-\26"+
		"/\2\61\2\63\2\65\2\67\279\30;\31=\32?\33A\34\3\2\16\4\2##//\4\2CCcc\4"+
		"\2PPpp\4\2FFff\4\2QQqq\4\2TTtt\4\2VVvv\6\2\13\f\17\17\"\"\u3002\u3002"+
		"\r\2\13\f\17\17\"$)-//<<AA]`}}\177\u0080\u3002\u3002\4\2--//\6\2$$,,A"+
		"A^^\4\2$$^^\2\u0139\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\3C\3\2\2\2\5E\3"+
		"\2\2\2\7G\3\2\2\2\tI\3\2\2\2\13K\3\2\2\2\rM\3\2\2\2\17O\3\2\2\2\21Q\3"+
		"\2\2\2\23T\3\2\2\2\25X\3\2\2\2\27Z\3\2\2\2\31\\\3\2\2\2\33^\3\2\2\2\35"+
		"`\3\2\2\2\37p\3\2\2\2!\u0080\3\2\2\2#\u0082\3\2\2\2%\u0084\3\2\2\2\'\u008e"+
		"\3\2\2\2)\u0096\3\2\2\2+\u0098\3\2\2\2-\u009c\3\2\2\2/\u009e\3\2\2\2\61"+
		"\u00a0\3\2\2\2\63\u00a5\3\2\2\2\65\u00a9\3\2\2\2\67\u00ac\3\2\2\29\u00b8"+
		"\3\2\2\2;\u00c9\3\2\2\2=\u0105\3\2\2\2?\u0107\3\2\2\2A\u0110\3\2\2\2C"+
		"D\7*\2\2D\4\3\2\2\2EF\7+\2\2F\6\3\2\2\2GH\7]\2\2H\b\3\2\2\2IJ\7_\2\2J"+
		"\n\3\2\2\2KL\7<\2\2L\f\3\2\2\2MN\7-\2\2N\16\3\2\2\2OP\t\2\2\2P\20\3\2"+
		"\2\2QR\7,\2\2R\22\3\2\2\2SU\7A\2\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2"+
		"\2\2W\24\3\2\2\2XY\7~\2\2Y\26\3\2\2\2Z[\7(\2\2[\30\3\2\2\2\\]\7}\2\2]"+
		"\32\3\2\2\2^_\7\177\2\2_\34\3\2\2\2`n\7`\2\2ac\5/\30\2ba\3\2\2\2cd\3\2"+
		"\2\2db\3\2\2\2de\3\2\2\2el\3\2\2\2fh\7\60\2\2gi\5/\30\2hg\3\2\2\2ij\3"+
		"\2\2\2jh\3\2\2\2jk\3\2\2\2km\3\2\2\2lf\3\2\2\2lm\3\2\2\2mo\3\2\2\2nb\3"+
		"\2\2\2no\3\2\2\2o\36\3\2\2\2p~\7\u0080\2\2qs\5/\30\2rq\3\2\2\2st\3\2\2"+
		"\2tr\3\2\2\2tu\3\2\2\2u|\3\2\2\2vx\7\60\2\2wy\5/\30\2xw\3\2\2\2yz\3\2"+
		"\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|v\3\2\2\2|}\3\2\2\2}\177\3\2\2\2~r"+
		"\3\2\2\2~\177\3\2\2\2\177 \3\2\2\2\u0080\u0081\7$\2\2\u0081\"\3\2\2\2"+
		"\u0082\u0083\7)\2\2\u0083$\3\2\2\2\u0084\u0085\7V\2\2\u0085\u0086\7Q\2"+
		"\2\u0086&\3\2\2\2\u0087\u0088\t\3\2\2\u0088\u0089\t\4\2\2\u0089\u008f"+
		"\t\5\2\2\u008a\u008c\5\27\f\2\u008b\u008d\5\27\f\2\u008c\u008b\3\2\2\2"+
		"\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e\u0087\3\2\2\2\u008e\u008a"+
		"\3\2\2\2\u008f(\3\2\2\2\u0090\u0091\t\6\2\2\u0091\u0097\t\7\2\2\u0092"+
		"\u0094\5\25\13\2\u0093\u0095\5\25\13\2\u0094\u0093\3\2\2\2\u0094\u0095"+
		"\3\2\2\2\u0095\u0097\3\2\2\2\u0096\u0090\3\2\2\2\u0096\u0092\3\2\2\2\u0097"+
		"*\3\2\2\2\u0098\u0099\t\4\2\2\u0099\u009a\t\6\2\2\u009a\u009b\t\b\2\2"+
		"\u009b,\3\2\2\2\u009c\u009d\t\t\2\2\u009d.\3\2\2\2\u009e\u009f\4\62;\2"+
		"\u009f\60\3\2\2\2\u00a0\u00a1\7^\2\2\u00a1\u00a2\13\2\2\2\u00a2\62\3\2"+
		"\2\2\u00a3\u00a6\n\n\2\2\u00a4\u00a6\5\61\31\2\u00a5\u00a3\3\2\2\2\u00a5"+
		"\u00a4\3\2\2\2\u00a6\64\3\2\2\2\u00a7\u00aa\5\63\32\2\u00a8\u00aa\t\13"+
		"\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00a8\3\2\2\2\u00aa\66\3\2\2\2\u00ab\u00ad"+
		"\5/\30\2\u00ac\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae"+
		"\u00af\3\2\2\2\u00af\u00b6\3\2\2\2\u00b0\u00b2\7\60\2\2\u00b1\u00b3\5"+
		"/\30\2\u00b2\u00b1\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00b7\3\2\2\2\u00b6\u00b0\3\2\2\2\u00b6\u00b7\3\2"+
		"\2\2\u00b78\3\2\2\2\u00b8\u00ba\5/\30\2\u00b9\u00bb\5/\30\2\u00ba\u00b9"+
		"\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\4/\61\2\u00bd"+
		"\u00bf\5/\30\2\u00be\u00c0\5/\30\2\u00bf\u00be\3\2\2\2\u00bf\u00c0\3\2"+
		"\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\4/\61\2\u00c2\u00c3\5/\30\2\u00c3"+
		"\u00c7\5/\30\2\u00c4\u00c5\5/\30\2\u00c5\u00c6\5/\30\2\u00c6\u00c8\3\2"+
		"\2\2\u00c7\u00c4\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8:\3\2\2\2\u00c9\u00cd"+
		"\5\63\32\2\u00ca\u00cc\5\65\33\2\u00cb\u00ca\3\2\2\2\u00cc\u00cf\3\2\2"+
		"\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce<\3\2\2\2\u00cf\u00cd"+
		"\3\2\2\2\u00d0\u00d3\5\21\t\2\u00d1\u00d3\5\23\n\2\u00d2\u00d0\3\2\2\2"+
		"\u00d2\u00d1\3\2\2\2\u00d3\u00dd\3\2\2\2\u00d4\u00d6\5\65\33\2\u00d5\u00d4"+
		"\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8"+
		"\u00db\3\2\2\2\u00d9\u00dc\5\23\n\2\u00da\u00dc\5\21\t\2\u00db\u00d9\3"+
		"\2\2\2\u00db\u00da\3\2\2\2\u00dc\u00de\3\2\2\2\u00dd\u00d5\3\2\2\2\u00de"+
		"\u00df\3\2\2\2\u00df\u00dd\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e4\3\2"+
		"\2\2\u00e1\u00e3\5\65\33\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u0106\3\2\2\2\u00e6\u00e4\3\2"+
		"\2\2\u00e7\u00f2\5\63\32\2\u00e8\u00ea\5\65\33\2\u00e9\u00e8\3\2\2\2\u00ea"+
		"\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00f0\3\2"+
		"\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00f1\5\23\n\2\u00ef\u00f1\5\21\t\2\u00f0"+
		"\u00ee\3\2\2\2\u00f0\u00ef\3\2\2\2\u00f1\u00f3\3\2\2\2\u00f2\u00eb\3\2"+
		"\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f5\3\2\2\2\u00f5"+
		"\u00f9\3\2\2\2\u00f6\u00f8\5\65\33\2\u00f7\u00f6\3\2\2\2\u00f8\u00fb\3"+
		"\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u0106\3\2\2\2\u00fb"+
		"\u00f9\3\2\2\2\u00fc\u00ff\5\21\t\2\u00fd\u00ff\5\23\n\2\u00fe\u00fc\3"+
		"\2\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0101\3\2\2\2\u0100\u0102\5\65\33\2\u0101"+
		"\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2"+
		"\2\2\u0104\u0106\3\2\2\2\u0105\u00d2\3\2\2\2\u0105\u00e7\3\2\2\2\u0105"+
		"\u00fe\3\2\2\2\u0106>\3\2\2\2\u0107\u010a\5!\21\2\u0108\u010b\5\61\31"+
		"\2\u0109\u010b\n\f\2\2\u010a\u0108\3\2\2\2\u010a\u0109\3\2\2\2\u010b\u010c"+
		"\3\2\2\2\u010c\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010e\3\2\2\2\u010e"+
		"\u010f\5!\21\2\u010f@\3\2\2\2\u0110\u0113\5!\21\2\u0111\u0114\5\61\31"+
		"\2\u0112\u0114\n\r\2\2\u0113\u0111\3\2\2\2\u0113\u0112\3\2\2\2\u0114\u0115"+
		"\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117"+
		"\u0118\5!\21\2\u0118B\3\2\2\2)\2Vdjlntz|~\u008c\u008e\u0094\u0096\u00a5"+
		"\u00a9\u00ae\u00b4\u00b6\u00ba\u00bf\u00c7\u00cd\u00d2\u00d7\u00db\u00df"+
		"\u00e4\u00eb\u00f0\u00f4\u00f9\u00fe\u0103\u0105\u010a\u010c\u0113\u0115"+
		"\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}