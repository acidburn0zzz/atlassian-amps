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

def testCompressedXML = new File(targetClasses, 'test-compressed.xml')
assert testCompressedXML.exists()

def testXML = new File(targetClasses, 'int/test.xml')
assert testXML.exists()

assert testCompressedXML.compareTo(testXML)