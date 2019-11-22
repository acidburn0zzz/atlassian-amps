def shouldCreateOsgiJavaConfig = request.getProperties().get("useOsgiJavaConfig") == "Y"
def createdPluginDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def createdPackagePath = request.getProperties().get("package").replace(".", "/")

if (!shouldCreateOsgiJavaConfig) {
    assert new File(createdPluginDir, "src/main/java/${createdPackagePath}/config").deleteDir()
}