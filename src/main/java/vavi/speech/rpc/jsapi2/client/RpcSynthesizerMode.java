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
     * @param modeName used as remote voice engine list factory class name
     */
    public RpcSynthesizerMode(String modeName) {
        super("RPC", modeName,
                null, null, null, null);
    }

    /**
     * Constructs a new object.
     *
     * @param modeName used as remote voice engine list factory class name
     * @param locale the locale associated with this mode
     */
    public RpcSynthesizerMode(String modeName, SpeechLocale locale) {
        super("RPC", modeName, null, null, null,
                new Voice[] {new Voice(locale, null, Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE)});
    }

    /**
     * Constructs a new object.
     *
     * RPC synthesizer does not support ssml
     *
     * @param engineName the name of the engine
     * @param modeName used as remote voice engine list factory class name
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
