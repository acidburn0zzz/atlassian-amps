package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import javax.annotation.Nonnull;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;

public final class ProductHandlerUtil
{
    private ProductHandlerUtil()
    {
        throw new UnsupportedOperationException("Do not implement");
    }

    public static List<ProductArtifact> toArtifacts(String val)
    {
        if (StringUtils.isEmpty(val))
        {
            return Lists.newArrayList();
        }

        return Arrays.stream(val.split(",")).map((artifact) -> {
            final String[] items = artifact.split(":");
            if (items.length < 2 || items.length > 3)
            {
                throw new IllegalArgumentException("Invalid artifact pattern: " + artifact);
            }
            final String groupId = items[0];
            final String artifactId = items[1];
            final String version = (items.length == 3 ? items[2] : "LATEST");
            return new ProductArtifact(groupId, artifactId, version);
        }).collect(Collectors.toList());
    }

    /**
     * Ping the product until it's up or stopped
     * @param startingUp true if applications are expected to be up; false if applications are expected to be brought down
     * @throws MojoExecutionException if the product didn't have the expected behaviour before the timeout
     */
    public static void pingRepeatedly(@Nonnull Product product, boolean startingUp, @Nonnull Log log) throws MojoExecutionException
    {
        final int port = product.getUseHttps() ? product.getHttpsPort() : product.getHttpPort();
        if (port != 0)
        {
            final String startStop = startingUp ? "start" : "stop";
            final String url = product.getProtocol() + "://" + product.getServer() + ":" + port + StringUtils.defaultString(product.getContextPath(), "");
            final int timeout = startingUp ? product.getStartupTimeout() : product.getShutdownTimeout();
            final long end = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);

            boolean success = false;
            String lastMessage = "";

            final URL urlToPing;
            final Optional<SSLFactoryAndVerifier> httpsConfig;
            try
            {
                urlToPing = new URL(url);
                httpsConfig = configureConnection(product.getUseHttps());
            }
            catch (MalformedURLException |NoSuchAlgorithmException |KeyManagementException e)
            {
                throw new MojoExecutionException(String.format("The product %s didn't %s after %ds at %s. %s",
                        product.getInstanceId(), startStop, TimeUnit.MILLISECONDS.toSeconds(timeout), url, e.getMessage()));
            }

            // keep retrieving from the url until a good response is returned, under a time limit.
            HttpURLConnection connection = null;
            while (!success && System.nanoTime() < end)
            {
                try
                {
                    connection = (HttpURLConnection) urlToPing.openConnection();
                    int response = connection.getResponseCode();
                    // Tomcat returns 404 until the webapp is up
                    lastMessage = "Last response code is " + response;
                    success = startingUp ? response < 400 : response >= 400;
                }
                catch (Exception e)
                {
                    lastMessage = e.getMessage();
                    success = !startingUp;
                }

                if (!success)
                {
                    log.info("Waiting for " + url + " to " + startStop);
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.getInputStream().close();
                }
                catch (IOException e)
                {
                    // Don't do anything
                }
            }

            httpsConfig.ifPresent((config) -> {
                HttpsURLConnection.setDefaultSSLSocketFactory(config.sslSocketFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(config.verifier);
            });

            if (!success)
            {
                throw new MojoExecutionException(String.format("The product %s didn't %s after %ds at %s. %s",
                        product.getInstanceId(), startStop, TimeUnit.MILLISECONDS.toSeconds(timeout), url, lastMessage));
            }
        }
    }

    private static class SSLFactoryAndVerifier
    {
        public final HostnameVerifier verifier;
        public final SSLSocketFactory sslSocketFactory;
        public SSLFactoryAndVerifier(HostnameVerifier verifier, SSLSocketFactory factory)
        {
            this.verifier = verifier;
            this.sslSocketFactory = factory;
        }
    }

    private static Optional<SSLFactoryAndVerifier> configureConnection(boolean useHttps) throws NoSuchAlgorithmException, KeyManagementException
    {
        if (useHttps)
        {
            final HostnameVerifier verifier = HttpsURLConnection.getDefaultHostnameVerifier();
            final SSLSocketFactory sslSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
            // set the connections to accept every ssl certificates
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager()
            {
                @Override public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException
                {  }

                @Override public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {  }

                @Override public X509Certificate[] getAcceptedIssuers() { return null; }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
            return Optional.of(new SSLFactoryAndVerifier(verifier, sslSocketFactory));
        }
        return Optional.empty();
    }

}
