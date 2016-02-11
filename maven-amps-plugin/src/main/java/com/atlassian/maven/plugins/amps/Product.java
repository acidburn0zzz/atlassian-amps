package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.studio.StudioProductHandler;
import com.atlassian.maven.plugins.amps.product.studio.StudioProperties;
import com.atlassian.maven.plugins.amps.util.ArtifactRetriever;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class Product
{
    /**
     * Container to run in
     */
    protected String containerId;

    /**
     * HTTP port for the servlet containers
     */
    protected int httpPort = 0;

    /**
     * RMI port, for Tomcat this is port used to send shutdown message
     */
    protected int rmiPort = 0;

    /**
     * if we should start with https on port 443
     */
    private Boolean useHttps;

    /**
     * the HTTPS port to use.
     * @since 5.0.4
     */
    private int httpsPort;

    /**
     * The SSL certificate chain option.
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration">Tomcat SSL HOWTO</a>
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/config/http.html#SSL_Support">Tomcat SSL Support</a>
     * @since 5.0.4
     */
    private String httpsClientAuth;

    /**
     * The SSL protocols to use.
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration">Tomcat SSL HOWTO</a>
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/config/http.html#SSL_Support">Tomcat SSL Support</a>
     * @since 5.0.4
     */
    private String httpsSslProtocol;

    /**
     * The pathname of the keystore file.
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration">Tomcat SSL HOWTO</a>
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/config/http.html#SSL_Support">Tomcat SSL Support</a>
     * @since 5.0.4
     */
    private String httpsKeystoreFile;

    /**
     * The password of the keystore file.
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration">Tomcat SSL HOWTO</a>
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/config/http.html#SSL_Support">Tomcat SSL Support</a>
     * @since 5.0.4
     */
    private String httpsKeystorePass;

    /**
     * The alias of the certificate to use.
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration">Tomcat SSL HOWTO</a>
     * @see <a href="http://tomcat.apache.org/tomcat-7.0-doc/config/http.html#SSL_Support">Tomcat SSL Support</a>
     * @since 5.0.4
     */
    private String httpsKeyAlias;

    /**
     * Cargo httpSecure flag
     * @see <a href="http://svn.codehaus.org/cargo/core/trunk/containers/tomcat/src/main/java/org/codehaus/cargo/container/tomcat/TomcatPropertySet.java">Cargo Tomcat Properties</a>
     * @since 5.0.4
     */
    private Boolean httpsHttpSecure;

    /**
     * Application context path, in the format: /context-path
     */
    protected String contextPath;

    /**
     * Application server
     */
    protected String server;

    /**
     * Webapp version
     */
    protected String version;

    /**
     * JVM arguments to pass to cargo
     */
    protected String jvmArgs = "";

    /**
     * Debug arguments to pass to cargo as JVM arguments
     */
    protected String debugArgs = "";

    /**
     * A log4j properties file
     */
    protected File log4jProperties;

    /**
     * The test resources version
     */
    protected String productDataVersion;

    /**
     * The path to a custom test resources zip or a directory. Takes precedence over dataVersion.
     * The data from this path will be copied into the home directory.
     */
    protected String productDataPath = "";

    /**
     * The path to the product's home directory. Takes precedence over dataPath.
     * The data from this path will be used directly (read/write) by the product.
     */
    protected String dataHome = "";

    /**
     */
    private List<Application> applications = new ArrayList<Application>();

    /**
     */
    private List<ProductArtifact> pluginArtifacts = new ArrayList<ProductArtifact>();

    /**
     */
    private List<ProductArtifact> libArtifacts = new ArrayList<ProductArtifact>();

    /**
     */
    private List<ProductArtifact> bundledArtifacts = new ArrayList<ProductArtifact>();

    /**
     * SAL version
     */
    private String salVersion;

    /**
     * Atlassian Plugin Development Kit (PDK) version
     */
    private String pdkVersion;

    /**
     * Atlassian REST module version
     */
    private String restVersion;

    /**
     * Version of the Felix OSGi web console
     */
    private String webConsoleVersion;

    /**
     * Flag to indicate whether or not to enable automatic bundling of Fastdev.
     */
    private Boolean enableFastdev;

    /**
     * Version of the Fastdev plugin
     */
    private String fastdevVersion;

    /**
     * Flag to indicate whether or not to enable automatic bundling of DevToolbox.
     */
    private Boolean enableDevToolbox;

    /**
     * Version of the Developer Toolbox plugin
     */
    private String devToolboxVersion;

    /**
     * Should QuickReload be enabled.
     */
    private Boolean enableQuickReload;

    /**
     * Version of QuickReload.
     */
    private String quickReloadVersion;

    /**
     * Flag to indicate whether or not to enable automatic bundling of PDE.
     */
    private Boolean enablePde;

    /**
     * Version of the PDE plugin
     */
    private String pdeVersion;
    
    /**
     * Product id - nickname of the product to run
     */
    protected String id;

    /**
     * The name of the instance of the product
     */
    protected String instanceId;

    private ArtifactRetriever artifactRetriever;

    /**
     * Flag to indicate whether or not to install the plugin
     */
    private Boolean installPlugin;

    /**
     * The system properties to set for the product
     */
    private Map<String,Object> systemProperties = new HashMap<String,Object>();

    /**
     * File the container should log to.
     */
    private String output;

    /**
     * Port for debugging
     */
    private int jvmDebugPort;

    /**
     * How long to wait for product startup, in milliseconds; if not specified, default is determined by AbstractProductHandlerMojo
     */
    private int startupTimeout = 0;

    /**
     * How long to wait for product shutdown, in milliseconds; if not specified, default is determined by AbstractProductHandlerMojo
     */
    private int shutdownTimeout = 0;

    /**
     * Waits until the application is up before proceeding to the next one (blocking call).<ul>
     * <li>If -Dparallel is not specified, default is TRUE for all products.</li>
     * <li>If -Dparallel is specified, default is FALSE except for Studio-Crowd and FeCru.</li>
     * <li>The pom.xml overrides the default values.</li>
     * <li>Use -Dparallel to start products in parallel. {@link AbstractProductHandlerMojo#setParallelMode(List)} sets the default values according to this parameter.</li>
     * </ul>
     */
    private Boolean synchronousStartup;

    /**
     * Waits until product is fully started - this is added for JIRA - JIRA new asynchronous start -
     * JIRA is performing minimal initialization and then whole plugin system startup is performed in background.<br>
     * This can interfere with integration tests - AMPS starts them after minimal initialization and some may crash as
     * plugin system is not fully started (if they do not perform check if jira is fully started).<br>
     * This flag prevents such situation and forces product to perform full initializaion before continuing to next
     * step.
     */
    private Boolean awaitFullInitialization;

    /**
     * An optional override of the webapp's groupId
     */
    private String groupId;

    /**
     * An optional override of the webapp's artifactId
     */
    private String artifactId;

    /**
     * The studio configuration which is shared for all products in the same
     * studio instance. Null if products are not studio or not yet configured.
     * <p>
     * {@link StudioProductHandler#configure(Product, List)} will set this value.
     * It must be called before Studio products are launched.
     */
    protected StudioProperties studioProperties;

    /**
     * Only applies to Studio
     * List of 'sub'-products that are managed by this Studio instance.
     * Optional. Default value is: studio-crowd, studio-confluence, studio-fecru, studio-bamboo, studio-jira.
     */
    protected List<String> instanceIds = new ArrayList<String>();

    /**
     * Only applies to Studio
     * Set 'true' if GApps is enabled. Default is 'false'
     */
    protected String gappsEnabled;

    /**
     * Only applies to Studio
     * The GApps domain, if GApps is enabled
     */
    protected String gappsDomain;

    /**
     * Only applies to StudioFecru
     * Tells whether shutdown is enabled for Fisheye. This property is passed on in the properties files.
     */
    protected Boolean shutdownEnabled;

    /**
     * Registers a JNDI datasource using cargo.datasource.datasource.
     * <ul>
     * <li>Default values depend on the product.</li>
     * <li>Default values will be applied to the first datasource if its definition is incomplete.</li>
     * <li>Only Jira, Studio-Jira, Studio-Bamboo, Studio-Confluence and Studio-Crowd have a datasource by default, and they use HSQL or H2.</li>
     * <li>Other products can use datasources if you configure them this way during the setup process (Requires to
     * start with an empty data home).</li>
     * <li>There is a simple prerequisite to configuring multiple datasources. You must use {@code <parallel>true</parallel>},
     * so that a recent version of CodeHaus Cargo is used.</li>
     * </ul>
     * Example:
     * <pre>{@code
     * <products>
     *   <product>
     *     <id>jira</id>
     *     <instanceId>jira50</instanceId>
     *     <version>5.0</version>
     *     <dataVersion>5.0</dataVersion>
     *     <dataSources>
     *         <dataSource>
     *             <jndi>jdbc/JiraDS</jndi>
     *             <url>jdbc:postgresql://localhost:5432/jira</url>
     *             <driver>org.postgresql.jdbcDriver</driver>
     *             <username>jira</username>
     *             <password>jira</password>
     *             <libArtifacts>
     *               <libArtifact>
     *                 <groupId>postgresql</groupId>
     *                 <artifactId>postgresql</artifactId>
     *                 <version>9.1-901-1.jdbc4</version>
     *               </libArtifact>
     *             </libArtifacts>
     *         </dataSource>
     *     </dataSources>
     *   </product>
     * </products>
     * }
     * </pre>
     * 
     */
    protected List<DataSource> dataSources;

    // The home directory shared between multiple instances in a cluster (added for JIRA)
    private String sharedHome;

    // The port for the Apache JServ Protocol; defaults to the web container's default value
    private int ajpPort;

    /**
     * Creates a new product that is merged with this one, where the properties in this one override the passed
     * in product.
     * @param product The product to merge with
     * @return A new product
     */
    public Product merge(final Product product)
    {
        final Product prod = new Product();
        prod.setOutput(output == null ? product.getOutput() : output);

        Map<String,Object> sysProps = new HashMap<String,Object>();
        sysProps.putAll(product.getSystemPropertyVariables());
        sysProps.putAll(systemProperties);
        prod.setSystemPropertyVariables(sysProps);

        prod.setInstallPlugin(installPlugin == null ? product.isInstallPlugin() : installPlugin);
        prod.setArtifactRetriever(artifactRetriever == null ? product.getArtifactRetriever() : artifactRetriever);
        prod.setId(id == null ? product.getId() : id);
        prod.setInstanceId(instanceId == null ? product.getInstanceId() : instanceId);
        prod.setWebConsoleVersion(webConsoleVersion == null ? product.getWebConsoleVersion() : webConsoleVersion);
        prod.setEnableFastdev(enableFastdev == null ? product.isEnableFastdev() : enableFastdev);
        prod.setFastdevVersion(fastdevVersion == null ? product.getFastdevVersion() : fastdevVersion);
        prod.setEnableDevToolbox(enableDevToolbox == null ? product.isEnableDevToolbox() : enableDevToolbox);
        prod.setDevToolboxVersion(devToolboxVersion == null ? product.getDevToolboxVersion() : devToolboxVersion);
        prod.setEnableQuickReload(enableQuickReload == null ? product.isEnableQuickReload() : enableQuickReload);
        prod.setQuickReloadVersion(quickReloadVersion == null ? product.getQuickReloadVersion() : quickReloadVersion);
        prod.setEnablePde(enablePde == null ? product.isEnablePde() : enablePde);
        prod.setPdeVersion(pdeVersion == null ? product.getPdeVersion() : pdeVersion);
        prod.setRestVersion(restVersion == null ? product.getRestVersion() : restVersion);
        prod.setPdkVersion(pdkVersion == null ? product.getPdkVersion() : pdkVersion);
        prod.setSalVersion(salVersion == null ? product.getSalVersion() : salVersion);

        prod.setBundledArtifacts(bundledArtifacts.isEmpty() ? product.getBundledArtifacts() : bundledArtifacts);
        prod.setPluginArtifacts(pluginArtifacts.isEmpty() ? product.getPluginArtifacts() : pluginArtifacts);
        prod.setLibArtifacts(libArtifacts.isEmpty() ? product.getLibArtifacts() : libArtifacts);
        prod.setApplications(applications.isEmpty() ? product.getApplications() : applications);

        prod.setDataPath(StringUtils.isBlank(productDataPath) ? product.getDataPath() : productDataPath);
        prod.setDataVersion(productDataVersion == null ? product.getDataVersion() : productDataVersion);
        prod.setDataHome(dataHome == null ? product.getDataHome() : dataHome);
        prod.setLog4jProperties(log4jProperties == null ? product.getLog4jProperties() : log4jProperties);
        prod.setJvmArgs(StringUtils.stripToNull(jvmArgs) == null ? product.getJvmArgs() : jvmArgs);
        prod.setDebugArgs(StringUtils.stripToNull(debugArgs) == null ? product.getDebugArgs() : debugArgs);
        prod.setDataSources(dataSources == null ? product.getDataSources() : dataSources);
        prod.setGroupId(groupId == null ? product.getGroupId() : groupId);
        prod.setArtifactId(artifactId == null ? product.getArtifactId() : artifactId);
        prod.setVersion(version == null ? product.getVersion() : version);

        prod.setServer(server == null ? product.getServer() : server);
        prod.setContextPath(contextPath == null ? product.getContextPath() : contextPath);
        prod.setContainerId(containerId == null ? product.getContainerId() : containerId);
        prod.setRmiPort(rmiPort == 0 ? product.getRmiPort() : rmiPort);
        prod.setHttpPort(httpPort == 0 ? product.getHttpPort() : httpPort);
        prod.setAjpPort(ajpPort == 0 ? product.getAjpPort() : ajpPort);
        prod.setJvmDebugPort(jvmDebugPort == 0 ? product.getJvmDebugPort() : jvmDebugPort);
        prod.setUseHttps(useHttps == null ? product.getUseHttps() : useHttps);

        prod.setStartupTimeout(startupTimeout == 0 ? product.getStartupTimeout() : startupTimeout);
        prod.setShutdownTimeout(shutdownTimeout == 0 ? product.getShutdownTimeout() : shutdownTimeout);
        prod.setSynchronousStartup(synchronousStartup == null ? product.getSynchronousStartup() : synchronousStartup);
        prod.setSharedHome(sharedHome == null ? product.getSharedHome() : sharedHome);

        // Studio-related properties
        prod.setStudioProperties(studioProperties == null ? product.getStudioProperties() : studioProperties);
        prod.setInstanceIds(instanceIds == null ? product.getInstanceIds() : instanceIds);
        prod.setShutdownEnabled(shutdownEnabled == null ? product.getShutdownEnabled() : shutdownEnabled);

        // https related properties
        prod.setHttpsPort(httpsPort == 0 ? product.getHttpsPort() : httpsPort);
        prod.setHttpsClientAuth(httpsClientAuth == null ? product.getHttpsClientAuth() : httpsClientAuth);
        prod.setHttpsSSLProtocol(httpsSslProtocol == null ? product.getHttpsSSLProtocol() : httpsSslProtocol);
        prod.setHttpsKeystoreFile(httpsKeystoreFile == null ? product.getHttpsKeystoreFile() : httpsKeystoreFile);
        prod.setHttpsKeystorePass(httpsKeystorePass == null ? product.getHttpsKeystorePass() : httpsKeystorePass);
        prod.setHttpsKeyAlias(httpsKeyAlias == null ? product.getHttpsKeyAlias() : httpsKeyAlias);
        prod.setHttpsHttpSecure(httpsHttpSecure == null ? product.getHttpsHttpSecure() : httpsHttpSecure);
        prod.setAwaitFullInitialization(awaitFullInitialization==null ? product.isAwaitFullInitialization() : awaitFullInitialization);

        return prod;
    }

    public String getContainerId()
    {
        return containerId;
    }

    public void setContainerId(String containerId)
    {
        this.containerId = containerId;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public int getRmiPort()
    {
        return rmiPort;
    }

    public void setRmiPort(int rmiPort)
    {
        this.rmiPort = rmiPort;
    }

    public Boolean getUseHttps()
    {
        return useHttps;
    }

    public void setUseHttps(Boolean useHttps)
    {
        this.useHttps = useHttps;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsPort(final int httpsPort)
    {
        this.httpsPort = httpsPort;
    }

    /**
     * @since 5.0.4
     */
    public int getHttpsPort()
    {
        return this.httpsPort;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsClientAuth(final String httpsClientAuth)
    {
        this.httpsClientAuth = httpsClientAuth;
    }

    /**
     * @since 5.0.4
     */
    public String getHttpsClientAuth()
    {
        return this.httpsClientAuth;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsSSLProtocol(final String httpsSslProtocol)
    {
        this.httpsSslProtocol = httpsSslProtocol;
    }

    /**
     * @since 5.0.4
     */
    public String getHttpsSSLProtocol()
    {
        return this.httpsSslProtocol;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsKeystoreFile(final String httpsKeystoreFile)
    {
        this.httpsKeystoreFile = httpsKeystoreFile;
    }

    /**
     * @since 5.0.4
     */
    public String getHttpsKeystoreFile()
    {
        return this.httpsKeystoreFile;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsKeystorePass(final String httpsKeystorePass)
    {
        this.httpsKeystorePass = httpsKeystorePass;
    }

    /**
     * @since 5.0.4
     */
    public String getHttpsKeystorePass()
    {
        return this.httpsKeystorePass;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsKeyAlias(final String httpsKeyAlias)
    {
        this.httpsKeyAlias = httpsKeyAlias;
    }

    /**
     * @since 5.0.4
     */
    public String getHttpsKeyAlias()
    {
        return this.httpsKeyAlias;
    }

    /**
     * @since 5.0.4
     */
    public void setHttpsHttpSecure(final Boolean httpsHttpSecure)
    {
        this.httpsHttpSecure = httpsHttpSecure;
    }

    /**
     * @since 5.0.4
     */
    public Boolean getHttpsHttpSecure()
    {
        return this.httpsHttpSecure;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public String getJvmArgs()
    {
        return jvmArgs;
    }

    public void setJvmArgs(String jvmArgs)
    {
        this.jvmArgs = jvmArgs == null ? "" : jvmArgs;
    }

    public String getDebugArgs()
    {
        return debugArgs;
    }

    public void setDebugArgs(String debugArgs)
    {
        this.debugArgs = debugArgs == null ? "" :debugArgs;
    }

    public ArtifactRetriever getArtifactRetriever()
    {
        return artifactRetriever;
    }

    public void setArtifactRetriever(ArtifactRetriever artifactRetriever)
    {
        this.artifactRetriever = artifactRetriever;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDataVersion()
    {
        return productDataVersion;
    }

    public void setDataVersion(String productDataVersion)
    {
        this.productDataVersion = productDataVersion;
    }

    /**
     * @deprecated since 3.2
     */
    public String getProductDataVersion()
    {
        return productDataVersion;
    }

    /**
     * @deprecated since 3.2
     */
    public void setProductDataVersion(String productDataVersion)
    {
        this.productDataVersion = productDataVersion;
    }

    /**
     * The path to a custom test resources zip or a directory. Takes precedence over dataVersion.
     * The data from this path will be copied into the home directory.
     */
    public String getDataPath()
    {
        return productDataPath;
    }

    /**
     * The path to a custom test resources zip or a directory. Takes precedence over dataVersion.
     * The data from this path will be copied into the home directory.
     */
    public void setDataPath(String productDataPath)
    {
        this.productDataPath = productDataPath;
    }

    /**
     * @deprecated since 3.2
     */
    public String getProductDataPath()
    {
        return productDataPath;
    }

    /**
     * @deprecated since 3.2
     */
    public void setProductDataPath(String productDataPath)
    {
        this.productDataPath = productDataPath;
    }

    public List<Application> getApplications()
    {
        return applications;
    }

    public void setApplications(final List<Application> applications)
    {
        this.applications = applications;
    }

    public List<ProductArtifact> getPluginArtifacts()
    {
        return pluginArtifacts;
    }

    public void setPluginArtifacts(List<ProductArtifact> pluginArtifacts)
    {
        this.pluginArtifacts = pluginArtifacts;
    }

    public List<ProductArtifact> getLibArtifacts()
    {
        return libArtifacts;
    }

    public void setLibArtifacts(List<ProductArtifact> libArtifacts)
    {
        this.libArtifacts = libArtifacts;
    }

    public List<ProductArtifact> getBundledArtifacts()
    {
        return bundledArtifacts;
    }

    public void setBundledArtifacts(List<ProductArtifact> bundledArtifacts)
    {
        this.bundledArtifacts = bundledArtifacts;
    }

    public File getLog4jProperties()
    {
        return log4jProperties;
    }

    public void setLog4jProperties(File log4jProperties)
    {
        this.log4jProperties = log4jProperties;
    }

    public String getRestVersion()
    {
        return restVersion;
    }

    public void setRestVersion(String restVersion)
    {
        this.restVersion = restVersion;
    }

    public String getSalVersion()
    {
        return salVersion;
    }

    public void setSalVersion(String salVersion)
    {
        this.salVersion = salVersion;
    }

    public String getPdkVersion()
    {
        return pdkVersion;
    }

    public void setPdkVersion(String pdkVersion)
    {
        this.pdkVersion = pdkVersion;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public Boolean isInstallPlugin()
    {
        return installPlugin;
    }

    public void setInstallPlugin(final Boolean installPlugin)
    {
        this.installPlugin = installPlugin;
    }

    public String getWebConsoleVersion()
    {
        return webConsoleVersion;
    }

    public Boolean isEnableFastdev()
    {
        return enableFastdev;
    }

    public void setEnableFastdev(final Boolean enableFastdev)
    {
        this.enableFastdev = enableFastdev;
    }

    public String getFastdevVersion()
    {
        return fastdevVersion;
    }

    public void setFastdevVersion(String fastdevVersion)
    {
        this.fastdevVersion = fastdevVersion;
    }

    public Boolean isEnableDevToolbox()
    {
        return enableDevToolbox;
    }

    public void setEnableDevToolbox(final Boolean enableDevToolbox)
    {
        this.enableDevToolbox = enableDevToolbox;
    }

    public String getQuickReloadVersion()
    {
        return quickReloadVersion;
    }

    public void setQuickReloadVersion(final String quickReloadVersion)
    {
        this.quickReloadVersion = quickReloadVersion;
    }

    public Boolean isEnableQuickReload()
    {
        return enableQuickReload;
    }

    public void setEnableQuickReload(final Boolean enableQuickReload)
    {
        this.enableQuickReload = enableQuickReload;
    }

    public String getDevToolboxVersion()
    {
        return devToolboxVersion;
    }

    public void setDevToolboxVersion(String devToolboxVersion)
    {
        this.devToolboxVersion = devToolboxVersion;
    }

    public Boolean isEnablePde()
    {
        return enablePde;
    }

    public void setEnablePde(Boolean enablePde)
    {
        this.enablePde = enablePde;
    }

    public String getPdeVersion()
    {
        return pdeVersion;
    }

    public void setPdeVersion(String pdeVersion)
    {
        this.pdeVersion = pdeVersion;
    }

    public void setWebConsoleVersion(String webConsoleVersion)
    {
        this.webConsoleVersion = webConsoleVersion;
    }

    /**
     * @deprecated Since 3.2, use systemPropertyVariables
     */
    public void setSystemProperties(Properties systemProperties)
    {
        this.systemProperties.putAll((Map) systemProperties);
    }

    /**
     * @deprecated Since 3.2, use systemPropertyVariables
     */
    public Properties getSystemProperties()
    {
        Properties props = new Properties();
        props.putAll(systemProperties);
        return props;
    }

    public void setSystemPropertyVariables(Map<String,Object> systemProperties)
    {
        this.systemProperties = systemProperties;
    }

    public Map<String,Object> getSystemPropertyVariables()
    {
        return systemProperties;
    }

    public String getOutput()
    {
        return output;
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public int getJvmDebugPort()
    {
        return jvmDebugPort;
    }

    public void setJvmDebugPort(int jvmDebugPort)
    {
        this.jvmDebugPort = jvmDebugPort;
    }

    public int getStartupTimeout()
    {
        return startupTimeout;
    }

    public void setStartupTimeout(int startupTimeout)
    {
        this.startupTimeout = startupTimeout;
    }

    public int getShutdownTimeout()
    {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(int shutdownTimeout)
    {
        this.shutdownTimeout = shutdownTimeout;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public StudioProperties getStudioProperties()
    {
        return studioProperties;
    }

    public void setStudioProperties(StudioProperties studioProperties)
    {
        this.studioProperties = studioProperties;
    }

    public List<String> getInstanceIds()
    {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds)
    {
        this.instanceIds = instanceIds;
    }

    public String getGappsEnabled()
    {
        return gappsEnabled;
    }

    public void setGappsEnabled(String gappsEnabled)
    {
        this.gappsEnabled = gappsEnabled;
    }

    public String getGappsDomain()
    {
        return gappsDomain;
    }

    public void setGappsDomain(String gappsDomain)
    {
        this.gappsDomain = gappsDomain;
    }

    public void setSystemProperties(Map<String, Object> systemProperties)
    {
        this.systemProperties = systemProperties;
    }

    public Boolean getShutdownEnabled()
    {
        return shutdownEnabled;
    }

    public void setShutdownEnabled(Boolean shutdownEnabled)
    {
        this.shutdownEnabled = shutdownEnabled;
    }

    public Boolean getSynchronousStartup()
    {
        return synchronousStartup;
    }

    public void setSynchronousStartup(Boolean synchronousStartup)
    {
        this.synchronousStartup = synchronousStartup;
    }

    public String getDataHome()
    {
        return dataHome;
    }

    /**
     * The path to the product's home directory. Takes precedence over dataPath.
     * The data from this path will be used directly (read/write) by the product.
     */
    public void setDataHome(String dataHome)
    {
        this.dataHome = dataHome;
    }

    /**
     * @return the dataSources. Not null, because initialized in {@link AbstractProductHandlerMojo#setDefaultValues(Product, com.atlassian.maven.plugins.amps.product.ProductHandler)}
     * May be empty.
     */
    public List<DataSource> getDataSources()
    {
        return dataSources;
    }

    /**
     * @param dataSources the dataSources to set
     */
    public void setDataSources(List<DataSource> dataSources)
    {
        this.dataSources = dataSources;    
    }

    /**
     * Returns the shared home directory for a JIRA cluster.
     *
     * @return null if no shared home is set, otherwise the path to that directory
     */
    public String getSharedHome()
    {
        return sharedHome;
    }

    /**
     * Sets the shared home directory for a JIRA cluster.
     *
     * @param sharedHome the directory path to set (can be null)
     */
    public void setSharedHome(final String sharedHome)
    {
        this.sharedHome = sharedHome;
    }

    /**
     * Returns the AJP port for use by the web container.
     *
     * @return see above
     */
    public int getAjpPort()
    {
        return ajpPort;
    }

    /**
     * Sets the AJP port for use by the web container.
     *
     * @param ajpPort the AJP port to set
     */
    public void setAjpPort(final int ajpPort)
    {
        this.ajpPort = ajpPort;
    }

    @Override
    public String toString()
    {
        return "Product " + id + " [instanceId=" + instanceId
				+ ", " + getProtocol() + "://" + server + ":" + httpPort + contextPath + "]";
    }

    /**
     * Returns the protocol transmission scheme.
     * @return "http" or "https".
     */
    public String getProtocol()
    {
        return useHttps != null && useHttps ? "https" : "http";
    }

    public Boolean isAwaitFullInitialization() {
        return awaitFullInitialization;
    }

    public void setAwaitFullInitialization(Boolean awaitFullInitialization) {
        this.awaitFullInitialization = awaitFullInitialization;
    }
}
