grammar Flow;

// Parser

expression: flow dot chain EOF;
chain: flowExpression (dot flowExpression)*;
flowExpression: then | thenSet;
flow: FLOW;
dot: '.';
then: THEN '(' lambda ')';
thenSet: THENSET '(' key ',' lambda ')';
key: KEY;
name: NAME;
string: KEY | STRING;
lambda: name '=>' body;
modelGet: MGET '(' key ')';
services: SERVICES;
result: RESULT;
body: operation | '{' operation? '}';
poperation: '(' operation? ')';
operation: ( modelGet | services | result | string | ',' | '"' | '=' | '=>' | name | SP | dot | poperation)+;

// Lexer

//DOT: '.';
MGET: 'model.get';
SERVICES: 'services';
RESULT: 'result';
THEN: 'then';
THENSET: 'thenSet';
FLOW: 'flow';
NAME: [0-9a-zA-Z]+;
KEY: '"' [0-9a-zA-Z]+ '"';
STRING: '"' ~["]* '"';
SP: '\'' | '\\' | '!' | '@' | '#' | '$' | '%' | '^' | '&' | '*' | '-' | '_' | '+' | '[' | ']' | '|' | ';' | ':' | '<' | '>' | '/' | '?';
WS: [ \n\t\r]+ -> channel(HIDDEN);

