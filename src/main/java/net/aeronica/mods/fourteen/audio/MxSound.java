package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

import net.minecraft.client.audio.ISound.AttenuationType;

public abstract class MxSound extends TickableSound
{
    protected int playID;
    protected AudioData audioData;
    private SoundEventAccessor soundEventAccessor;

    public MxSound(AudioData audioData, SoundCategory categoryIn)
    {
        super(ModSoundEvents.PCM_PROXY, categoryIn);
        this.audioData = audioData;
        this.playID = audioData.getPlayId();
        this.audioData.setISound(this);
        this.sound = new PCMSound();
        this.volume = 1F;
        this.pitch = 1F;
        this.looping = false;
        this.delay = 0;
        this.x = 0F;
        this.y = 0F;
        this.z = 0F;
        this.attenuation = AttenuationType.LINEAR;
        this.soundEventAccessor = new SoundEventAccessor(this.sound.getLocation(), "subtitle.fourteen.pcm-proxy");
    }

    @Override
    public SoundEventAccessor resolve(SoundHandler handler)
    {
        return this.soundEventAccessor;
    }

    @Override
    public void tick()
    {
        onUpdate();
    }

    protected void onUpdate() { /* NOP */ }

    protected void setDonePlaying()
    {
        this.stop();
    }
}
