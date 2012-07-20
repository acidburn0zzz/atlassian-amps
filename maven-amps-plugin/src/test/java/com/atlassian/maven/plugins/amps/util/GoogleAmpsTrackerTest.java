package com.atlassian.maven.plugins.amps.util;

import java.util.prefs.Preferences;

import com.dmurph.tracking.VisitorData;

import org.junit.Before;
import org.junit.Test;
import org.apache.maven.plugin.logging.Log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GoogleAmpsTrackerTest
{
    private static final String PREF_NAME = "ga_visitor_data";
    private Preferences prefs;
    
    @Before
    public void setup()
    {
        this.prefs = Preferences.userNodeForPackage(GoogleAmpsTracker.class); 
    }
    
    @Test
    public void firstTrackCreatesNewVisitor() throws Exception
    {
        prefs.remove(PREF_NAME);
        prefs.flush();
        
        Log logger = mock(Log.class);
        GoogleAmpsTracker tracker = new GoogleAmpsTracker("googleTrackingTester",logger);
        tracker.track("testInitialTrack");

        prefs.flush();
        String visitorData = prefs.get(PREF_NAME,null);
        
        assertTrue("visitor data was not created!",(null != visitorData));
        
    }

    @Test
    public void secondTrackCreatesNewSession() throws Exception
    {
        prefs.remove(PREF_NAME);
        prefs.flush();

        Log logger = mock(Log.class);
        GoogleAmpsTracker firstTracker = new GoogleAmpsTracker("googleTrackingTester",logger);
        firstTracker.track("testFirstTrack");

        prefs.flush();
        String firstVisData = prefs.get(PREF_NAME,null);

        assertTrue("first visitor data was not created!",(null != firstVisData));
        
        String firstParts[] = firstVisData.split(":");
        int firstVisId = Integer.parseInt(firstParts[0]);
        long firstStartTS = Long.parseLong(firstParts[1]);
        long firstPrevTS = Long.parseLong(firstParts[2]);
        int firstVisits = Integer.parseInt(firstParts[3]);

        //need to wait a second to make sure timestamps are updated
        Thread.sleep(1000);
        
        GoogleAmpsTracker secondTracker = new GoogleAmpsTracker("googleTrackingTester",logger);
        secondTracker.track("testSecondTrack");
        prefs.flush();

        String secondVisData = prefs.get(PREF_NAME,null);

        assertTrue("second visitor data was not created!",(null != secondVisData));

        String secondParts[] = secondVisData.split(":");
        int secondVisId = Integer.parseInt(secondParts[0]);
        long secondStartTS = Long.parseLong(secondParts[1]);
        long secondPrevTS = Long.parseLong(secondParts[2]);
        int secondVisits = Integer.parseInt(secondParts[3]);
        
        assertEquals("visitor id's do not match",firstVisId,secondVisId);
        assertEquals("second visits not ticked",(firstVisits + 1),secondVisits);
        assertEquals("first timestamp not preserved",firstStartTS,secondStartTS);
        assertTrue("timestamps not updated",(firstPrevTS != secondPrevTS));
    }
    
}
