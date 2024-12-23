# KotlinLox

This is my implementation of the scripting language Lox in Kotlin, following the first half of the book [Crafting Interpreters](https://craftinginterpreters.com/index.html) by Robert Nystrom.
This is a Tree-Walk Interpreter.

My main intention is to learn just enough so that I can write my "real" implementation in Rust - which will follow the second half of the book.

I'm choosing Kotlin because it's better not to just copy the code in the book, and I think "sequence", which basically lets you make classical coroutines, is very well suited for writing a lexer. Also, null-safety is nice.

All the lox code in tests/from-book comes straight from the Crafting Interpreters book. Other code is highly derived from the code in that book, some of it almost a direct translation (of course, I try to minimize that, since it's not the best way to learn!).

As of now, this is built through IntelliJ's build system, and nothing fancier. This is because I can't be bothered at the moment.