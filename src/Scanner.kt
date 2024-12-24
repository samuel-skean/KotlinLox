import TokenType.*
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
            yieldAll(scanToken())
        }

        yield(Token(EOF, "", null, lineNum, start))
    }

    private fun scanToken() = sequence {
        val c = nextCharacter()
        // man... that's not what when means :(.
        when (c) {
            '(' -> yieldAll(addSimpleToken(LEFT_PAREN))
            ')' -> yieldAll(addSimpleToken(RIGHT_PAREN))
            '{' -> yieldAll(addSimpleToken(LEFT_BRACE))
            '}' -> yieldAll(addSimpleToken(RIGHT_BRACE))
            ',' -> yieldAll(addSimpleToken(COMMA))
            '.' -> yieldAll(addSimpleToken(DOT))
            '-' -> yieldAll(addSimpleToken(MINUS))
            '+' -> yieldAll(addSimpleToken(PLUS))
            ';' -> yieldAll(addSimpleToken(SEMICOLON))
            '/' -> {
                if (matchCharacter('/')) {
                    while (peek() != '\n' && !isAtEnd()) nextCharacter()
                } else {
                    yieldAll(addSimpleToken(SLASH))
                }
            }
            '*' -> yieldAll(addSimpleToken(STAR))
            '!' -> yieldAll(addSimpleToken(if (matchCharacter('=')) BANG_EQUAL else BANG))
            '=' -> yieldAll(addSimpleToken(if (matchCharacter('=')) EQUAL_EQUAL else EQUAL))
            '>' -> yieldAll(addSimpleToken(if (matchCharacter('=')) GREATER_EQUAL else GREATER))
            '<' -> yieldAll(addSimpleToken(if (matchCharacter('=')) LESS_EQUAL else LESS))

            // Ignore whitespace:
            ' ', '\r', '\t' -> {}
            '\n' -> lineNum++
            '"' -> yieldAll(string())


            else -> {
                if (c.isDigit()) {
                    yieldAll(number())
                } else if (c == '_' || c.isLetter()) {
                    yieldAll(identifier())
                } else {
                    loxError(lineNum, "Unexpected character.")
                }

            }

        }
    }

    private fun identifier() = sequence {
        while (peek()?.isLetterOrDigit() == true || peek() == '_') nextCharacter()

        // Maximal munch requires that we handle keywords as a subcategory of identifiers.
        yieldAll(addSimpleToken(reservedWords[source.substring(start, current)] ?: IDENTIFIER))
    }

    private fun string() = sequence {
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
            return@sequence
        }

        // Consume the final '"':
        nextCharacter()

        // Trim the surrounding quotes.
        // Lox does not support escape sequences, so this is all we need to do.
        val contents = source.substring(start + 1, current - 1)
        yieldAll(addToken(STRING, contents))
    }

    private fun number() = sequence {
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
        yieldAll(addToken(NUMBER, literal.toDouble()))
    }


    // Helpers:

    private fun isAtEnd() = current >= source.length

    // advance() from the book
    private fun nextCharacter() = source[current++] // yay, operator overloading! :)

    // match() from the book
    private fun matchCharacter(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char? {
        if (isAtEnd()) return null
        return source[current]
    }

    private fun peekNext(): Char? {
        if (current + 1 >= source.length) return null
        return source[current + 1]
    }

    // Take that, function overloading!
    private fun addSimpleToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) = sequence {
        yield(Token(type, source.substring(start, current), literal, lineNum, start))
    }
}
