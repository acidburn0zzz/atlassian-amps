You have successfully created a plugin using the Cross Product plugin archetype!

Here are the maven commands you'll use most frequently:

* mvn amps:run     -- installs this plugin into the refapp and starts it on http://<machinename>:5990/refapp
* mvn amps:debug   -- same as previous, but allows a debugger to attach at port 5005
* -Dproduct=<name> -- parameter that can be added to the previous two commands to test against a different product.
                      Options and their ports are: confluence (1990), jira (2990), fecru (3990), and bamboo (6990).
                      Example: mvn amps:run -Dproduct=confluence
* mvn install      -- build project and run tests (use -DskipTests here or in above commands to skip the tests)
* mvn clean        -- remove build-generated "target" directories in project
