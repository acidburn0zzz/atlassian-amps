def stagingrepo = new File(basedir, 'target/releases')
assert stagingrepo.exists(), "staging repo should exist at:" + stagingrepo

//we assert that the deploy plugin repo is not used. That would happen if if
// defined a custom goal for deploying again and reuse the deploy plugins config as we did with mvn-deploy
// in normal case nexus staging should override that.
def deployrepo = new File(basedir, 'target/releases-wrong')
assert !deployrepo.exists(), "deploy repo should not exist at:" + deployrepo

def deferred = new File(basedir, "target/nexus-staging/deferred/.index");
assert deferred.exists(), "deferred directory should exist at:" + deferred
//we want to make sure that nexus staging .index properties file only contains
// one entry per file. With attached jar artifact we ended up with 2 and the wrong one took over because it
// came later in the file.
weHavePluginJarEntry = false;
deferred.eachLine { 
  if (it != null && it.contains("com/atlassian/amps/it/maven-amps-plugin-deploy/testing/maven-amps-plugin-deploy-testing.jar=")) {
      assert weHavePluginJarEntry == false
      weHavePluginJarEntry = true;
  }
}
