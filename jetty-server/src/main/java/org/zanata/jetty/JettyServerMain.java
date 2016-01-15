package org.zanata.jetty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServerMain {
    private String warLocation;

    public JettyServerMain(String warLocation) {
        this.warLocation = warLocation;
    }

    enum OperationalMode {
        DEV,
        PROD,
        UNKNOWN
    }

    private Path basePath;

    public static void main(String[] args) {
        try {
            String warPath = args.length > 0 ? args[0] : null;
            new JettyServerMain(warPath).run();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void run() throws Throwable {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199","false");

        Server server = new Server(8081);

        enableAnnotationScanning(server);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");

        // equivalent to jetty-web.xml (only works in jetty 8 not 9)
        context.prependServerClass("-org.eclipse.jetty.servlet.");
        context.prependServerClass("-org.eclipse.jetty.server.handler.ContextHandler");

        // equivalent to jetty-env.xml
        org.eclipse.jetty.plus.jndi.Resource mydatasource = new org.eclipse.jetty.plus.jndi.Resource(context, "BeanManager",
                new javax.naming.Reference("javax.enterprise.inject.spi.BeanManager", "org.jboss.weld.resources.ManagerObjectFactory", null));

        switch (getOperationalMode()) {
            case PROD:
                // Configure as WAR
                System.out.println("==== > staring WAR deployment");
                context.setWar(basePath.toString());
                break;
            case DEV:
                // TODO pahuang this is not maven so may not work
                // Configuring from Development Base
                System.out.println("==== > staring DEV base deployment");
                context.setBaseResource(new PathResource(basePath.resolve("src/main/webapp")));
                // Add webapp compiled classes & resources (copied into place from src/main/resources)
                Path classesPath = basePath.resolve("build/classes/main/classes");
                context.setExtraClasspath(classesPath.toAbsolutePath().toString());
                break;
            default:
                throw new FileNotFoundException("Unable to configure WebAppContext base resource undefined");
        }

        server.setHandler(context);

        server.start();
//        server.dumpStdErr();
        server.join();
    }

    private OperationalMode getOperationalMode() throws IOException {
        if (warLocation != null) {
            Path warPath = new File(warLocation).toPath().toRealPath();
            if (Files.exists(warPath) && Files.isRegularFile(warPath)) {
                this.basePath = warPath;
                return OperationalMode.PROD;
            }
        }

        Path devPath = new File("../livewar").toPath().toRealPath();
        if (Files.exists(devPath) && Files.isDirectory(devPath)) {
            this.basePath = devPath;
            return OperationalMode.DEV;
        }

        return OperationalMode.UNKNOWN;
    }

    private void enableAnnotationScanning(Server server) {
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");
    }
}
