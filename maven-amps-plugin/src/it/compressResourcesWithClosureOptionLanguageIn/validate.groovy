assert mavenExitCode == 0, "The maven build should not have failed!"

def targetClasses = new File(basedir, 'target/classes')
assert new File(targetClasses, 'foo.js').exists()
def fooMinJs = new File(targetClasses, 'foo-min.js');
assert fooMinJs.exists();

//File should not be empty
def line;
fooMinJs.withReader { line = it.readLine() };
assert line != null;