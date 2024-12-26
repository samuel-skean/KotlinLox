package lox.printers

import lox.Expr

// I don't love that this is called a printer when it really produces a string, but it seems like things like
// `PrintWriter` exist in Java and are supported by Kotlin, so when in Rome...
abstract class ExprPrinter : Expr.Visitor<String> {

    fun print(expr: Expr) = expr.accept(this)

    override fun visitBinaryExpr(expr: Expr.Binary): String =
        format(expr.operator.lexeme, expr.left, expr.right)

    override fun visitGroupingExpr(expr: Expr.Grouping): String =
        // If we record grouping, are we *really* an AST? I don't know. Because our parser will have flat structures
        // (at least, by the book) but this one won't, I guess this is more of an AST than a parse tree.
        format("group", expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): String =
        format(expr.value.toString())

    override fun visitUnaryExpr(expr: Expr.Unary): String =
        format(expr.operator.lexeme, expr.right)

    abstract fun format(name: String, vararg exprs: Expr): String


}