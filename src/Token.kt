class Token(val type: TokenType, val lexeme: String, val literal: Any?, val lineNum: Int) {
    override fun toString(): String = "$type $lexeme $literal"
}