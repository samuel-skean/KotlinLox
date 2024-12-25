package lox

import lox.TokenType.*
class Scanner(private val source: String) {
    // I cannot escape companion objects, I guess.
    companion object {
        private val reservedWords = mapOf("and" to AND, "class" to CLASS, "else" to ELSE, "false" to FALSE, "fun" to
                FUN,
            "for" to FOR, "if" to IF, "nil" to NIL, "or" to OR, "print" to PRINT, "return" to RETURN, "super" to SUPER,
            "this" to THIS, "true" to TRUE, "var" to VAR, "while" to WHILE)
    }
    // First character of the current lexeme being scanned:
    private var start = 0
    private var current = 0
    private var lineNum = 1

    fun scanTokens(): Sequence<Token> = sequence {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            // Scrunch up, little lexer caterpillar!
            start = current
            val nextToken = nextToken()
            if (nextToken != null) yield(nextToken)
        }

        yield(Token(EOF, "", null, lineNum, start))
    }

    private fun nextToken(): Token? {
        // man... that's not what when means :(.
        return when (val c = nextCharacter()) {
            '(' -> simpleToken(LEFT_PAREN)
            ')' -> simpleToken(RIGHT_PAREN)
            '{' -> simpleToken(LEFT_BRACE)
            '}' -> simpleToken(RIGHT_BRACE)
            ',' -> simpleToken(COMMA)
            '.' -> simpleToken(DOT)
            '-' -> simpleToken(MINUS)
            '+' -> simpleToken(PLUS)
            ';' -> simpleToken(SEMICOLON)
            '/' -> {
                if (matchCharacter('/')) { // Single-line comment
                    while (peek() != '\n' && !isAtEnd()) nextCharacter()
                    null
                } else if (matchCharacter('*')) { // Multi-line comment
                    while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                        if (nextCharacter() == '\n') lineNum++
                    }

                    // STRETCH: Is there a better way to do this that doesn't duplicate the error code?
                    if (!isAtEnd()) {
                        nextCharacter()
                        if (!isAtEnd()) nextCharacter()
                        else loxError(lineNum, "Unterminated multi-line comment.")
                    } else {
                        loxError(lineNum, "Unterminated multi-line comment.")
                    }
                    null
                } else {
                    simpleToken(SLASH)
                }
            }
            '*' -> simpleToken(STAR)
            '!' -> simpleToken(if (matchCharacter('=')) BANG_EQUAL else BANG)
            '=' -> simpleToken(if (matchCharacter('=')) EQUAL_EQUAL else EQUAL)
            '>' -> simpleToken(if (matchCharacter('=')) GREATER_EQUAL else GREATER)
            '<' -> simpleToken(if (matchCharacter('=')) LESS_EQUAL else LESS)

            // Ignore whitespace:
            ' ', '\r', '\t' -> null
            '\n' -> {
                lineNum++
                null
            }
            '"' -> string()


            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c == '_' || c.isLetter()) {
                    identifier()
                } else {
                    loxError(lineNum, "Unexpected character.")
                    null
                }
            }
        }
    }

    private fun identifier(): Token {
        while (peek()?.isLetterOrDigit() == true || peek() == '_') nextCharacter()

        // Maximal munch requires that we handle keywords as a subcategory of identifiers.
        return simpleToken(reservedWords[source.substring(start, current)] ?: IDENTIFIER)
    }

    private fun string(): Token? {
        while (peek() != '"' && !isAtEnd()) {
            // String literals can have newlines in them in Lox! Whaddya know.
            if (peek() == '\n') lineNum++
            nextCharacter()
        }

        if (isAtEnd()) {
            loxError(lineNum, "Unterminated string.")
            // In general, this means "return from this closure, not the full function `string`". So, basically, do a
            // local return. This means `sequence` is still responsible for creating a sequence of things that were
            // yielded (though in this case, that's nothing).
            return null
        }

        @Suppress("GrazieInspection")
        // Consume the final '"':
        nextCharacter()

        // Trim the surrounding quotes.
        // Lox does not support escape sequences, so this is all we need to do.
        val contents = source.substring(start + 1, current - 1)
        return token(STRING, contents)
    }

    private fun number(): Token {
        // This is pretty disgusting, but I *think* it's nicer than returning a null character (e.g., '\0'):
        while (peek()?.isDigit() == true) {
            nextCharacter()
        }
        // We must make sure there is a digit after the '.', or we aren't a real Lox interpreter! So, just for that, we
        // must have a peekNext().
        if (peek() == '.' && peekNext()?.isDigit() == true) {
            nextCharacter()
            while (peek()?.isDigit() == true) {
                nextCharacter()
            }
        }

        val literal = source.substring(start, current)
        return token(NUMBER, literal.toDouble())
    }


    // STRETCH: Make the below methods the only methods on the lexer class. Then, make all the above methods, that
    // are specific to actual things in the language, freestanding functions. Take as much inspiration as possible
    // from [Lexical Scanning in Go](https://www.youtube.com/watch?v=HxaD_trXwRE) without *actually* invoking
    // arbitrary concurrency.
    // Helpers:

    private fun isAtEnd() = current >= source.length

    // advance() from the book
    private fun nextCharacter() = source[current++] // yay, operator overloading! :)

    // match() from the book
    private fun matchCharacter(expected: Char): Boolean =
        if (isAtEnd()) false
        else if (source[current] != expected) false
        else {
            current++
            true
        }

    private fun peek(): Char? =
        if (isAtEnd()) null
        else source[current]

    private fun peekNext(): Char? =
        if (current + 1 >= source.length) null
        else source[current + 1]


    // Take that, function overloading!
    private fun simpleToken(type: TokenType) = token(type, null)

    private fun token(type: TokenType, literal: Any?) =
        Token(type, source.substring(start, current), literal, lineNum, start)
}
