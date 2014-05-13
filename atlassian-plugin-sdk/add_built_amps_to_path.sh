# Source this script to use the locally built AMPS scripts for testing
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

ln -s "$DIR/target/apache-maven-3.2.1" "$DIR/target/apache-maven"
chmod u+x "$DIR"/target/bin/*
PATH="$DIR/target/bin:$PATH"
