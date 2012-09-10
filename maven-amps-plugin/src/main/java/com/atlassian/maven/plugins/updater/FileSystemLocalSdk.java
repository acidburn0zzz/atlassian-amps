package com.atlassian.maven.plugins.updater;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Default LocalSdk implementation for SDK installed on filesystem.
 */
public class FileSystemLocalSdk implements LocalSdk {

    private static final String INSTALLTYPE_FILE_NAME = "installtype.txt" ;

    @Override
    public SdkPackageType sdkPackageType() {
        String sdkHome = sdkHomeDir();
        // look for a file installtype.txt which is delivered in the SDK packages;
        // it tells us what kind of install this is and, therefore, what package
        // to download from Marketplace.
        File installType = new File(sdkHome, INSTALLTYPE_FILE_NAME);
        SdkPackageType detectedType;
        if (installType.exists() && installType.canRead()) {
            String packageType = getInstallType(installType);
            try {
                detectedType = SdkPackageType.getType(packageType);
            } catch (IllegalArgumentException e) {
                // no match found for package type, fall back to tar.gz
                detectedType = SdkPackageType.TGZ;
            }
        } else {
            // assume it was installed from a tar.gz
            detectedType = SdkPackageType.TGZ;
        }
        return detectedType;
    }

    @Override
    public String sdkHomeDir() {
        return System.getenv("ATLAS_HOME");
    }

    private String getInstallType(File file) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            return in.readLine().trim();
        } catch (FileNotFoundException e) {
            // shouldn't happen, as we did the check above
            throw new RuntimeException("file " + file.getAbsolutePath()
                    + " wasn't found, even though it exists");
        } catch (IOException e) {
            throw new RuntimeException("couldn't read from file " + file.getAbsolutePath()
                    + " even though we checked it for readability");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

}
