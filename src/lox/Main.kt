package lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

var hadError = false
fun main(args: Array<String>) {
    if (args.size > 1) {
        eprintln("Usage: klox [script]")
        // Following a convention in
        // https://man.freebsd.org/cgi/man.cgi?query=sysexits&apropos=0&sektion=0&manpath=FreeBSD+4.3-RELEASE&format=html, just like the book.
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))
    if (hadError) { exitProcess(65) }
}

fun runPrompt() {
    while (true) {
        print("> ")
        // Throws an exception when the input ends.
        val line = readlnOrNull() ?: break
        run(line)
        hadError = false
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    // For now, just print the tokens:
    for (token in tokens) {
        println(token)
    }
}

fun loxError(lineNum: Int, message: String) {
    report(lineNum, "", message)
}

fun report(lineNum: Int, where: String, message: String) {
    eprintln("[line $lineNum] Error $where: $message")
    hadError = true
}

// Really?
fun eprintln(string: String) {
    System.err.println(string)
}