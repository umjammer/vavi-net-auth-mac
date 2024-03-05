/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.client;


import java.io.Closeable;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Arrays;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EnginePropertyListener;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.SpeechLocale;
import javax.speech.VocabularyManager;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

import static java.lang.System.getLogger;


/**
 * RpcClient.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-09-13 nsano initial version <br>
 */
public class RpcClient implements Closeable {

    private static final Logger logger = getLogger(RpcClient.class.getName());

    /** RPC application web api */
    private static String url = "http://localhost:60090/";

    /** */
    private static Gson gson = new GsonBuilder().create();

    /* */
    static {
        String url = System.getProperty("vavi.speech.rpc.url");
        if (url != null) {
            RpcClient.url = url;
        }
    }

    /** rest */
    private final Client client;

    /** rest address */
    private final WebTarget target;

    /** */
    public RpcClient() {
        try {
            client = ClientBuilder.newClient(); // DON'T CLOSE
            target = client.target(url);
        } catch (Exception e) {
            throw new IllegalStateException("RpcClient is not available at " + url, e);
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    /** DTO for {@link Voice} */
    public static class VoiceDTO {
        /** DTO for {@link SpeechLocale} */
        public static class SpeechLocaleDTO {
            public SpeechLocaleDTO() {}
            public SpeechLocaleDTO(SpeechLocale speechLocale) {
                language = speechLocale.getLanguage();
                country = speechLocale.getCountry();
                variant = speechLocale.getVariant();
            }
            public String language;
            public String country;
            public String variant;
            SpeechLocale toSpeechLocale() {
                return new SpeechLocale(language, country, variant);
            }
        }
        public VoiceDTO() {}
        public VoiceDTO(Voice voice) {
            locale = new SpeechLocaleDTO(voice.getSpeechLocale());
            name = voice.getName();
            gender = voice.getGender();
            age = voice.getAge();
            variant = voice.getVariant();
        }
        public SpeechLocaleDTO locale;
        public String name;
        public int gender;
        public int age;
        public int variant;
        public Voice toVoice() {
            return new Voice(locale != null ? locale.toSpeechLocale() : null, name, gender, age, variant);
        }
    }

    /** */
    public Voice[] getVoices() {
        String json = target.path("/jsapi2/getVoices")
                .request()
                .get(String.class);
        VoiceDTO[] voices = gson.fromJson(json, VoiceDTO[].class);
        return Arrays.stream(voices).map(VoiceDTO::toVoice).toArray(Voice[]::new);
    }

//    public boolean cancel() throws EngineStateException {
//        target.path("/jsapi2/cancel")
//                .request()
//                .get();
//        return false;
//    }

    public boolean cancel(int id) throws EngineStateException {
        return target.path("/jsapi2/cancel")
                .queryParam("id", id)
                .request()
                .get(Boolean.class);
    }

    public boolean cancelAll() throws EngineStateException {
        return target.path("/jsapi2/cancelAll")
                .request()
                .get(Boolean.class);
    }

    public String getPhonemes(String text) throws EngineStateException {
        return target.path("/jsapi2/getPhonemes")
                .queryParam("text", text)
                .request()
                .get(String.class);
    }

    /** DTO for {@link SynthesizerProperties} */
    public static class SynthesizerPropertiesDTO {
        SynthesizerPropertiesDTO() {}
        public SynthesizerPropertiesDTO(SynthesizerProperties sp) {
            voice = new VoiceDTO(sp.getVoice());
            volume = sp.getVolume();
        }
        public VoiceDTO voice;
        public int volume;
    }

    /** setting property is reflected on server side, implementations are pertly */
    class RcpSynthesizerProperties implements SynthesizerProperties {
        RcpSynthesizerProperties(SynthesizerPropertiesDTO sp) {
            this.voice = sp.voice.toVoice();
            this.volume = sp.volume;
        }

        Voice voice;
        int volume;

        @Override public void setInterruptibility(int level) throws IllegalArgumentException {

        }

        @Override public int getInterruptibility() {
            return 0;
        }

        @Override public void setPitch(int hertz) throws IllegalArgumentException {

        }

        @Override public int getPitch() {
            return 0;
        }

        @Override public void setPitchRange(int hertz) throws IllegalArgumentException {

        }

        @Override public int getPitchRange() {
            return 0;
        }

        @Override public void setSpeakingRate(int wpm) throws IllegalArgumentException {

        }

        @Override public int getSpeakingRate() {
            return 0;
        }

        @Override public void setVoice(Voice voice) throws IllegalArgumentException {
            MultivaluedHashMap<String, String> formParams = new MultivaluedHashMap<>();
            formParams.putSingle("voice", gson.toJson(new VoiceDTO(voice)));
            target.path("/jsapi2/synthesizerProperties/voice")
                    .request()
                    .post(Entity.entity(formParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        }

        @Override public Voice getVoice() {
            return voice;
        }

        @Override public void setVolume(int volume) throws IllegalArgumentException {
            target.path("/jsapi2/synthesizerProperties/volume")
                    .queryParam("volume", volume)
                    .request()
                    .get();
        }

        @Override public int getVolume() {
            return volume;
        }

        @Override public void addEnginePropertyListener(EnginePropertyListener listener) {}

        @Override public void removeEnginePropertyListener(EnginePropertyListener listener) {}

        @Override public int getPriority() {
            return 0;
        }

        @Override public void setPriority(int priority) throws IllegalArgumentException {

        }

        @Override public void reset() {

        }

        @Override public void setBase(String uri) throws IllegalArgumentException {

        }

        @Override public String getBase() {
            return null;
        }
    }

    public SynthesizerProperties getSynthesizerProperties() {
        String json = target.path("/jsapi2/getSynthesizerProperties")
                .request()
                .get(String.class);
        SynthesizerPropertiesDTO dto = gson.fromJson(json, SynthesizerPropertiesDTO.class);
        return new RcpSynthesizerProperties(dto);
    }

//    public void allocate() throws AudioException, EngineException, EngineStateException, SecurityException {
//        target.path("/jsapi2/allocate")
//                .request()
//                .get();
//    }

    public void allocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException, SecurityException {
        target.path("/jsapi2/allocate")
                .queryParam("mode", mode)
                .request()
                .get();
    }

    public void deallocate() throws AudioException, EngineException, EngineStateException {
        client.close();
logger.log(Level.INFO, "ignore: deallocate()V");
//        target.path("/jsapi2/deallocate")
//                .request()
//                .get();
    }

    public void deallocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException {
        client.close();
logger.log(Level.INFO, "ignore: deallocate(I)V");
//        target.path("/jsapi2/deallocate")
//                .queryParam("mode", mode)
//                .request()
//                .get();
    }

    public void pause() throws EngineStateException {
        target.path("/jsapi2/pause")
                .request()
                .get();
    }

    public boolean resume() throws EngineStateException {
        return target.path("/jsapi2/resume")
                .request()
                .get(Boolean.class);
    }

    public boolean testEngineState(long state) throws IllegalArgumentException {
        return target.path("/jsapi2/testEngineState")
                .queryParam("state", state)
                .request()
                .get(Boolean.class);
    }

//    public long waitEngineState(long state) throws InterruptedException, IllegalArgumentException, IllegalStateException {
//        target.path("/jsapi2/waitEngineState")
//                .queryParam("state", state)
//                .request()
//                .get();
//        return 0;
//    }

    public long waitEngineState(long state, long timeout) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return target.path("/jsapi2/waitEngineState")
                .queryParam("state", state)
                .queryParam("timeout", timeout)
                .request()
                .get(Long.class);
    }

    public AudioManager getAudioManager() {
        String json = target.path("/jsapi2/getAudioManager")
                .request()
                .get(String.class);
        return gson.fromJson(json, AudioManager.class);
    }

//    public EngineMode getEngineMode() {
//        String json = target.path("/jsapi2/getEngineMode")
//                .request()
//                .get(String.class);
//        return gson.fromJson(json, EngineMode.class);
//    }

    public long getEngineState() {
        return target.path("/jsapi2/getEngineState")
                .request()
                .get(Long.class);
    }

    public VocabularyManager getVocabularyManager() {
        String json = target.path("/jsapi2/getVocabularyManager")
                .request()
                .get(String.class);
        return gson.fromJson(json, VocabularyManager.class);
    }

    public void setEngineMask(int mask) {
        target.path("/jsapi2/setEngineMask")
                .queryParam("mask", mask)
                .request()
                .get();
    }

    public int getEngineMask() {
        return target.path("/jsapi2/getEngineMask")
                .request()
                .get(Integer.class);
    }

    public SpeechEventExecutor getSpeechEventExecutor() {
        String json = target.path("/jsapi2/getSpeechEventExecutor")
                .request()
                .get(String.class);
        return gson.fromJson(json, SpeechEventExecutor.class);
    }

    public void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor) {
        target.path("/jsapi2/setSpeechEventExecutor")
                .request()
                .get();
    }

    public void setSpeakableMask(int mask) {
        target.path("/jsapi2/setSpeakableMask")
                .queryParam("mask", mask)
                .request()
                .get();
    }

    public int getSpeakableMask() {
        return target.path("/jsapi2/getSpeakableMask")
                .request()
                .get(Integer.class);
    }

    public int speak(AudioSegment audio) throws SpeakableException, EngineStateException, IllegalArgumentException {
        return target.path("/jsapi2/speak")
                .queryParam("audio", audio)
                .request()
                .get(Integer.class);
    }

    public int speak(Speakable speakable) throws SpeakableException, EngineStateException {
        return target.path("/jsapi2/speak")
                .queryParam("speakable", speakable)
                .request()
                .get(Integer.class);
    }

    public int speak(String text) throws EngineStateException {
        return target.path("/jsapi2/speak")
                .queryParam("text", text)
                .request()
                .get(Integer.class);
    }

    public int speakMarkup(String synthesisMarkup) throws SpeakableException, EngineStateException {
        return target.path("/jsapi2/speak")
                .queryParam("synthesisMarkup", synthesisMarkup)
                .request()
                .get(Integer.class);
    }
}
