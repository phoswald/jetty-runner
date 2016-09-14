package phoswald.jetty.runner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.glassfish.jersey.servlet.ServletProperties;

import phoswald.daemon.utils.Daemon;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.out.println("Usage: java -jar jetty-runner.jar <webapp.war>");
            return;
        }
        Path webappFile = Paths.get(args[0]).toAbsolutePath().normalize();
        int port = Integer.parseInt(System.getProperty("runner.port", "8080"));
        String context = System.getProperty("runner.context", "");
        System.out.println("Using Webapp: " + webappFile);
        System.out.println("Serving:      http://localhost:" + port + "/" + context);
        
        // If webappFile is a .war file (not a directory):
        // - if the directory of the .war contains a sub-directory with the same name, 
        //   the webapp is run from that sub-directory (assuming the contents are the same).
        // - if no such sub-directory exists, the war is extracted into a 
        //   temporary directory (/tmp/...) and the webapp run from there.
        // If webappFile is a directory, the webapp is run from there.
        WebAppContext handler = new WebAppContext();
        handler.setContextPath("/" + context);
        handler.setWar(webappFile.toString());
        handler.setConfigurations(new Configuration[] {
    		new AnnotationConfiguration(),
    		new WebInfConfiguration(),
    		new WebXmlConfiguration()
    	});
        
        // handler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*"); // working, but static content is no longer served
        // handler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/rest*"); // working, but exact pattern is unknown 
        FilterHolder jersey = handler.addFilter(org.glassfish.jersey.servlet.ServletContainer.class, "/*", EnumSet.allOf(DispatcherType.class));
        jersey.setInitParameter(ServletProperties.FILTER_FORWARD_ON_404, "true");
        
        Server server = new Server(port);
        server.setHandler(handler);
        server.start();

		System.out.println("[Press Ctrl+C or send SIGTERM to stop]");
    	Daemon.main(server::join, server::stop, () -> System.out.println("[Stopped]"));
    }
}
