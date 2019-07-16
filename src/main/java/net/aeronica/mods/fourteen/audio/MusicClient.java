package net.aeronica.mods.fourteen.audio;

import net.minecraft.util.SoundCategory;

public class MusicClient extends MxSound
{

    MusicClient(AudioData audioData)
    {
        super(audioData, SoundCategory.MUSIC);
        this.attenuationType = AttenuationType.NONE;
    }

    @Override
    public boolean isGlobal()
    {
        return true;
    }

    @Override
    protected void onUpdate()
    {
        // NOP
    }
}
