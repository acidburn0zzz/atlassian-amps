assert mavenExitCode == 0, "The maven build should not have failed!"

def jira_pom = new File(basedir, "jira-run-pom.xml")
assert jira_pom.exists(), "Command should have been run and should have created a jira-run-pom.xml"