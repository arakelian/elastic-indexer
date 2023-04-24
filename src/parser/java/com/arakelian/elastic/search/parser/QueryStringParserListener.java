// Generated from com/arakelian/elastic/search/parser/QueryStringParser.g4 by ANTLR 4.12.0

// @formatter:off
package com.arakelian.elastic.search.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryStringParser}.
 */
public interface QueryStringParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#mainQ}.
	 * @param ctx the parse tree
	 */
	void enterMainQ(QueryStringParser.MainQContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#mainQ}.
	 * @param ctx the parse tree
	 */
	void exitMainQ(QueryStringParser.MainQContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#clauseDefault}.
	 * @param ctx the parse tree
	 */
	void enterClauseDefault(QueryStringParser.ClauseDefaultContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#clauseDefault}.
	 * @param ctx the parse tree
	 */
	void exitClauseDefault(QueryStringParser.ClauseDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#clauseOr}.
	 * @param ctx the parse tree
	 */
	void enterClauseOr(QueryStringParser.ClauseOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#clauseOr}.
	 * @param ctx the parse tree
	 */
	void exitClauseOr(QueryStringParser.ClauseOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#clauseAnd}.
	 * @param ctx the parse tree
	 */
	void enterClauseAnd(QueryStringParser.ClauseAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#clauseAnd}.
	 * @param ctx the parse tree
	 */
	void exitClauseAnd(QueryStringParser.ClauseAndContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#clauseNot}.
	 * @param ctx the parse tree
	 */
	void enterClauseNot(QueryStringParser.ClauseNotContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#clauseNot}.
	 * @param ctx the parse tree
	 */
	void exitClauseNot(QueryStringParser.ClauseNotContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#clauseBasic}.
	 * @param ctx the parse tree
	 */
	void enterClauseBasic(QueryStringParser.ClauseBasicContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#clauseBasic}.
	 * @param ctx the parse tree
	 */
	void exitClauseBasic(QueryStringParser.ClauseBasicContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(QueryStringParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(QueryStringParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(QueryStringParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(QueryStringParser.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(QueryStringParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(QueryStringParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#anything}.
	 * @param ctx the parse tree
	 */
	void enterAnything(QueryStringParser.AnythingContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#anything}.
	 * @param ctx the parse tree
	 */
	void exitAnything(QueryStringParser.AnythingContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#two_sided_range_term}.
	 * @param ctx the parse tree
	 */
	void enterTwo_sided_range_term(QueryStringParser.Two_sided_range_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#two_sided_range_term}.
	 * @param ctx the parse tree
	 */
	void exitTwo_sided_range_term(QueryStringParser.Two_sided_range_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#range_term}.
	 * @param ctx the parse tree
	 */
	void enterRange_term(QueryStringParser.Range_termContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#range_term}.
	 * @param ctx the parse tree
	 */
	void exitRange_term(QueryStringParser.Range_termContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#range_value}.
	 * @param ctx the parse tree
	 */
	void enterRange_value(QueryStringParser.Range_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#range_value}.
	 * @param ctx the parse tree
	 */
	void exitRange_value(QueryStringParser.Range_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#multi_value}.
	 * @param ctx the parse tree
	 */
	void enterMulti_value(QueryStringParser.Multi_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#multi_value}.
	 * @param ctx the parse tree
	 */
	void exitMulti_value(QueryStringParser.Multi_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#normal}.
	 * @param ctx the parse tree
	 */
	void enterNormal(QueryStringParser.NormalContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#normal}.
	 * @param ctx the parse tree
	 */
	void exitNormal(QueryStringParser.NormalContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#truncated}.
	 * @param ctx the parse tree
	 */
	void enterTruncated(QueryStringParser.TruncatedContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#truncated}.
	 * @param ctx the parse tree
	 */
	void exitTruncated(QueryStringParser.TruncatedContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#quoted_truncated}.
	 * @param ctx the parse tree
	 */
	void enterQuoted_truncated(QueryStringParser.Quoted_truncatedContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#quoted_truncated}.
	 * @param ctx the parse tree
	 */
	void exitQuoted_truncated(QueryStringParser.Quoted_truncatedContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#quoted}.
	 * @param ctx the parse tree
	 */
	void enterQuoted(QueryStringParser.QuotedContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#quoted}.
	 * @param ctx the parse tree
	 */
	void exitQuoted(QueryStringParser.QuotedContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(QueryStringParser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(QueryStringParser.ModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#term_modifier}.
	 * @param ctx the parse tree
	 */
	void enterTerm_modifier(QueryStringParser.Term_modifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#term_modifier}.
	 * @param ctx the parse tree
	 */
	void exitTerm_modifier(QueryStringParser.Term_modifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#boost}.
	 * @param ctx the parse tree
	 */
	void enterBoost(QueryStringParser.BoostContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#boost}.
	 * @param ctx the parse tree
	 */
	void exitBoost(QueryStringParser.BoostContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#fuzzy}.
	 * @param ctx the parse tree
	 */
	void enterFuzzy(QueryStringParser.FuzzyContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#fuzzy}.
	 * @param ctx the parse tree
	 */
	void exitFuzzy(QueryStringParser.FuzzyContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#not_}.
	 * @param ctx the parse tree
	 */
	void enterNot_(QueryStringParser.Not_Context ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#not_}.
	 * @param ctx the parse tree
	 */
	void exitNot_(QueryStringParser.Not_Context ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#and_}.
	 * @param ctx the parse tree
	 */
	void enterAnd_(QueryStringParser.And_Context ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#and_}.
	 * @param ctx the parse tree
	 */
	void exitAnd_(QueryStringParser.And_Context ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#or_}.
	 * @param ctx the parse tree
	 */
	void enterOr_(QueryStringParser.Or_Context ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#or_}.
	 * @param ctx the parse tree
	 */
	void exitOr_(QueryStringParser.Or_Context ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDate(QueryStringParser.DateContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDate(QueryStringParser.DateContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryStringParser#sep}.
	 * @param ctx the parse tree
	 */
	void enterSep(QueryStringParser.SepContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryStringParser#sep}.
	 * @param ctx the parse tree
	 */
	void exitSep(QueryStringParser.SepContext ctx);
}