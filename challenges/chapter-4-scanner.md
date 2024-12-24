1. The lexical grammars of Python and Haskell are not regular. What does that mean, and why aren’t they?


They aren't regular because of significant indentation and, for Haskell, because multi-line comments can nest. This is handled in the scanner, not the parser, because the parser should be able to be independent of the indentation rules.

2. Aside from separating tokens—distinguishing print foo from printfoo—spaces aren’t used for much in most languages. However, in a couple of dark corners, a space does affect how code is parsed in CoffeeScript, Ruby, and the C preprocessor. Where and what effect does it have in each of those languages?

I know Ruby has special rules around the `[]=` method and other methods ending in `=` (which typically act as property setters), allowing whitespace in those identifiers but not others. I don't quite care enough to look at the others - I don't really ever want to use CoffeeScript, and the C preprocessor scares me.

3. Our scanner here, like most, discards comments and whitespace since those aren’t needed by the parser. Why might you want to write a scanner that does not discard those? What would it be useful for?

It might be useful for doc-comments, or to report grammatical errors in comments, or to attempt to detect when some code is mistakenly commented out. It would also be useful for transforming code while preserving comments.

4. Add support to Lox’s scanner for C-style /* ... */ block comments. Make sure to handle newlines in them. Consider allowing them to nest. Is adding support for nesting more work than you expected? Why?

I added support. I'm not supporting nesting because I know that's a pain (I did it for Bluejay), and there's very little reason to given that single-line comments exist.