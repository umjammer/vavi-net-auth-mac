/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.speech.rpc.jsapi2.client.RpcClient;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-02 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
@EnabledIf("localPropertiesExists")
class TestCase {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "text")
    String text = "src/test/resources/test.txt";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    void test01() throws Exception {
        RpcClient c = new RpcClient();
Debug.println("to getVoices");
        Voice[] voices = c.getVoices();
Arrays.stream(voices).forEach(System.err::println);
        c.close();
    }

    @Test
    void test02() throws Exception {
        EngineManager.registerEngineListFactory(vavi.speech.aquestalk10.jsapi2.AquesTalk10EngineListFactory.class.getName());
        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
        Voice[] voices = ((SynthesizerMode) synthesizer.getEngineMode()).getVoices();
Arrays.stream(voices).forEach(System.err::println);
    }

    @Test
    void test03() throws Exception {
        speak("ゆっくりしていってね。");
    }

    @Test
    void test04() throws Exception {
        RpcClient c = new RpcClient();
        SynthesizerProperties sp = c.getSynthesizerProperties();
Debug.println(sp);
        c.close();
    }

    @Test
    void test1() throws Exception {
        Path path = Paths.get(text);
        String text = String.join("\n", Files.readAllLines(path));
        speak(text);
    }

    /** */
    void speak(String text) throws Exception {
        EngineManager.registerEngineListFactory(vavi.speech.rpc.jsapi2.client.RpcEngineListFactory.class.getName());

        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
        synthesizer.addSynthesizerListener(System.err::println);
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        synthesizer.resume();
        synthesizer.waitEngineState(Synthesizer.RESUMED);

        String voiceName = "F1";
        Voice voice = Arrays.stream(((SynthesizerMode) synthesizer.getEngineMode()).getVoices()).filter(v -> v.getName().equals(voiceName)).findFirst().get();
Debug.println(voice);
        synthesizer.getSynthesizerProperties().setVoice(voice);
        synthesizer.getSynthesizerProperties().setVolume(2);

        for (String line : text.split("[。\n]")) {
            System.out.println(line);
            synthesizer.speak(line + "。", System.err::println);
        }

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }
}
