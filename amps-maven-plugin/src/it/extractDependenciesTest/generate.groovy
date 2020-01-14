#!/usr/bin/env groovy

// Run this file from the 'target' directory where it's been copied to to update sha256sum

def basedir = new File(".")
def classesDir = new File(basedir, "target/classes")

def list = [
        'META-INF/LICENSE',
        'META-INF/LICENSE.txt',
        'META-INF/NOTICE',
        'META-INF/NOTICE.txt',
        'org/kxml2/io/KXmlParser.class',
        'org/kxml2/io/KXmlSerializer.class',
        'org/kxml2/kdom/Document.class',
        'org/kxml2/kdom/Element.class',
        'org/kxml2/kdom/Node.class',
        'org/kxml2/wap/Wbxml.class',
        'org/kxml2/wap/WbxmlParser.class',
        'org/kxml2/wap/WbxmlSerializer.class',
        'org/kxml2/wap/syncml/SyncML.class',
        'org/kxml2/wap/wml/Wml.class',
        'org/kxml2/wap/wv/WV.class',
        'org/osgi/service/obr/Capability.class',
        'org/osgi/service/obr/CapabilityProvider.class',
        'org/osgi/service/obr/Repository.class',
        'org/osgi/service/obr/RepositoryAdmin.class',
        'org/osgi/service/obr/RepositoryPermission.class',
        'org/osgi/service/obr/Requirement.class',
        'org/osgi/service/obr/Resolver.class',
        'org/osgi/service/obr/Resource.class',
        'org/osgi/service/obr/packageinfo',
        'org/xmlpull/v1/XmlPullParser.class',
        'org/xmlpull/v1/XmlPullParserException.class',
        'org/xmlpull/v1/XmlPullParserFactory.class',
        'org/xmlpull/v1/XmlSerializer.class'
]

new File(basedir, "sha256sum").write(list.collect { "${new File(classesDir, it).text.digest('SHA-256').padLeft(64, '0')} ${it}" }.join("\n"))

