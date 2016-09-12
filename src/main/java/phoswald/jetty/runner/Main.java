package phoswald.jetty.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import phoswald.daemon.utils.Daemon;

public class Main {

    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.out.println("Usage: java -jar jetty-runner.jar <webapp.war>");
            return;
        }
        String webappFile = args[0];
        int port = Integer.parseInt(System.getProperty("runner.port", "8080"));
        String context = System.getProperty("runner.context", "");
        System.out.println("Using Webapp: " + webappFile);
        System.out.println("Serving:      http://localhost:" + port + "/" + context);
        
        WebAppContext handler = new WebAppContext();
        handler.setContextPath("/" + context);
        handler.setWar(webappFile);
        
        Server server = new Server(port);
        server.setHandler(handler);
        server.start();

		System.out.println("[Press Ctrl+C or send SIGTERM to stop]");
    	Daemon.main(server::join, server::stop, () -> System.out.println("[Stopped]"));
    }
}
