assert mavenExitCode == 0, "The maven build should not have failed!"

def targetClasses = new File(basedir, 'target/classes')
assert !new File(targetClasses, 'foo.js').exists()
assert !new File(targetClasses, 'foo-min.js').exists()

assert !new File(targetClasses, 'foo.css').exists()
assert !new File(targetClasses, 'foo-min.css').exists()

assert !new File(targetClasses, 'foo.css.js-ext').exists()
assert !new File(targetClasses, 'foo.css.js-ext-min').exists()

assert new File(targetClasses, 'foo.css.js-ext.jsp').exists()
assert !new File(targetClasses, 'foo.css.js-ext-min.jsp').exists()

assert new File(targetClasses, 'js_.js').exists()
assert new File(targetClasses, 'js_-min.js').exists()

assert new File(targetClasses, 'ab_foo.css').exists()
assert new File(targetClasses, 'ab_foo-min.css').exists()

def targetClassesInt = new File(basedir, 'target/classes/int')
assert new File(targetClassesInt, 'test.xml').exists()

