package ${package};

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import ${package}.resources.HealthCheck;

public class Main
{
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int EXIT_CODE_TIMEOUT = 3;
    private static final int EXIT_CODE_EXCEPTION_IN_HANDLER = 1;
    private static final int EXIT_CODE_STARTUP_FAILED = 2;

    public static void main(final String[] args)
    {
        final CountDownLatch exitLatch = new CountDownLatch(1);
        final AtomicBoolean isCleanExit = new AtomicBoolean(true);

        try
        {
            final Server server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(8080);
            server.addConnector(connector);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.addEventListener(new ContextLoaderListener());
            context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
            context.setInitParameter("contextConfigLocation", Main.class.getPackage().getName());

            ServletHolder holder = new ServletHolder(new ServletContainer());
            holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, HealthCheck.class.getPackage().getName());
            holder.setInitOrder(1);
            context.addServlet(holder, "/*");
            server.setHandler(context);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try
                {
                    server.stop();
                }
                catch (Exception e)
                {
                    log.error("Exception while stopping server", e);
                    isCleanExit.set(false);
                }
                finally
                {
                    exitLatch.countDown();
                }
            }
            ));
            server.start();
            server.join();
        }
        catch (Exception e)
        {
            log.error("Server startup failed", e);
            System.exit(EXIT_CODE_STARTUP_FAILED);
        }

        try
        {
            if (!exitLatch.await(1, TimeUnit.MINUTES))
            {
                log.error("Timeout waiting for shutdown");
                System.exit(EXIT_CODE_TIMEOUT);
            }
            if (!isCleanExit.get())
            {
                log.error("An exception occurred in the shutdown handler...");
                System.exit(EXIT_CODE_EXCEPTION_IN_HANDLER);
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }


}

