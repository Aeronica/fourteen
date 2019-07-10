package net.aeronica.mods.fourteen.audio;

import net.aeronica.mods.fourteen.Fourteen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientAudio
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static SoundEngine soundEngine;
    private static SoundHandler soundHandler;

    private ClientAudio() { /* NOP */ }

    @Mod.EventBusSubscriber(value=Dist.CLIENT, modid = Fourteen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientAudioEvents
    {
        @SubscribeEvent
        public static void event(SoundSetupEvent event)
        {
            soundEngine = event.getManager();
            soundHandler = Minecraft.getInstance().getSoundHandler();
            LOGGER.debug("SoundSetupEvent");
        }

        @SubscribeEvent
        public static void event(SoundLoadEvent event) // sound reload
        {
            soundEngine = event.getManager();
            soundHandler = Minecraft.getInstance().getSoundHandler();
            LOGGER.debug("SoundLoadEvent");
        }

        @SubscribeEvent
        public static void event(PlaySoundEvent event)
        {
            ResourceLocation soundLocation = event.getSound().getSoundLocation();

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
}
