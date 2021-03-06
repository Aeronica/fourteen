package net.aeronica.libs.mml.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;

@SuppressWarnings("unused")
public enum MMLUtil
{
    ;
    public static final Logger MML_LOGGER = LogManager.getLogger("MML Core");
    public static final int MAX_TRACKS = 10;

    /* int[0] is not used. Used to reorder ASCII letters to musical notes taking into account accidentals. That is ABCDEFG to CDEFGAB */
    private static final int[] DOE_RE_MI = {50, 9, 11, 0, 2, 4, 5, 7};

    public static int getMIDINote(int rawNote, int mmlOctave)
    {
        /* Convert ASCII UPPER CASE Note value to integer: A=1, G=7 */
        int doreNote = rawNote - 64;
        /* Get start of the MML Octave */
        int octave = (mmlOctave * 12) + 12;
        /* combine the octave and reordered note to get the MIDI note value */
        return octave + DOE_RE_MI[doreNote];
    }

    public static int getMIDINote(int rawNote, int mmlOctave, boolean rest)
    {
        int midiNote = getMIDINote(rawNote, mmlOctave);
        return midiNote + (rest ? 128 : 0);
    }
    
    public static int smartClampMIDI(int midiNoteIn)
    {
        int midiNoteClamped = midiNoteIn;
        while (midiNoteClamped < 0 || midiNoteClamped > 127)
        {
            if (midiNoteClamped < 0) midiNoteClamped += 12;
            if (midiNoteClamped > 127) midiNoteClamped -= 12;
        }
        return midiNoteClamped;
    }
    
    /**
     * Unpacks the packedPreset and returns the {@link Patch}<br/>
     * Note that his is not always going to return what you might expect. Tread carefully!
     * @param packedPatchIn the packed bank and program represented as a single integer
     * @return javax.sound.midi.Patch
     */
    public static Patch packedPreset2Patch(int packedPatchIn)
    {
        int program = packedPatchIn & 0x7F;
        int bank = 0;
        if (packedPatchIn > 0x7F)
        {
            bank = (packedPatchIn & 0x1FFF80) >>> 7;
        }
        return new Patch(bank, program);
    }

    /**
     * A convenience method for building a packed integer representation of
     * the bank and program of a soundfont preset
     * @param bankIn 0-128
     * @param programIn 0-127
     * @return the packed integer soundfont Preset
     */
    public static int preset2PackedPreset(int bankIn, int programIn)
    {
        int bank = bankIn < 128 ? bankIn : 128;
        int patch = programIn & 0x7F;
        return (bank << 7) + patch;
    }

    /**
     * Returns a packed integer representation of a ({@link Patch})<br/>
     * Note that his is not always going to return what you might expect. Tread carefully!<br/>
     * e.g. DO NOT USE with {@link Patch javax.sound.midi.Instrument.getPatch()}
     * @param patchIn the Patch
     * @return the packed integer representation of a Patch
     */
    public static int patch2PackedPreset(Patch patchIn)
    {
        return preset2PackedPreset(patchIn.getBank(), patchIn.getProgram());
    }

    public static int instrument2PackedPreset(@Nonnull Instrument instrument)
    {
        /* Table Flip! */
        boolean isPercussionSet = instrument.toString().contains("Drumkit:");
        /* A SoundFont 2.04 preset allows 128 banks 0-127) plus the percussion
         * set for 129 sets! OwO However when you get a patch from an
         * Instrument from a loaded soundfont you will find the bank value
         * for the preset is left shifted 7 bits. However what's worse is that
         * for preset bank:128 the value returned by getBank() is ZERO!
         * So as a workaround check the name of instrument to see if it's a percussion set.
         */
        int bank = instrument.getPatch().getBank() >>> 7;
        int program = instrument.getPatch().getProgram();
        return isPercussionSet ? MMLUtil.preset2PackedPreset(128, program) : MMLUtil.preset2PackedPreset(bank, program);
    }
}
