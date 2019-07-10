package net.aeronica.mods.fourteen.audio;


import net.aeronica.mods.fourteen.Fourteen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@ObjectHolder(Fourteen.MODID)
public class ModSoundEvents
{
    @ObjectHolder("pcm-proxy")
    public static final SoundEvent PCM_PROXY = registerSound("pcm-proxy");
    private static final Logger LOGGER = LogManager.getLogger();

    private ModSoundEvents() { /* NOP */ }

    /**
     * Register a {@link SoundEvent}.
     *
     * @param soundName The SoundEvent's name without the [MODID] prefix
     * @return The SoundEvent
     * @author Choonster
     */
    private static SoundEvent registerSound(String soundName)
    {
        final ResourceLocation soundID = new ResourceLocation(Fourteen.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }

    @Mod.EventBusSubscriber(modid = Fourteen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler
    {
        private RegistrationHandler() { /* NOP */ }

        @SubscribeEvent
        public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event)
        {
            event.getRegistry().registerAll(PCM_PROXY);
            LOGGER.debug("Register pcm-proxy.");
        }
    }
}
