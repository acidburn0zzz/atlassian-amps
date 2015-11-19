def product = binding.product
product = 'amps'.equals(product) ? 'refapp' : product

def integrationSurefireReports = new File(basedir, "target/group-__no_test_group__/tomcat6x/surefire-reports")
assert integrationSurefireReports.exists(), "Integration tests should have run and created test reports in $integrationSurefireReports"

assert new File(integrationSurefireReports, 'it.com.atlassian.amps.IntegrationTest.txt').exists(), "IntegrationTest.txt file did not exist"
