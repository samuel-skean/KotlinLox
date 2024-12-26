package tool

import lox.eprintln
import java.io.PrintWriter
import kotlin.system.exitProcess

// Kotlin's data classes are nice enough that code generation is *almost* pointless - but I'm still doing it, partly
// for the exercise, partly so I can generate my visitor interface well.
fun main(args: Array<String>) {
    if (args.size != 1) {
        eprintln("Usage: generate_ast <output_directory>")
        exitProcess(64)
    }
    val outputDir = args[0]
    // This format is more Kotlin-y than the syntax given in the book.
    defineAst(outputDir, "Expr", listOf(
        "Binary   - left: Expr, operator: Token, right: Expr",
        "Grouping - expression: Expr",
        "Literal  - value: Any",
        "Unary    - operator: Token, right: Expr",
    ))
}

fun defineAst(outputDir: String, baseName: String, variants: List<String>) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path)
    writer.println("package lox")
    writer.println()
    writer.println("sealed interface $baseName {")

    defineVisitor(writer, baseName, variants)

    // The subclasses/variants:
    for (variant in variants) {
        val (variantName, fields) = variant.split("-").map { it.trim() }
        val fieldList = fields.split(",").map { it.trim() }
        defineVariant(writer, baseName, variantName, fieldList)
    }

    writer.println("}")
    writer.close()
}

fun defineVisitor(writer: PrintWriter, baseName: String, variants: List<String>) {
    writer.println("    interface Visitor<out R> {")

    for (variant in variants) {
        val variantName = variant.split("-")[0].trim()
        writer.println("        fun visit$variantName$baseName(${baseName.lowercase()}: $variantName): R")
    }

    writer.println("    }")
}

fun defineVariant(writer: PrintWriter, baseName: String, variantName: String, fields: List<String>) {
    writer.print("    data class $variantName(")

    for (field in fields) {
        writer.print("val $field, ")
    }

    writer.println(") : $baseName {")

    writer.println("        fun <R> accept(visitor: Visitor<R>): R = ")
    writer.println("            visitor.visit$variantName$baseName(this)")

    writer.println("    }")
}
