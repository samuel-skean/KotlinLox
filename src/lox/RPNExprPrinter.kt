package lox

class RPNExprPrinter : ExprPrinter() {
    override fun format(name: String, vararg exprs: Expr): String {
        val output = StringBuilder()

        for (expr in exprs) {
            output.append(expr.accept(this))
            output.append(" ") // Similarly, this works for RPN!
        }
        output.append(name)
        return output.toString()
    }
}