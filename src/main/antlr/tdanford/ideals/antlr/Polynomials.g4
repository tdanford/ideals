grammar Polynomials;

polynomials
  : polynomial (SEP polynomial)* SEP? // grammar is made up of ';' separated statements
  ;

polynomial
  : term # Monomial
  | grouped_polynomial+ # Multiplication
  | polynomial '+' term # Addition
  | polynomial '-' term # Subtraction
  ;

grouped_polynomial : '(' polynomial ')' ;

term
  : coefficient? exp_var+   # MonomialTerm
  | coefficient   # ConstantTerm
  ;

exp_var
  : var   # SingleVar
  | var '^' exponent   # ExponentiatedVar
  ;

coefficient : integer | rational | decimal ;

rational : '-'? INTEGRAL '/' INTEGRAL ;
integer : '-'? INTEGRAL ;
decimal : '-' ? FLOATING_POINT ;

exponent : INTEGRAL ;
var : VARIABLE ;

/* Lexer rules

Order matters here: rules that appear first are matched first, even if a rule that appears later
would also match. For example "FALSE" is matched as K_FALSE even though it could be matched as
an IDENTIFIER.
*/

// Keywords
SEP : ';';

VARIABLE
  : [a-zA-Z_] [a-zA-Z_0-9]*
  ;

STRING_LITERAL
  : '\'' (ESC | ~ ['\\])* '\''
  ;

INTEGRAL : DIGIT+ ;

FLOATING_POINT
  : DIGIT+ ( '.' DIGIT* )? ( E [-+]? DIGIT+ )?
  | '.' DIGIT+ ( E [-+]? DIGIT+ )?
  ;

SPACES
  : [ \u000B\t\r\n]+ -> channel(HIDDEN)
  ;

fragment ESC : '\\' (['\\/bfnrt] | 'u' HEX HEX HEX HEX);

fragment DIGIT : [0-9];

// A hexadecimal digit
fragment HEX : [0-9a-fA-F];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];
