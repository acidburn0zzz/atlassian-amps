package com.atlassian.maven.plugins.amps.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.atlassian.maven.plugins.amps.codegen.prompter.PrettyPrompter;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import jline.ANSIBuffer;

/**
 * @since version
 */
public class AmpsEmailSubscriberImpl extends AbstractLogEnabled implements AmpsEmailSubscriber
{
    private static final String EMAIL_SUBSCRIBE_ROOT = "https://hamlet.atlassian.com/1.0/public/email/";
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int READ_TIMEOUT = 15 * 1000;
    

    public static final List<String> YN_ANSWERS = new ArrayList<String>(Arrays.asList("Y", "y", "N", "n"));
    private boolean useAnsiColor;
    private Prompter prompter;

    public AmpsEmailSubscriberImpl()
    {
        String mavencolor = System.getenv("MAVEN_COLOR");
        if (mavencolor != null && !mavencolor.equals(""))
        {
            useAnsiColor = Boolean.parseBoolean(mavencolor);
        } else
        {
            useAnsiColor = false;
        }
    }

    @Override
    public void promptForSubscription()
    {
            try
            {
                if (useAnsiColor)
                {
                    promptForEmailAnsi();
                } else
                {
                    promptForEmailPlain();
                }
            }
            catch (Throwable e)
            {
                
            }
    }

    private void promptForEmailPlain() throws PrompterException, IOException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Would you like to subscribe to the Atlassian developer mailing list?");

        boolean signUp = promptForBoolean(builder.toString(), "Y");
        
        if(signUp)
        {
            String email = prompter.prompt("Please enter your email address (leave blank to cancel):");
            if(StringUtils.isNotBlank(email) && EmailValidator.getInstance().isValid(email))
            {
                doSubscribe(email);
            }
        }
    }

    private void promptForEmailAnsi() throws PrompterException, IOException
    {
        ANSIBuffer ansiBuffer = new ANSIBuffer();
        ansiBuffer.append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.FG_YELLOW))
                  .append("Would you like to subscribe to the Atlassian developer mailing list?")
                  .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.OFF));

        boolean signUp = promptForBoolean(ansiBuffer.toString(),"Y");
        
        if(signUp)
        {
            ANSIBuffer ansiEmailBuffer = new ANSIBuffer();
            ansiEmailBuffer.append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.FG_GREEN))
                      .append("Please enter your email address (leave blank to cancel):")
                      .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.OFF));
            
            String email = prompter.prompt(ansiEmailBuffer.toString());
            
            if(StringUtils.isNotBlank(email) && EmailValidator.getInstance().isValid(email))
            {
                doSubscribe(email);
            }
        }
    }

    private void doSubscribe(String email) throws IOException, PrompterException
    {
        String list = "1243499";
                
        String subscribeUrl = EMAIL_SUBSCRIBE_ROOT + email + "/subscribe?mailingListId=" + list;

        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(subscribeUrl);
            
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            //it's either pass or fail.
            //right now, we're just going to ignore it
            int responseCode = conn.getResponseCode();
            if(200 != responseCode)
            {
                getLogger().error("There was an error subscribing to the email list. Perhaps you're already on it?");
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Your subscription request has been sent.\n")
                        .append("Check your email for a confirmation (takes about 5 minutes) and click the opt-in link to complete the subscription process.\n")
                        .append("Press ENTER to continue");
                
                prompter.prompt(sb.toString());
            }
        
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private boolean promptForBoolean(String message, String defaultValue) throws PrompterException
    {
        String answer;
        boolean bool;
        if (StringUtils.isBlank(defaultValue))
        {
            answer = prompter.prompt(message, YN_ANSWERS);
        } else
        {
            answer = prompter.prompt(message, YN_ANSWERS, defaultValue);
        }

        if ("y".equals(answer.toLowerCase()))
        {
            bool = true;
        } else
        {
            bool = false;
        }

        return bool;
    }

    
}
