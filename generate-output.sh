#!/usr/bin/env zsh
# Make sure to build the Kotlin app before expecting this to do the right thing!
set -ux # I can't set -e because sometimes the code has an exception, and it actually should!

# Don't pass me any paths with '/' in them!
OUTPUT_DIR=$1
rm -r $OUTPUT_DIR # Remove the output directory.
for testfile in $(find tests -type f); do
  OUTPUT_FILE=$(sed "s/tests/${OUTPUT_DIR}/g" <<< $testfile | sed 's/.lox/.out/g')
  mkdir -p $(echo $OUTPUT_FILE | sed 's/\/[^/]*$//')
  $HOME/Library/Java/JavaVirtualMachines/corretto-21.0.5/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=55048:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/samuelskean/IdeaProjects/KotlinLox/out/production/KotlinLox:/Users/samuelskean/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/2.0.20/kotlin-stdlib-2.0.20.jar:/Users/samuelskean/.m2/repository/org/jetbrains/annotations/13.0/annotations-13.0.jar MainKt $testfile &> $OUTPUT_FILE
done
