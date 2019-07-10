package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

public class MxSound extends TickableSound
{
    protected int playID;
    private SoundEventAccessor soundEventAccessor;

    public MxSound(int playID, SoundCategory categoryIn)
    {
        super(ModSoundEvents.PCM_PROXY, categoryIn);
        this.playID = playID;
        this.sound = new PCMSound();
        this.volume = 1F;
        this.pitch = 1F;
        this.repeat = false;
        this.repeatDelay = 0;
        this.donePlaying = false;
        this.x = 0F;
        this.y = 0F;
        this.z = 0F;
        this.attenuationType = AttenuationType.LINEAR;
        this.soundEventAccessor = new SoundEventAccessor(this.sound.getSoundLocation(), "fourteen.subtitle.pcm-proxy");
    }

    // This is used as the key for our PlaySoundEvent handler
    MxSound()
    {
        super(ModSoundEvents.PCM_PROXY, SoundCategory.MASTER);
    }

    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        return super.createAccessor(handler);
    }

    @Override
    public void tick()
    {

    }

    protected void onUpdate() { /* NOP */ }

    protected void setDonePlaying()
    {
        this.donePlaying = true;
    }
}
