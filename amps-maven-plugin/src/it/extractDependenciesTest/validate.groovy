import java.security.MessageDigest

def buildLog = new File(basedir, 'build.log').collect { it }

def startOfWarnings = '[WARNING] Extracting your plugin\'s dependencies caused the following file(s) to overwrite each other:'
assert buildLog.contains(startOfWarnings)

buildLog = buildLog[buildLog.indexOf(startOfWarnings) .. -1]

assert buildLog[1]  == '[WARNING] -- META-INF/LICENSE from [org.apache.felix.bundlerepository-1.4.0-jar, org.apache.felix.shell-1.2.0-jar, org.osgi.core-1.0.1-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[2]  == '[WARNING] -- META-INF/LICENSE.txt from [commons-lang3-3.9-jar, commons-text-1.8-jar]'
assert buildLog[3]  == '[WARNING] -- META-INF/NOTICE from [org.apache.felix.bundlerepository-1.4.0-jar, org.apache.felix.shell-1.2.0-jar, org.osgi.core-1.0.1-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[4]  == '[WARNING] -- META-INF/NOTICE.txt from [commons-lang3-3.9-jar, commons-text-1.8-jar]'
assert buildLog[5]  == '[WARNING] -- org/kxml2/io/KXmlParser.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[6]  == '[WARNING] -- org/kxml2/io/KXmlSerializer.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[7]  == '[WARNING] -- org/kxml2/kdom/Document.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[8]  == '[WARNING] -- org/kxml2/kdom/Element.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[9]  == '[WARNING] -- org/kxml2/kdom/Node.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[10] == '[WARNING] -- org/kxml2/wap/Wbxml.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[11] == '[WARNING] -- org/kxml2/wap/WbxmlParser.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[12] == '[WARNING] -- org/kxml2/wap/WbxmlSerializer.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[13] == '[WARNING] -- org/kxml2/wap/syncml/SyncML.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[14] == '[WARNING] -- org/kxml2/wap/wml/Wml.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[15] == '[WARNING] -- org/kxml2/wap/wv/WV.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar]'
assert buildLog[16] == '[WARNING] -- org/osgi/service/obr/Capability.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[17] == '[WARNING] -- org/osgi/service/obr/CapabilityProvider.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[18] == '[WARNING] -- org/osgi/service/obr/Repository.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[19] == '[WARNING] -- org/osgi/service/obr/RepositoryAdmin.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[20] == '[WARNING] -- org/osgi/service/obr/RepositoryPermission.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[21] == '[WARNING] -- org/osgi/service/obr/Requirement.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[22] == '[WARNING] -- org/osgi/service/obr/Resolver.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[23] == '[WARNING] -- org/osgi/service/obr/Resource.class from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[24] == '[WARNING] -- org/osgi/service/obr/packageinfo from [org.apache.felix.bundlerepository-1.4.0-jar, org.osgi.service.obr-1.0.2-jar]'
assert buildLog[25] == '[WARNING] -- org/xmlpull/v1/XmlPullParser.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar, xmlpull-1.1.3.1-jar]'
assert buildLog[26] == '[WARNING] -- org/xmlpull/v1/XmlPullParserException.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar, xmlpull-1.1.3.1-jar]'
assert buildLog[27] == '[WARNING] -- org/xmlpull/v1/XmlPullParserFactory.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar, xmlpull-1.1.3.1-jar]'
assert buildLog[28] == '[WARNING] -- org/xmlpull/v1/XmlSerializer.class from [kxml2-2.2.2-jar, org.apache.felix.bundlerepository-1.4.0-jar, xmlpull-1.1.3.1-jar]'
assert buildLog[29] == '[WARNING] To prevent this, set <extractDependencies> to false in your AMPS configuration'

def classesDir = new File(basedir, "target/classes")

new File(basedir, 'sha256sum').eachLine {
    def (hash, file) = it.split(' ', 2)
    assert hash == new File(classesDir, file).text.digest('SHA-256').padLeft(64, '0')
}