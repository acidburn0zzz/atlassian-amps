MAX_RETRIES=6
WAIT_PERIOD_IN_SECONDS=5
DEFAULT_DEBUG_PORT=5005

debugPort = Integer.getInteger('jvm.debug.port', DEFAULT_DEBUG_PORT);
println "Ensuring debug port:${debugPort} is closed"

// We need to ensure that the debug port has closed from previous Tomcat instances
// So we repeatedly try to open the port until we fail

debugPortClosed = false;
for(int attempt=0; attempt<MAX_RETRIES; attempt++)
{
    try
    {
        socket = new Socket('localhost', debugPort);
        println "Debug port is still open - waiting for ${WAIT_PERIOD_IN_SECONDS} seconds";
        sleep WAIT_PERIOD_IN_SECONDS * 1000;
    }
    catch (final IOException e) {
        println "Port has closed";
        debugPortClosed = true;
        break;
    }
}

assert debugPortClosed, "Debug port ${debugPort} should be closed before commencing test"