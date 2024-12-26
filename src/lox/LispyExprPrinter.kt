package lox

class LispyExprPrinter : ExprPrinter() {
    override fun format(name: String, vararg exprs: Expr): String {

        val output = StringBuilder()
        output.append("(")

        output.append(name)
        for (expr in exprs) {
            output.append(" ") // How nice that this just works for Lisp.
            output.append(expr.accept(this))
        }

        output.append(")")
        return output.toString()
    }
}