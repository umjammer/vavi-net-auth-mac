/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.server;

import java.io.Closeable;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineManager;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import vavi.speech.rpc.jsapi2.client.RpcClient.SynthesizerPropertiesDTO;
import vavi.speech.rpc.jsapi2.client.RpcClient.VoiceDTO;

import static java.lang.System.getLogger;


/**
 * Jsapi2Service.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-02-03 nsano initial version <br>
 */
@Path("jsapi2")
public class Jsapi2Service implements Closeable {

    private static final Logger logger = getLogger(Jsapi2Service.class.getName());

    /** */
    private static final Gson gson = new GsonBuilder().create();

    @Inject
    private Synthesizer synthesizer; // this class is stateless, so it's needed to set synthesizer every access

    // TODO should not be static but di container

    /** for DI */
    private static Synthesizer _synthesizer;

    /** for DI */
    public static Synthesizer getSynthesizer() {
        return _synthesizer;
    }

    /** for DI */
    public static void createSynthesizer(String modeName) {
logger.log(Level.DEBUG, "modeName: " + modeName);
        try {
            @SuppressWarnings("unchecked")
            Class<SynthesizerMode> clazz = (Class<SynthesizerMode>) Class.forName(modeName);
logger.log(Level.DEBUG, "clazz: " + clazz.getName());
            _synthesizer = (Synthesizer) EngineManager.createEngine(clazz.getDeclaredConstructor().newInstance());
logger.log(Level.DEBUG, "synthesizer: " + _synthesizer.getClass().getName());
            _synthesizer.addSynthesizerListener(System.err::println);
            _synthesizer.allocate();
            _synthesizer.waitEngineState(Engine.ALLOCATED);
            _synthesizer.resume();
            _synthesizer.waitEngineState(Synthesizer.RESUMED);
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();
        } catch (AudioException | EngineException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getVoices")
    public String getVoices(@QueryParam("modeName") String modeName) {
logger.log(Level.DEBUG, "getVoices: " + modeName);
        createSynthesizer(modeName);
        Voice[] voices = ((SynthesizerMode) getSynthesizer().getEngineMode()).getVoices();
        VoiceDTO[] dtos = Arrays.stream(voices).map(VoiceDTO::new).toArray(VoiceDTO[]::new);
        return gson.toJson(dtos);
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("cancel")
//    public boolean cancel() {
//        return synthesizer.cancel();
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cancel")
    public boolean cancel(@QueryParam("id") int id) throws EngineStateException {
        if (id == -1) {
            return synthesizer.cancel();
        } else {
            return synthesizer.cancel(id);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cancelAll")
    public boolean cancelAll() throws EngineStateException {
        return synthesizer.cancelAll();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("getPhonemes")
    public String getPhonemes(@QueryParam("text") String text) throws EngineStateException {
        return synthesizer.getPhonemes(text);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getSynthesizerProperties")
    public String getSynthesizerProperties() {
        SynthesizerProperties sp = synthesizer.getSynthesizerProperties();
        return gson.toJson(new SynthesizerPropertiesDTO(sp));
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("synthesizerProperties/voice")
    public void setSynthesizerProperties_voice(@FormParam("voice") String voice) {
        VoiceDTO dto = gson.fromJson(voice, VoiceDTO.class);
logger.log(Level.DEBUG, "setVoice: " + dto.toVoice());
        synthesizer.getSynthesizerProperties().setVoice(dto.toVoice());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("synthesizerProperties/volume")
    public void setSynthesizerProperties_volume(@QueryParam("volume") int volume) {
logger.log(Level.DEBUG, "setVolume: " + volume);
        synthesizer.getSynthesizerProperties().setVolume(volume);
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("allocate")
//    public String allocate() throws AudioException, EngineException, EngineStateException, SecurityException {
//        synthesizer.allocate();
//        return "DONE";
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("allocate")
    public void allocate(@QueryParam("mode") int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException, SecurityException {
        if (mode == -1) {
            synthesizer.allocate();
        } else {
            synthesizer.allocate(mode);
        }
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("deallocate")
//    public void deallocate() throws AudioException, EngineException, EngineStateException {
//        synthesizer.deallocate();
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("deallocate")
    public void deallocate(@QueryParam("mode") int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException {
        if (mode == -1) {
            synthesizer.deallocate();
        } else {
            synthesizer.deallocate(mode);
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("pause")
    public void pause() throws EngineStateException {
        synthesizer.pause();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("resume")
    public boolean resume() throws EngineStateException {
        return synthesizer.resume();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("testEngineState")
    public boolean testEngineState(@QueryParam("state") long state) throws IllegalArgumentException {
        return synthesizer.testEngineState(state);
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("waitEngineState")
//    public long waitEngineState(@QueryParam("state") long state) throws InterruptedException, IllegalArgumentException, IllegalStateException {
//        return synthesizer.waitEngineState(state);
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("waitEngineState")
    public long waitEngineState(@QueryParam("state") long state, @QueryParam("timeout") long timeout) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        if (timeout == -1) {
            return synthesizer.waitEngineState(state);
        } else {
            return synthesizer.waitEngineState(state, timeout);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getAudioManager")
    public String getAudioManager() {
        AudioManager am = synthesizer.getAudioManager();
        return gson.toJson(am);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getEngineState")
    public long getEngineState() {
        return synthesizer.getEngineState();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getVocabularyManager")
    public String getVocabularyManager() {
        VocabularyManager vm = synthesizer.getVocabularyManager();
        return gson.toJson(vm);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("setEngineMask")
    public void setEngineMask(@QueryParam("mask") int mask) {
        synthesizer.setEngineMask(mask);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getEngineMask")
    public int getEngineMask() {
        return synthesizer.getEngineMask();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getSpeechEventExecutor")
    public SpeechEventExecutor getSpeechEventExecutor() {
        return synthesizer.getSpeechEventExecutor();
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("setSpeechEventExecutor")
//    public void setSpeechEventExecutor(@QueryParam("speechEventExecutor") SpeechEventExecutor speechEventExecutor) {
//        synthesizer.setSpeechEventExecutor(speechEventExecutor);
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("setSpeakableMask")
    public void setSpeakableMask(@QueryParam("mask") int mask) {
        synthesizer.setSpeakableMask(mask);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getSpeakableMask")
    public int getSpeakableMask() {
        return synthesizer.getSpeakableMask();
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("speak")
//    public int speak(@QueryParam("audio") AudioSegment audio)
//            throws SpeakableException, EngineStateException, IllegalArgumentException {
//        return synthesizer.speak(audio, e -> logger.log(Level.TRACE, e));
//    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("speak")
//    public int speak(@QueryParam("speakable") Speakable speakable)
//            throws SpeakableException, EngineStateException {
//        return synthesizer.speak(speakable, e -> logger.log(Level.TRACE, e));
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("speak")
    public int speak(@QueryParam("text") String text)
            throws EngineStateException {
        return synthesizer.speak(text, e -> logger.log(Level.TRACE, e));
    }

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    @Path("speak")
//    public int speakMarkup(@QueryParam("synthesisMarkup") String synthesisMarkup)
//            throws SpeakableException, EngineStateException {
//        return synthesizer.speakMarkup(synthesisMarkup, e -> logger.log(Level.TRACE, e));
//    }
}