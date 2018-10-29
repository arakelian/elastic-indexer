parser grammar QueryStringParser;

@header {
// @formatter:off
package com.arakelian.elastic.search.parser;
}

options {tokenVocab = QueryStringLexer;}

tokens {
  OPERATOR,
  ATOM,
  MODIFIER,
  TMODIFIER,
  CLAUSE,
  FIELD,
  FUZZY,
  BOOST,
  QNORMAL,
  QPHRASE,
  QPHRASETRUNC,
  QTRUNCATED,
  QRANGEIN,
  QRANGEEX,
  QANYTHING,
  QDATE
}

mainQ :
  sep? clause=clauseDefault sep? EOF
  ;

clauseDefault
  :
  //m:(a b AND c OR d OR e)
  // without duplicating the rules (but it allows recursion)
  clauseOr (sep? clauseOr)*
  ;

clauseOr
  : clauseAnd (or_ clauseAnd)*
  ;

clauseAnd
  : clauseNot (and_ clauseNot)*
  ;

clauseNot
  : clauseBasic (not_ clauseBasic)*
  ;

clauseBasic
  :
  sep? modifier? LPAREN clauseDefault sep? RPAREN term_modifier?
  | sep? atom
  ;

atom
  :
  modifier? field multi_value term_modifier?
  | modifier? field? value term_modifier?
  ;

field
  :
  TERM_NORMAL COLON sep?
  ;

value
  :
  range_term
  | normal
  | truncated
  | quoted
  | quoted_truncated
  | QMARK
  | anything
  | STAR
  ;

anything
  :
  STAR COLON STAR
  ;

two_sided_range_term
  :
  start_type=(LBRACK|LCURLY)
  sep?
  (a=range_value)
  sep?
  ( TO? sep? b=range_value sep? )?
  end_type=(RBRACK|RCURLY)
  ;

range_term
  :
  two_sided_range_term
  ;

range_value
  :
  truncated
  | quoted
  | quoted_truncated
  | date
  | normal
  | STAR
  ;

multi_value
  :
  LPAREN clauseDefault sep? RPAREN
  ;

normal
  :
  TERM_NORMAL
  | NUMBER
  ;

truncated
  :
  TERM_TRUNCATED
  ;

quoted_truncated
  :
  PHRASE_ANYTHING
  ;

quoted  :
  PHRASE
  ;

modifier:
  PLUS
  | MINUS;


term_modifier :
  boost fuzzy?
  | fuzzy boost?
  ;

boost :
  (CARAT) // set the default value
  (NUMBER)? //replace the default with user input
  ;

fuzzy :
  (TILDE) // set the default value
  (NUMBER)? //replace the default with user input
  ;

not_  :
  sep? AND sep? NOT
  | sep? NOT
  ;

and_  :
  sep? AND
  ;

or_   :
  sep? OR
  ;

date  :
  DATE_TOKEN
  ;
  
sep : WS+;
