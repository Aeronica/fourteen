package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ClientAudio
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static SoundEngine soundEngine;
    private static SoundHandler soundHandler;
    private static int counter;

    private ClientAudio() { /* NOP */ }

    private static void init(SoundEngine se)
    {
        if (soundHandler == null || soundEngine == null)
        {
            soundEngine = se;
            soundHandler = soundEngine.sndHandler;
        }
    }

    // SoundEngine
    // private final Map<ISound, ChannelManager.Entry> field_217942_m = Maps.newHashMap(); // AT this so we can attach the PCM the audio stream
    // private final Multimap<SoundCategory, ISound> field_217943_n = HashMultimap.create(); // AT this for monitoring our ISounds
    // private final List<ITickableSound> tickableSounds = Lists.newArrayList(); // AT this for monitoring
    //

    //    this.field_217939_i.func_217917_b(sound.getSoundAsOggLocation()).thenAccept((p_217928_1_) -> {
    //        channelmanager$entry.func_217888_a((p_217935_1_) -> {
    //            p_217935_1_.func_216433_a(p_217928_1_);
    //            p_217935_1_.func_216438_c();
    //            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(this, isound, p_217935_1_));
    //        });
    //    });

    //    public CompletableFuture<IAudioStream> func_217917_b(ResourceLocation p_217917_1_) {
    //        return CompletableFuture.supplyAsync(() -> {
    //            try {
    //                IResource iresource = this.resourceManager.getResource(p_217917_1_);
    //                InputStream inputstream = iresource.getInputStream();
    //                return new OggAudioStream(inputstream);
    //            } catch (IOException ioexception) {
    //                throw new CompletionException(ioexception);
    //            }
    //        }, Util.getServerExecutor());
    //    }



    public static CompletableFuture<IAudioStream> submitStream()
    {
        return CompletableFuture.supplyAsync(() ->
                                             {
                                                 try
                                                 {
                                                     return new PCMNoiseStream();
                                                 } catch (IOException ioexception)
                                                 {
                                                     throw new CompletionException(ioexception);
                                                 }
                                             }, Util.getServerExecutor());
    }

    private static void dump()
    {
        if (soundEngine != null && soundHandler != null)
        {
            synchronized (soundEngine.field_217942_m)
            {
                for (Map.Entry<ISound, ChannelManager.Entry> entry : soundEngine.field_217942_m.entrySet())
                    if (entry.getKey() instanceof MxSound && !entry.getValue().func_217889_a())
                    {
                        submitStream().thenAccept(iAudioStream -> {
                            entry.getValue().func_217888_a(soundSource->{
                                soundSource.func_216433_a(iAudioStream);
                                soundSource.func_216438_c();
                            });
                        });
                        LOGGER.debug("ISound {}, {}", entry.getKey(), entry.getValue());
                    }
            }
        }
    }

    @SubscribeEvent
    public static void event(TickEvent.ClientTickEvent event)
    {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END)
        {
            /* once every 1/4 second */
            if (counter++ % 5 == 0)
                dump();
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
    public static void event(SoundEvent.SoundSourceEvent event)
    {
        if (event.getSound().getSoundLocation().equals(ModSoundEvents.PCM_PROXY.getRegistryName()))
            LOGGER.debug("SoundSourceEvent", event.getName());
    }

    @SubscribeEvent
    public static void event(PlaySoundEvent event)
    {
        init(event.getManager());
        ResourceLocation soundLocation = event.getSound().getSoundLocation();
        LOGGER.debug("PlaySoundEvent {}", soundLocation);
        if (soundLocation.equals(ModSoundEvents.PCM_PROXY.getRegistryName()))
        {
            LOGGER.debug("pcm-proxy SoundEvent detected");
            event.setResultSound(new MovingMusic(0));
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
