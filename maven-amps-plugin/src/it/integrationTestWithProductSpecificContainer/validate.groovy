def product = binding.product
product = 'amps'.equals(product) ? 'refapp' : product

def logFile = new File(basedir, "target/jira/home/log/atlassian-jira.log")
assert logFile.exists(), "Integration tests should have run and created test reports in $logFile"

logFile.eachLine {
    if (it.contains('Application Server')) {
        println it
        assert it.contains('Apache Tomcat/8.5.40')
    }
}
