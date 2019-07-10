package net.aeronica.mods.fourteen.audio;

import net.aeronica.mods.fourteen.Fourteen;
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;

public class PCMSound extends Sound
{
    public PCMSound()
    {
        // String nameIn, float volumeIn, float pitchIn, int weightIn, Type typeIn, boolean streamingIn, boolean preloadIn, int attenuationDistanceIn
        super(Fourteen.MODID + ":pcm-proxy", 1F, 1F, 0, Type.SOUND_EVENT, true, false, 64);
    }

    @Override
    public ResourceLocation getSoundAsOggLocation()
    {
        return new ResourceLocation(Fourteen.MODID, "sounds/" + getSoundLocation().getPath() + ".nul");
    }
}
