/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.client;

import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.SpeechLocale;
import javax.speech.spi.EngineFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;


/**
 * Synthesizer mode for RPC.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/09/13 umjammer initial version <br>
 */
public final class RpcSynthesizerMode extends SynthesizerMode implements EngineFactory {

    /** */
    private RpcClient rpcClient;

    /** */
    public RpcClient getRpcClient() {
        return rpcClient;
    }

    /**
     * Constructs a new object.
     */
    public RpcSynthesizerMode() {
        super();
    }

    /**
     * Constructs a new object.
     *
     * @param locale the locale associated with this mode
     */
    public RpcSynthesizerMode(SpeechLocale locale) {
        super(locale);
    }

    /**
     * Constructs a new object.
     *
     * RPC synthesizer does not support ssml
     *
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     */
    public RpcSynthesizerMode(String engineName,
                              String modeName,
                              Boolean running,
                              Boolean supportsLetterToSound,
                              Boolean supportsMarkup,
                              Voice[] voices,
                              RpcClient rpcClient) {
        super(engineName, modeName, running, supportsLetterToSound, supportsMarkup, voices);
        this.rpcClient = rpcClient;
    }

    @Override
    public Engine createEngine() throws IllegalArgumentException, EngineException {
        return new RpcSynthesizer(this);
    }
}
