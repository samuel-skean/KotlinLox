import TokenType.*
class Scanner(private val source: String) {
    // I cannot escape companion objects, I guess.
    companion object {
        val reservedWords = mapOf("and" to AND, "class" to CLASS, "else" to ELSE, "false" to FALSE, "fun" to FUN,
            "for" to FOR, "if" to IF, "nil" to NIL, "or" to OR, "print" to PRINT, "return" to RETURN, "super" to SUPER,
            "this" to THIS, "true" to TRUE, "var" to VAR, "while" to WHILE)
    }
    private val tokens = mutableListOf<Token>()
    // First character of the current lexeme being scanned:
    private var start = 0
    private var current = 0
    private var lineNum = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            // Scrunch up, little lexer caterpillar!
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, lineNum, start))
        return tokens
    }

    private fun scanToken() {
        val c = nextCharacter()
        // man... that's not what when means :(.
        when (c) {
            '(' -> addSimpleToken(LEFT_PAREN)
            ')' -> addSimpleToken(RIGHT_PAREN)
            '{' -> addSimpleToken(LEFT_BRACE)
            '}' -> addSimpleToken(RIGHT_BRACE)
            ',' -> addSimpleToken(COMMA)
            '.' -> addSimpleToken(DOT)
            '-' -> addSimpleToken(MINUS)
            '+' -> addSimpleToken(PLUS)
            ';' -> addSimpleToken(SEMICOLON)
            '/' -> {
                if (matchCharacter('/')) {
                    while (peek() != '\n' && !isAtEnd()) nextCharacter()
                } else {
                    addSimpleToken(SLASH)
                }
            }
            '*' -> addSimpleToken(STAR)
            '!' -> addSimpleToken(if (matchCharacter('=')) BANG_EQUAL else BANG)
            '=' -> addSimpleToken(if (matchCharacter('=')) EQUAL_EQUAL else EQUAL)
            '>' -> addSimpleToken(if (matchCharacter('=')) GREATER_EQUAL else GREATER)
            '<' -> addSimpleToken(if (matchCharacter('=')) LESS_EQUAL else LESS)

            // Ignore whitespace:
            ' ', '\r', '\t' -> {}
            '\n' -> lineNum++
            '"' -> string()


            else -> {
                if (c.isDigit()) {
                    number()
                } else if (c == '_' || c.isLetter()) {
                    identifier()
                } else {
                    loxError(lineNum, "Unexpected character.")
                }

            }

        }
    }

    private fun identifier() {
        while (peek()?.isLetterOrDigit() == true || peek() == '_') nextCharacter()

        // Maximal munch requires that we handle keywords as a subcategory of identifiers.
        addSimpleToken(reservedWords[source.substring(start, current)] ?: IDENTIFIER)
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            // String literals can have newlines in them in Lox! Whaddya know.
            if (peek() == '\n') lineNum++
            nextCharacter()
        }

        if (isAtEnd()) {
            loxError(lineNum, "Unterminated string.")
            return
        }

        // Consume the final '"':
        nextCharacter()

        // Trim the surrounding quotes.
        // Lox does not support escape sequences, so this is all we need to do.
        val contents = source.substring(start + 1, current - 1)
        addToken(STRING, contents)
    }

    private fun number() {
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
        addToken(NUMBER, literal.toDouble())
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

    private fun addToken(type: TokenType, literal: Any?) {
        tokens.add(Token(type, source.substring(start, current), literal, lineNum, start))
    }
}
