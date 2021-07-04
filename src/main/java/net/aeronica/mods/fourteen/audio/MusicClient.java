package net.aeronica.mods.fourteen.audio;

import net.minecraft.util.SoundCategory;

import net.minecraft.client.audio.ISound.AttenuationType;

public class MusicClient extends MxSound
{

    MusicClient(AudioData audioData)
    {
        super(audioData, SoundCategory.MUSIC);
        this.attenuation = AttenuationType.NONE;
    }

    @Override
    public boolean isRelative()
    {
        return true;
    }

    @Override
    protected void onUpdate()
    {
        // NOP
    }
}
