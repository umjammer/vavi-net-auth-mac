/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rpc.jsapi2.client;

import java.util.ArrayList;
import java.util.List;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;


/**
 * Factory for the RPC Speech engine.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/09/13 umjammer initial version <br>
 */
public class RpcEngineListFactory implements EngineListFactory {

    @Override
    public EngineList createEngineList(EngineMode require) {
        if (require instanceof SynthesizerMode synthesizerMode) {
            RpcClient rpcClient = new RpcClient();
            List<Voice> allVoices = List.of(rpcClient.getVoices());
            List<Voice> voices = new ArrayList<>();
            if (synthesizerMode.getVoices() == null) {
                voices.addAll(allVoices);
            } else {
                for (Voice availableVoice : allVoices) {
                    for (Voice requiredVoice : synthesizerMode.getVoices()) {
                        if (availableVoice.match(requiredVoice)) {
                            voices.add(availableVoice);
                        }
                    }
                }
            }
            SynthesizerMode[] features = new SynthesizerMode[] {
                new RpcSynthesizerMode(null,
                                       synthesizerMode.getEngineName(),
                                       synthesizerMode.getRunning(),
                                       synthesizerMode.getSupportsLetterToSound(),
                                       synthesizerMode.getMarkupSupport(),
                                       voices.toArray(Voice[]::new),
                                       rpcClient)
            };
            return new EngineList(features);
        }

        return null;
    }
}
