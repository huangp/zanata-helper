//
//  ========================================================================
//  Copyright (c) 1995-2015 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.zanata.sync.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

public class JettyBootstrap {

    public static void main(String[] args) {
        try {
            URL warLocation =
                    JettyBootstrap.class.getProtectionDomain().getCodeSource()
                            .getLocation();
            if (warLocation == null) {
                throw new IOException("JettyBootstrap not discoverable");
            }

            LiveWarClassLoader clWar = new LiveWarClassLoader(warLocation);
            System.out.println("Using ClassLoader: " + clWar);
            Thread.currentThread().setContextClassLoader(clWar);

            File warFile = new File(warLocation.toURI());
            String warLocationStr = warFile
                    .toPath().toRealPath().toString();

            Class<?> mainClass =
                    Class.forName("org.zanata.jetty.JettyServerMain", true, clWar);

            Class.forName("org.eclipse.jetty.webapp.WebAppContext", false, clWar);
            Method mainMethod = mainClass.getMethod("main", args.getClass());
            // we put warLocation as first argument and pass on what's passed in here
            String [] extraArgs = new String[args.length + 1];
            extraArgs[0] = warLocationStr;
            System.arraycopy(args, 0, extraArgs, 1, args.length);

            mainMethod.invoke(mainClass, new Object[] { extraArgs });
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }
}
