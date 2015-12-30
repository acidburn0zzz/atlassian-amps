import net.lingala.zip4j.core.ZipFile;

def obrFile = new File(basedir, 'target/maven-amps-plugin-genenerate-obr-artifact-test-testing.obr')
assert obrFile.exists(), "The $obrFile file should exist"

final String unzipLocation = "$basedir/target/obrunzip"
final String obrLocation = "$basedir/target/maven-amps-plugin-genenerate-obr-artifact-test-testing.obr"

ZipFile obrCompressed = new ZipFile(obrLocation)
obrCompressed.extractAll(unzipLocation)


assert new File(unzipLocation, 'maven-amps-plugin-genenerate-obr-artifact-test-testing.jar').exists()
assert new File(unzipLocation, 'obr.xml').exists()
assert new File(unzipLocation, 'dependencies/commons-io-1.4.jar').exists()
assert !new File(unzipLocation, 'dependencies/commons-logging-1.1.1.jar').exists()
