/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.server;


import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import javax.speech.synthesis.Synthesizer;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static java.lang.System.getLogger;


/**
 * Main.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-02-02 nsano initial version <br>
 */
public class Main {

    private static final Logger logger = getLogger(Main.class.getName());

    /**
     * @param args none
     */
    public static void main(String[] args) throws Exception {
        Server server = null;
        try {
            ResourceConfig config = new ResourceConfig()
                    .register(Jsapi2Service.class)
                    .register(new AbstractBinder() {
                        @Override protected void configure() {
                            bindFactory(Jsapi2Service::getSynthesizer).to(Synthesizer.class);
                        }
                    });
            server = JettyHttpContainerFactory.createServer(
                    URI.create("http://localhost:60090/"), config); // TODO ssl
logger.log(Level.DEBUG, "server created");

            server.start();
logger.log(Level.DEBUG, "server started");
            server.join();
logger.log(Level.DEBUG, "server joined");
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
            throw e;
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
