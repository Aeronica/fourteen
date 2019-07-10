package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientAudio
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static SoundEngine soundEngine;
    private static SoundHandler soundHandler;

    private ClientAudio() { /* NOP */ }

    private static void init()
    {
        if (soundHandler == null)
        {
            soundHandler = Minecraft.getInstance().getSoundHandler();
            // need an AT to get the SoundEngine. Do I really need it?
        }
    }

    @SubscribeEvent
    public static void event(SoundSetupEvent event) // never gets called
    {
        soundEngine = event.getManager();
        soundHandler = Minecraft.getInstance().getSoundHandler();
        LOGGER.debug("SoundSetupEvent");
    }

    @SubscribeEvent
    public static void event(SoundLoadEvent event) // only called on sound reload. i.e. key-press F3+T
    {
        soundEngine = event.getManager();
        soundHandler = Minecraft.getInstance().getSoundHandler();
        LOGGER.debug("SoundLoadEvent");
    }

    @SubscribeEvent
    public static void event(PlaySoundEvent event)
    {
        init();
        ResourceLocation soundLocation = event.getSound().getSoundLocation();
        LOGGER.debug("PlaySoundEvent {}", soundLocation);
        if (soundLocation.equals(ModSoundEvents.PCM_PROXY.getRegistryName()))
        {
            LOGGER.debug("pcm-proxy SoundEvent detected");
        }
    }

    @SubscribeEvent
    public static void event(PlayStreamingSourceEvent event)
    {
        if (event.getSound().getSoundLocation().equals(ModSoundEvents.PCM_PROXY.getRegistryName()))
        {
            LOGGER.debug("pcm-proxy PlayStreamingSourceEvent");
        }
    }
}
