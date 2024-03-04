/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.client;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerListener;
import javax.speech.synthesis.SynthesizerProperties;

import static java.lang.System.getLogger;


/**
 * An RPC compliant {@link javax.speech.synthesis.Synthesizer}.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/09/13 umjammer initial version <br>
 */
public final class RpcSynthesizer implements Synthesizer {

    /** Logger for this class. */
    private static final Logger logger = getLogger(RpcSynthesizer.class.getName());

    private RpcSynthesizerMode mode;

    /** */
    private final RpcClient rpcClient;

    /**
     * Constructs a new synthesizer object.
     *
     * @param mode the synthesizer mode
     */
    RpcSynthesizer(RpcSynthesizerMode mode) {
        this.mode = mode;
        this.rpcClient = mode.getRpcClient();
    }

    private final List<SpeakableListener> speakableListeners = new ArrayList<>();

    private final List<SynthesizerListener> synthesizerListeners = new ArrayList<>();

    @Override
    public void addSpeakableListener(SpeakableListener listener) {
        speakableListeners.add(listener);
    }

    @Override
    public void removeSpeakableListener(SpeakableListener listener) {
        speakableListeners.remove(listener);
    }

    @Override
    public void addSynthesizerListener(SynthesizerListener listener) {
        synthesizerListeners.add(listener);
    }

    @Override
    public void removeSynthesizerListener(SynthesizerListener listener) {
        synthesizerListeners.remove(listener);
    }

    @Override
    public boolean cancel() throws EngineStateException {
        return rpcClient.cancel(-1); // TODO
    }

    @Override
    public boolean cancel(int id) throws EngineStateException {
        return rpcClient.cancel(id);
    }

    @Override
    public boolean cancelAll() throws EngineStateException {
        return rpcClient.cancelAll();
    }

    @Override
    public String getPhonemes(String text) throws EngineStateException {
        return rpcClient.getPhonemes(text);
    }

    @Override
    public SynthesizerProperties getSynthesizerProperties() {
        return rpcClient.getSynthesizerProperties();
    }

    @Override
    public void allocate() throws AudioException, EngineException, EngineStateException, SecurityException {
        rpcClient.allocate(-1); // TODO
    }

    @Override
    public void allocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException, SecurityException {
        rpcClient.allocate(mode);
    }

    @Override
    public void deallocate() throws AudioException, EngineException, EngineStateException {
        rpcClient.deallocate();
    }

    @Override
    public void deallocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException {
        rpcClient.deallocate(mode);
    }

    @Override
    public void pause() throws EngineStateException {
        rpcClient.pause();
    }

    @Override
    public boolean resume() throws EngineStateException {
        return rpcClient.resume();
    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        return rpcClient.testEngineState(state);
    }

    @Override
    public long waitEngineState(long state) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return rpcClient.waitEngineState(state, -1); // TODO
    }

    @Override
    public long waitEngineState(long state, long timeout) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return rpcClient.waitEngineState(state, timeout);
    }

    @Override
    public AudioManager getAudioManager() {
        return rpcClient.getAudioManager();
    }

    @Override
    public EngineMode getEngineMode() {
        return mode;
    }

    @Override
    public long getEngineState() {
        return rpcClient.getEngineState();
    }

    @Override
    public VocabularyManager getVocabularyManager() {
        return rpcClient.getVocabularyManager();
    }

    @Override
    public void setEngineMask(int mask) {
        rpcClient.setEngineMask(mask);
    }

    @Override
    public int getEngineMask() {
        return rpcClient.getEngineMask();
    }

    @Override
    public SpeechEventExecutor getSpeechEventExecutor() {
        return rpcClient.getSpeechEventExecutor();
    }

    @Override
    public void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor) {
        rpcClient.setSpeechEventExecutor(speechEventExecutor);
    }

    @Override
    public void setSpeakableMask(int mask) {
        rpcClient.setSpeakableMask(mask);
    }

    @Override
    public int getSpeakableMask() {
        return rpcClient.getSpeakableMask();
    }

    @Override
    public int speak(AudioSegment audio, SpeakableListener listener) throws SpeakableException, EngineStateException, IllegalArgumentException {
        return rpcClient.speak(audio);
    }

    @Override
    public int speak(Speakable speakable, SpeakableListener listener) throws SpeakableException, EngineStateException {
        return rpcClient.speak(speakable);
    }

    @Override
    public int speak(String text, SpeakableListener listener) throws EngineStateException {
        return rpcClient.speak(text);
    }

    @Override
    public int speakMarkup(String synthesisMarkup, SpeakableListener listener) throws SpeakableException, EngineStateException {
        return rpcClient.speak(synthesisMarkup);
    }
}
