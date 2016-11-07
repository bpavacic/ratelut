package com.ratelut.apiserver.app;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.ratelut.apiserver.listeners.BackgroundJobManager;
import com.ratelut.apiserver.storage.StorageModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Main application class.
 *
 * Initializes Guice and starts Jetty web server.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class Main {
    private static final int DEFAULT_PORT = 8080;
    @Inject private BackgroundJobManager backgroundJobManager;

    private static String getJarPath() {
        java.net.URL codeBase = Main.class.getProtectionDomain().getCodeSource().getLocation();
        if (codeBase.getPath().endsWith(".jar")) {
            return String.format("jar:file://%s", codeBase.getPath());
        } else {
            return null;
        }
    }

    public void startServer(int serverPort) throws Exception {
        System.out.println("Starting Jetty server");

        Server server = new Server(serverPort);

        ServletContextHandler root = new ServletContextHandler(server, "/");
        String jarPath = getJarPath();
        if (jarPath != null) {
            // Running from JAR.
            root.setResourceBase(jarPath + "!/html/");
        } else {
            // Running from IDE.
            root.setResourceBase("./src/main/resources/html/");
        }
        root.addFilter(GuiceFilter.class, "/api/*", null);
        root.addServlet(DefaultServlet.class, "/");
        root.addEventListener(backgroundJobManager);

        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(
                new StorageModule(),
                new JerseyModule()
        );

        Main main = injector.getInstance(Main.class);
        main.startServer(DEFAULT_PORT);
    }
}
