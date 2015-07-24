import static groovyx.net.http.Method.GET
import groovyx.net.http.HTTPBuilder
import java.util.Properties
import org.apache.http.conn.HttpHostConnectException

final File ampsFile = new File("${project.build.directory}/amps.properties")

assert ampsFile.exists(), "amps.properties doesn't exists at $ampsFile.absolutePath"

final Properties amps = new Properties();
ampsFile.withInputStream { amps.load(it) }

new HTTPBuilder("http://localhost:${amps['http.port']}${amps['context.path']}/plugins/servlet/qr").request(GET) {
    response.success = { assert it.statusLine.statusCode < 400 , "Expected status code below 400 on QuickReload manage page" }
    response.failure = { assert false, "The HTTP GET should have succeeded on QuickReload manage page" }
}
    
