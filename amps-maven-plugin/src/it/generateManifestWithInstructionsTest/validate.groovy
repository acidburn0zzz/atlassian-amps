import java.util.jar.Manifest

final def manifestFile = new File("$basedir/target/classes/META-INF/MANIFEST.MF")
assert manifestFile.exists(), "There should be a manifest for no instructions and is an Atlassian plugin, see $manifestFile.absolutePath"

final Manifest manifest = manifestFile.withInputStream { InputStream is -> new Manifest(is) }

assert manifest.mainAttributes.getValue("Bundle-Classpath").contains('META-INF/lib/commons-logging-1.1.1.jar'), "Should contain the commons-logging compile dep"

assert manifest.mainAttributes.getValue('Some-Key').equals('Some-Value'), "Should contain instructions specified in the POM"

