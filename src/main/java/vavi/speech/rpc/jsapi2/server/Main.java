/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.server;


import java.net.URI;

import javax.speech.synthesis.Synthesizer;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import vavi.util.Debug;


/**
 * Main.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-02-02 nsano initial version <br>
 */
public class Main {

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

            server.start();
            server.join();
        } catch (Exception e) {
            Debug.printStackTrace(e);
            throw e;
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }
}
