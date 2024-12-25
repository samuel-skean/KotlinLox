package lox

enum class TokenType {
    // Some single-character tokens:
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // Comparison operator tokens (one or two characters):
    BANG, BANG_EQUAL,
    // I kinda wish these were "ASSIGN" and "EQUALITY", but that wouldn't fit with the rest:
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals:
    IDENTIFIER, STRING, NUMBER,

    // Keywords:
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}