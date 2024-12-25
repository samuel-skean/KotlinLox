data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val lineNum: Int, val start: Int) {
    override fun toString(): String = "Line Number: $lineNum Start position: $start $type $lexeme $literal"
}