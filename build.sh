if [ ! -d "./bin" ]; then
  mkdir bin
fi

javac src/*.java src/*/*.java -d bin