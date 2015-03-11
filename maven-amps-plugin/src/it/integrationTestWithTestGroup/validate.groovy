assert mavenExitCode == 0, "The maven build should not have failed!"

def product = project.properties['shitty.product']
product = 'amps'.equals(product) ? 'refapp' : product

def integrationSurefireReportsFoo = new File(basedir, "target/group-foo/tomcat6x/surefire-reports")
assert integrationSurefireReportsFoo.exists(), "Integration tests should have run and created test reports in $integrationSurefireReportsFoo"
def integrationSurefireReportsBar = new File(basedir, "target/group-bar/tomcat6x/surefire-reports")
assert integrationSurefireReportsBar.exists(), "Integration tests should have run and created test reports in $integrationSurefireReportsBar"

assert new File(integrationSurefireReportsFoo, 'it.com.atlassian.amps.foo.FooIntegrationTest.txt').exists(), "FooIntegrationTest.txt file should exist"
assert !new File(integrationSurefireReportsFoo, 'it.com.atlassian.amps.bar.BarIntegrationTest.txt').exists(), "BarIntegrationTest.txt file should not exist"
assert !new File(integrationSurefireReportsFoo, 'it.com.atlassian.amps.IntegrationTest.txt').exists(), "IntegrationTest.txt file did not exist"

assert new File(integrationSurefireReportsBar, 'it.com.atlassian.amps.bar.BarIntegrationTest.txt').exists(), "BarIntegrationTest.txt file should exist"
assert !new File(integrationSurefireReportsBar, 'it.com.atlassian.amps.foo.FooIntegrationTest.txt').exists(), "FooIntegrationTest.txt file should not exist"
assert !new File(integrationSurefireReportsBar, 'it.com.atlassian.amps.IntegrationTest.txt').exists(), "IntegrationTest.txt file did not exist"