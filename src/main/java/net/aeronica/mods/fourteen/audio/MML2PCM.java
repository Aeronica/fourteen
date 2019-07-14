/*
 * Aeronica's mxTune MOD
 * Copyright 2018, Paul Boese a.k.a. Aeronica
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package net.aeronica.mods.fourteen.audio;

import net.aeronica.libs.mml.core.MMLParser;
import net.aeronica.libs.mml.core.MMLParserFactory;
import net.aeronica.libs.mml.core.MMLToMIDI;
import net.aeronica.libs.mml.core.MMLUtil;
import net.aeronica.mods.fourteen.util.MIDISystemUtil;
import net.minecraft.client.resources.I18n;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

import static net.aeronica.mods.fourteen.audio.ClientAudio.Status.ERROR;
import static net.aeronica.mods.fourteen.audio.ClientAudio.Status.READY;


public class MML2PCM
{
    private static final Logger LOGGER = LogManager.getLogger();
    private AudioData audioData;
    private String mmlText;

    MML2PCM(AudioData audioData, String mmlText)
    {
        this.audioData = audioData;
        this.mmlText = mmlText;
    }

    public boolean process()
    {
        MMLParser mmlParser;
        try
        {
            mmlParser = MMLParserFactory.getMMLParser(mmlText);
        }
        catch (IOException e)
        {
            LOGGER.debug("MMLParserFactory.getMMLParser() IOException in {}, Error: {}", MML2PCM.class.getSimpleName(), e);
            audioData.setStatus(ERROR);
            return false;
        }
        mmlParser.setBuildParseTree(true);
        ParseTree tree = mmlParser.band();

        ParseTreeWalker walker = new ParseTreeWalker();
        MMLToMIDI mmlToMIDI = new MMLToMIDI();
        walker.walk(mmlToMIDI, tree);
        // ANTLR4 MML Parser END

        // Log bank and program per instrument
        for (int packedPreset: mmlToMIDI.getPackedPresets())
        {
            Patch patchPreset = MMLUtil.packedPreset2Patch(packedPreset);
            String name = I18n.format(MIDISystemUtil.getPatchNameKey(patchPreset));
            LOGGER.debug("MML2PCM preset: bank: {}, program {}, name {}", patchPreset.getBank(),
                           patchPreset.getProgram(), name);
        }

        // Generate PCM Audio Stream from MIDI
        try
        {
            Midi2WavRenderer mw = new Midi2WavRenderer();
            AudioInputStream pcmStream = mw.createPCMStream(mmlToMIDI.getSequence(), audioData.getAudioFormat());
            audioData.setAudioStream(pcmStream);
        } catch (ModMidiException | MidiUnavailableException e)
        {
            audioData.setStatus(ERROR);
            LOGGER.error("MIDI to PCM process: ", e);
            return false;
        }
        audioData.setStatus(READY);
        return true;
    }
}
