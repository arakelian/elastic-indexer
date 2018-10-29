// Generated from com/arakelian/elastic/search/parser/QueryStringParser.g4 by ANTLR 4.7.1

// @formatter:off
package com.arakelian.elastic.search.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QueryStringParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QueryStringParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#mainQ}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainQ(QueryStringParser.MainQContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#clauseDefault}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauseDefault(QueryStringParser.ClauseDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#clauseOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauseOr(QueryStringParser.ClauseOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#clauseAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauseAnd(QueryStringParser.ClauseAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#clauseNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauseNot(QueryStringParser.ClauseNotContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#clauseBasic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClauseBasic(QueryStringParser.ClauseBasicContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(QueryStringParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(QueryStringParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(QueryStringParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#anything}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnything(QueryStringParser.AnythingContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#two_sided_range_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTwo_sided_range_term(QueryStringParser.Two_sided_range_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#range_term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_term(QueryStringParser.Range_termContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#range_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_value(QueryStringParser.Range_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#multi_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulti_value(QueryStringParser.Multi_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#normal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal(QueryStringParser.NormalContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#truncated}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTruncated(QueryStringParser.TruncatedContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#quoted_truncated}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuoted_truncated(QueryStringParser.Quoted_truncatedContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#quoted}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuoted(QueryStringParser.QuotedContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifier(QueryStringParser.ModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#term_modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm_modifier(QueryStringParser.Term_modifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#boost}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoost(QueryStringParser.BoostContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#fuzzy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuzzy(QueryStringParser.FuzzyContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#not_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot_(QueryStringParser.Not_Context ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#and_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_(QueryStringParser.And_Context ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#or_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_(QueryStringParser.Or_Context ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate(QueryStringParser.DateContext ctx);
	/**
	 * Visit a parse tree produced by {@link QueryStringParser#sep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSep(QueryStringParser.SepContext ctx);
}