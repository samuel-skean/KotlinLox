1. Earlier, I said that the |, *, and + forms we added to our grammar metasyntax were just syntactic sugar. Take this grammar:

expr → expr ( "(" ( expr ( "," expr )* )? ")" | "." IDENTIFIER )+
     | IDENTIFIER
     | NUMBER
Produce a grammar that matches the same language but does not use any of that notational sugar.

Bonus: What kind of expression does this bit of grammar encode?
    This encodes a bit of function-call syntax that works like C (no trailing commas) and lets you use identifiers or number literals as arguments. Interestingly, it also lets you call numbers.

2. The Visitor pattern lets you emulate the functional style in an object-oriented language. Devise a complementary pattern for a functional language. It should let you bundle all of the operations on one type together and let you define new types easily.

(SML or Haskell would be ideal for this exercise, but Scheme or another Lisp works as well.)
    Well, I think you wrote an article on it! https://journal.stuffwithstuff.com/2013/08/26/what-is-open-recursion/
    Though that might be a bit more complicated than necessary for this pattern. Frankly, all you need is the ability to store functions as values in a map or (for more type safety) record type. You don't get overriding then, but overriding is the trickiest part of object orientation, and you do get putting all the code in one place.

3. In reverse Polish notation (RPN), the operands to an arithmetic operator are both placed before the operator, so 1 + 2 becomes 1 2 +. Evaluation proceeds from left to right. Numbers are pushed onto an implicit stack. An arithmetic operator pops the top two numbers, performs the operation, and pushes the result. Thus, this:

(1 + 2) * (4 - 3)
in RPN becomes:

1 2 + 4 3 - *
Define a visitor class for our syntax tree classes that takes an expression, converts it to RPN, and returns the resulting string.
    That was easy enough. Especially because I used inheritance (eek!) to make my life a bit easier.
    Of course, I could've used closures, but then any mutable state it captured might be explicit.