#!/usr/bin/env zsh
# Make sure to build the Kotlin app before expecting this to do the right thing!
set -ux # I can't set -e because sometimes the code has an exception, and it actually should!

# Don't pass me any paths with '/' in them!
EXPECTED_OUTPUT_DIR=$1
# This assumes some very specific things about my setup, and it shouldn't be run that frequently anyway - the whole idea
# is to generate this once so I can then edit the lexer and have something to check against.
for testfile in $(find tests -type f); do
  EXPECTED_FILE=$(sed "s/tests/${EXPECTED_OUTPUT_DIR}/g" <<< $testfile | sed 's/.lox/.expected/g')
  mkdir -p $(echo $EXPECTED_FILE | sed 's/\/[^/]*$//')
  $HOME/Library/Java/JavaVirtualMachines/corretto-21.0.5/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=55048:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/samuelskean/IdeaProjects/KotlinLox/out/production/KotlinLox:/Users/samuelskean/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/2.0.20/kotlin-stdlib-2.0.20.jar:/Users/samuelskean/.m2/repository/org/jetbrains/annotations/13.0/annotations-13.0.jar MainKt $testfile &> $EXPECTED_FILE
done
