// Some reason this is here.  These not delongs here.
def thisArtifactId = binding.artifactId

final File projectDir = new File("$basedir/amps-it-create")
assert projectDir.exists(), "The project should have been created under $projectDir"

final File projectPom = new File(projectDir, 'pom.xml')
assert projectPom.exists(), "The project's POM should have been created under $projectDir"

def pom = new groovy.xml.Namespace("http://maven.apache.org/POM/4.0.0", '')
def project = new XmlParser().parse(projectPom)

assert project[pom.groupId].text() == 'com.atlassian.amps.it.create', "Unexpected ${project[pom.groupId].text()}"
assert project[pom.artifactId].text() == 'amps-it-create'
assert project[pom.version].text() == '1.0'
assert project[pom.packaging].text() == 'atlassian-plugin'


// Verify that the generated plugin has the configuration as transformless (Atlassian-Plugin-Key exist and correct).
def ampsPlugin = project[pom.build][pom.plugins][pom.plugin].find { it[pom.artifactId].text() == thisArtifactId }
def pluginKey = ampsPlugin[pom.configuration][pom.instructions][pom.'Atlassian-Plugin-Key'].text()
assert pluginKey == '${atlassian.plugin.key}', "Unexpected ${pluginKey}"

final File projectPluginDescriptor = new File(projectDir, 'src/main/resources/atlassian-plugin.xml')
assert projectPluginDescriptor.exists(), "The project's plugin descriptor should have been created at $projectPluginDescriptor"

def pluginXml = new XmlParser().parse(projectPluginDescriptor)
// The key should be the same with the one in pom.xml
assert pluginXml.'@key' == pluginKey, "Unexpected ${pluginXml.'@key'}"
assert pluginXml.'@name' == '${project.name}'
assert pluginXml.'@plugins-version' == '2'
assert pluginXml.'plugin-info'.description.text() == '${project.description}'
assert pluginXml.'plugin-info'.version.text() == '${project.version}'

final File packageDir = new File("$projectDir/src/main/java/${'com.atlassian.it'.replace('.', '/')}")
assert packageDir.exists(), "Package should exist at $packageDir"
assert packageDir.list().length == 2, "Package should contain two subpackages" // api and impl
assert new File(packageDir, 'api/MyPluginComponent.java').exists(), "api should contain MyPluginComponent interface"
assert new File(packageDir, 'impl/MyPluginComponentImpl.java').exists(), "impl should contain MyPluginComponentImpl class"

