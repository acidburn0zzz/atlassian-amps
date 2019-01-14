package com.atlassian.maven.plugins.amps.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FecruFullVersionGetter {

    /**
     * Generates a message indicating that the full version could not be found and shows a link to the page where
     * all fecru product versions can be found.
     *
     * @param productVersion the version for which the full product version could not be found
     * @return the generated message
     */
    public static String generateNoVersionMessage(String productVersion) {
        String message = "There is no valid full version of " + productVersion + ". Please double check your input\n" +
                "\tThe full list of versions can be found at: " +
                "\n\thttps://packages.atlassian.com/content/repositories/atlassian-public/com/atlassian/fecru/amps-fecru/";
        return message;
    }

    /**
     * Generates a message indicating a potential full version that has been found for the productVersion and how to
     * start Fecru with the full version for the run command.
     *
     * @param productVersion the version that was requested
     * @param version the full version which was found to be a match for the productVersion
     * @return the generated message
     */
    public static String generateFullVersionMessage(String productVersion, String version) {
        String message = "You entered: " + productVersion + " as your version, this is not a version." +
                "\n\tDid you mean?: " + version + "\n\tPlease re-run with the correct version (atlas-run -v " + version + ")";
        return message;
    }

    /**
     * Generates a message indicating a potential full version that has been found for the productVersion and how to
     * start Fecru with the full version for the run-standalone command.
     *
     * @param productVersion the version that was requested
     * @param version the full version which was found to be a match for the productVersion
     * @return the generated message
     */
    public static String generateFullVersionMessageStandalone(String productVersion, String version) {
        String message = "You entered: " + productVersion + " as your version, this is not a version." +
                "\n\tDid you mean?: " + version + "\n\tPlease re-run with the correct version (atlas-run-standalone --product " +
                "fecru -v " + version + ")";
        return message;
    }

    /**
     * Makes a urlConnection to the page listing all Fecru product versions and searches for a match with provided
     * versionInput.
     *
     * @param versionInput the version that was requested
     * @return full version if a match is found or an Optional object if no match is found
     * @throws IOException if there is an error creating the urlConnection
     */
    public static Optional<String> getFullVersion(String versionInput) throws IOException {
        Pattern p = Pattern.compile(".*>(" + versionInput + "-[0-9]{14}).*");
        Matcher m;
        String correctVersion = null;
        URLConnection connection = new URL("https://packages.atlassian.com/content/repositories/atlassian-public/com/atlassian/fecru/amps-fecru/").openConnection();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String inputLine;
            // Read each line of the page and parse it to see the version number
            while ((inputLine = in.readLine()) != null) {
                m = p.matcher(inputLine);
                if (m.matches()) {
                    correctVersion = m.group(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(correctVersion);
    }
}
