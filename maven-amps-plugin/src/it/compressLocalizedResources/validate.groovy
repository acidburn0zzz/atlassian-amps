assert mavenExitCode == 0, "The maven build should not have failed!"
def expectedClasses = new File(basedir, 'expected')
def targetClasses = new File(basedir, 'target/classes')
assert new File(targetClasses, 'foo.js').exists()
assert new File(targetClasses, 'foo.css').exists()

def actualJS = new File(targetClasses, 'foo-min.js');
assert actualJS.exists()
def expectedJS = new File(expectedClasses, 'foo-min.js');
assert expectedJS.exists()
assert actualJS.text == expectedJS.text

def actualCSS = new File(targetClasses, 'foo-min.css');
assert actualCSS.exists()
def expectedCSS = new File(expectedClasses, 'foo-min.css');
assert expectedCSS.exists()
assert actualCSS.text == expectedCSS.text