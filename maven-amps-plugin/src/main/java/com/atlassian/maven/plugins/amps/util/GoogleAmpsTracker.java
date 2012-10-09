package com.atlassian.maven.plugins.amps.util;

import java.util.Locale;
import java.util.prefs.Preferences;

import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.VisitorData;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;

/**
 * @since version
 */
public class GoogleAmpsTracker
{
    private static final String PREF_NAME = "ga_visitor_data";
    private static final String TRACKING_CODE = "UA-6032469-43";
    private static final String AMPS = "AMPS";
    private static final String EVENT_PREFIX = AMPS + ":";

    public static final String CREATE_PLUGIN = "Create Plugin";
    public static final String DEBUG = "Debug";
    public static final String RUN = "Run";
    public static final String RUN_STANDALONE = "Run Standalone";
    public static final String RELEASE = "Release";
    public static final String CREATE_HOME_ZIP = "Create Home Zip";
    public static final String CREATE_PLUGIN_MODULE = "Create Plugin Module";
    public static final String SDK_FIRST_RUN = "SDK First Run";

    private final AnalyticsConfigData config;
    private final JGoogleAnalyticsTracker tracker;
    private final Log mavenLogger;
    private String productId;
    private String ampsVersion;
    private String sdkVersion;

    public GoogleAmpsTracker(String productId, String sdkVersion, String ampsVersion, Log mavenLogger)
    {
        this.productId = productId;
        this.ampsVersion = ampsVersion;
        this.sdkVersion = sdkVersion;
        this.mavenLogger = mavenLogger;
        
        this.config = new AnalyticsConfigData(TRACKING_CODE, loadVisitorData());
        config.setFlashVersion(ampsVersion);
        
        this.tracker = new JGoogleAnalyticsTracker(config, JGoogleAnalyticsTracker.GoogleAnalyticsVersion.V_4_7_2);

        tracker.setDispatchMode(JGoogleAnalyticsTracker.DispatchMode.MULTI_THREAD);
    }

    private VisitorData loadVisitorData()
    {
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        String gaVisitorData = prefs.get(PREF_NAME, null);
        VisitorData visitorData = null;

        if (gaVisitorData == null)
        {
            visitorData = VisitorData.newVisitor();
            saveVisitorData(visitorData);
        }
        else
        {
            try
            {
                visitorData = visitorDataFromString(gaVisitorData);
            }
            catch (Exception e)
            {
                mavenLogger.warn("Couldn't parse ga visitor data from prefs; deleting");
                prefs.remove(PREF_NAME);
                visitorData = VisitorData.newVisitor();
                saveVisitorData(visitorData);
            }
        }

        return visitorData;
    }

    private VisitorData visitorDataFromString(String gaVisitorData)
    {
        String parts[] = gaVisitorData.split(":");
        int visitorId = Integer.parseInt(parts[0]);
        long timestampfirst = Long.parseLong(parts[1]);
        long timestamplast = Long.parseLong(parts[2]);
        int visits = Integer.parseInt(parts[3]);

        return VisitorData.newSession(visitorId, timestampfirst, timestamplast, visits);
    }

    private void saveVisitorData(VisitorData visitorData)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(visitorData.getVisitorId())
          .append(":")
          .append(visitorData.getTimestampFirst())
          .append(":")
          .append(visitorData.getTimestampCurrent())
          .append(":")
          .append(visitorData.getVisits());

        Preferences prefs = Preferences.userNodeForPackage(getClass());
        prefs.put(PREF_NAME,sb.toString());
    }

    public void track(String eventName)
    {
        if (tracker.isEnabled())
        {
            try
            {
                System.setProperty("http.agent",getUserAgent());
                mavenLogger.info("Sending event to Google Analytics: " + getCategoryName() + " - " + eventName);
                tracker.trackEvent(getCategoryName(), eventName);
                saveVisitorData(config.getVisitorData());
            }
            catch (Throwable t)
            {
                
            }
        }
    }

    public void track(String eventName, String label)
    {
        if (tracker.isEnabled())
        {
            try
            {
                System.setProperty("http.agent",getUserAgent());
                mavenLogger.info("Sending event to Google Analytics: " + getCategoryName() + " - " + eventName + " - " + label);
                tracker.trackEvent(getCategoryName(), eventName, label);
                saveVisitorData(config.getVisitorData());
            }
            catch (Throwable t)
            {
                
            }
        }
    }

    private String getCategoryName()
    {
        if (StringUtils.isNotBlank(productId))
        {
            return EVENT_PREFIX + productId;
        }
        else
        {
            return AMPS;
        }
    }
    
    private String getUserAgent()
    {
        StringBuilder sb = new StringBuilder();
        String browser = (null != System.getenv("ATLAS_VERSION")) ? "Atlassian-SDK" : "Atlassian-AMPS-Plugin";
        String browserVersion = (null != System.getenv("ATLAS_VERSION")) ? sdkVersion : ampsVersion;
        
        sb.append(browser + "/" + browserVersion + "(compatible; ")
          .append(browser)
          .append(" ")
          .append(browserVersion)
          .append("; ")
          .append(System.getProperty("os.name"))
          .append(" ")
          .append(System.getProperty("os.version"))
          .append("; ")
          .append(Locale.getDefault().getLanguage().toLowerCase())
          .append("-")
          .append(Locale.getDefault().getCountry().toLowerCase())
          .append(")");
        
        return sb.toString();
    }

    public VisitorData getVisitorData()
    {
        return config.getVisitorData();    
    }
    
    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setEnabled(boolean enabled)
    {
        tracker.setEnabled(enabled);
    }
}
