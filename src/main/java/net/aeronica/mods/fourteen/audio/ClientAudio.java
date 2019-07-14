package net.aeronica.mods.fourteen.audio;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.aeronica.mods.fourteen.Fourteen;
import net.aeronica.mods.fourteen.managers.PlayIdSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import java.util.*;
import java.util.concurrent.*;

public class ClientAudio
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Object THREAD_SYNC = new Object();
    private static Minecraft mc = Minecraft.getInstance();
    private static SoundEngine soundEngine;
    private static SoundHandler soundHandler;
    private static int counter;
    private static Queue<Integer> delayedAudioDataRemovalQueue = new ConcurrentLinkedDeque<>();

    private static final int THREAD_POOL_SIZE = 2;
    /* PCM Signed Monaural little endian */
    private static final AudioFormat audioFormat3D = new AudioFormat(48000, 16, 1, true, false);
    /* PCM Signed Stereo little endian */
    private static final AudioFormat audioFormatStereo = new AudioFormat(48000, 16, 2, true, false);
    /* Used to track which player/groups queued up music to be played by PlayID */
    private static Queue<Integer> playIDQueue01 = new ConcurrentLinkedQueue<>(); // Polled in ClientAudio#PlaySoundEvent
    private static Queue<Integer> playIDQueue02 = new ConcurrentLinkedQueue<>(); // Polled in PCMAudioStream
    private static Queue<Integer> playIDQueue03 = new ConcurrentLinkedQueue<>(); // Polled in initializeCodec
    private static final Map<Integer, AudioData> playIDAudioData = new ConcurrentHashMap<>();

    private static ExecutorService executorService = null;
    private static ThreadFactory threadFactory = null;

    private ClientAudio() { /* NOP */ }

    public static synchronized Set<Integer> getActivePlayIDs()
    {
        return Collections.unmodifiableSet(new HashSet<>(playIDAudioData.keySet()));
    }

    private static void startThreadFactory()
    {
        if (threadFactory == null)
        {
            threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat(Fourteen.MODID + " ClientAudio-%d")
                    .setDaemon(true)
                    .setPriority(Thread.NORM_PRIORITY)
                    .build();
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, threadFactory);
        }
    }

    private static void init(SoundEngine se)
    {
        if (soundHandler == null || soundEngine == null)
        {
            soundEngine = se;
            soundHandler = soundEngine.sndHandler;
        }
    }

    public enum Status
    {
        WAITING, READY, ERROR, DONE
    }

    private static synchronized void addPlayIDQueue(int playID)
    {
        playIDQueue01.add(playID);
        playIDQueue02.add(playID);
        playIDQueue03.add(playID);
    }

    private static int pollPlayIDQueue01()
    {
        return playIDQueue01.peek() == null ? PlayIdSupplier.INVALID : playIDQueue01.poll();
    }

    static int pollPlayIDQueue02()
    {
        return playIDQueue02.peek() == null ? PlayIdSupplier.INVALID : playIDQueue02.poll();
    }

    private static int pollPlayIDQueue03()
    {
        return playIDQueue03.peek() == null ? PlayIdSupplier.INVALID : playIDQueue03.poll();
    }

    private static int peekPlayIDQueue03()
    {
        return playIDQueue03.peek() == null ? PlayIdSupplier.INVALID : playIDQueue03.peek();
    }

    static AudioData getAudioData(Integer playID)
    {
        synchronized (THREAD_SYNC)
        {
            return playIDAudioData.get(playID);
        }
    }

    private static synchronized void setUuid(Integer playID, String uuid)
    {
        AudioData audioData = playIDAudioData.get(playID);
        if (audioData != null)
            audioData.setUuid(uuid);
    }

    static synchronized void setISound(Integer playID, ISound iSound)
    {
        AudioData audioData = playIDAudioData.get(playID);
        if (audioData != null)
            audioData.setISound(iSound);
    }

    @Nullable
    private static BlockPos getBlockPos(Integer playID)
    {
        AudioData audioData = playIDAudioData.get(playID);
        return (audioData != null) ? audioData.getBlockPos() : null;
    }

    private static SoundRange getSoundRange(Integer playID)
    {
        AudioData audioData = playIDAudioData.get(playID);
        return audioData != null ? audioData.getSoundRange() :  SoundRange.NORMAL;
    }

    static boolean hasPlayID(int playID)
    {
        return !playIDAudioData.isEmpty() && playIDAudioData.containsKey(playID);
    }

    private static boolean isClientPlayer(Integer playID)
    {
        AudioData audioData = playIDAudioData.get(playID);
        return audioData != null && audioData.isClientPlayer();
    }

    /**
     * For players.
     * @param playID unique submission identifier.
     * @param musicText MML string
     */
    public static void play(Integer playID, String musicText)
    {
        play(playID, null, musicText, false, SoundRange.NORMAL, null);
    }

    /**
     * For TileEntities placed in the world.
     * @param playID unique submission identifier.
     * @param pos block position in the world
     * @param musicText MML string
     * @param soundRange defines the attenuation: NATURAL or INFINITY respectively
     */
    public static void play(Integer playID, BlockPos pos, String musicText, SoundRange soundRange)
    {
        play(playID, pos, musicText, false, soundRange, null);
    }

    public static void playLocal(int playId, String musicText, IAudioStatusCallback callback)
    {
        play(playId, mc.player.getPosition(), musicText, true, SoundRange.INFINITY, callback);
    }


    // Determine if audio is 3D spacial or background
    // Players playing solo, or in JAMS hear their own audio without 3D effects or falloff.
    // Musical Automata that have SoundRange.INFINITY will play for all clients without 3D effects or falloff.
    private static void setAudioFormat(AudioData audioData)
    {
        if (audioData.isClientPlayer() || (audioData.getSoundRange() == SoundRange.INFINITY))
            audioData.setAudioFormat(audioFormatStereo);
        else audioData.setAudioFormat(audioFormat3D);
    }

    private static void play(int playID, @Nullable BlockPos pos, String musicText, boolean isClient, SoundRange soundRange, IAudioStatusCallback callback)
    {
        startThreadFactory();
        if(playID != PlayIdSupplier.INVALID)
        {
            addPlayIDQueue(playID);
            AudioData audioData = new AudioData(playID, pos, isClient, soundRange, callback);
            setAudioFormat(audioData);
            AudioData result = playIDAudioData.putIfAbsent(playID, audioData);
            if (result != null)
            {
                LOGGER.warn("ClientAudio#play: playID: %s has already been submitted", playID);
                return;
            }
            mc.getSoundHandler().play(new MovingMusic());
            executorService.execute(new ThreadedPlay(audioData, musicText));
        } else
        {
            LOGGER.warn("ClientAudio#play(Integer playID, BlockPos pos, String musicText): playID is null!");
        }
    }

    public static void stop(int playID)
    {
        AudioData audioData = playIDAudioData.get(playID);
        if (audioData != null && audioData.getISound() != null)
            soundHandler.stop(audioData.getISound());
    }

    private static class ThreadedPlay implements Runnable
    {
        private final AudioData audioData;
        private final String musicText;

        ThreadedPlay(AudioData audioData, String musicText)
        {
            this.audioData = audioData;
            this.musicText = musicText;
        }

        @Override
        public void run()
        {
            MML2PCM mml2PCM = new MML2PCM(audioData, musicText);
            mml2PCM.process();
        }
    }

    private static void cleanup()
    {
        playIDAudioData.keySet().forEach(ClientAudio::queueAudioDataRemoval);
        playIDQueue01.clear();
        playIDQueue02.clear();
        playIDQueue03.clear();
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



    private static CompletableFuture<IAudioStream> submitStream()
    {
        return CompletableFuture.supplyAsync(PCMAudioStream::new, Util.getServerExecutor());
    }

    private static void initializeCodec()
    {
        if (soundEngine != null && soundHandler != null && peekPlayIDQueue03() != PlayIdSupplier.INVALID)
        {
            synchronized (soundEngine.field_217942_m)
            {
                AudioData audioData = getAudioData(peekPlayIDQueue03());
                if (audioData.getISound() != null)
                {
                    ISound sound = audioData.getISound();
                    ChannelManager.Entry entry = soundEngine.field_217942_m.get(sound);
                    if (entry != null)
                    {
                        submitStream().thenAccept(iAudioStream -> entry.func_217888_a(soundSource ->
                            {
                                soundSource.func_216433_a(iAudioStream);
                                soundSource.func_216438_c();
                            }));
                        int playId = pollPlayIDQueue03();
                        LOGGER.debug("initializeCodec: PlayID {}, ISound {}", playId, sound);
                    }
                    else
                    {
                        int pid2 = pollPlayIDQueue02();
                        int pid3 = pollPlayIDQueue03();
                        playIDAudioData.remove(pid3);
                        LOGGER.debug("initializeCodec: failed - Queue2: {}, Queue 3: {}", pid2, pid3);
                    }

                }
            }
        }
    }

    private static void updateClientAudio()
    {
        if (soundEngine != null)
        {
            if(playIDAudioData.isEmpty() )
            {
                //resumeVanillaMusic();
                //setVanillaMusicPaused(false);
            } else if (!playIDAudioData.isEmpty())
            {
                // don't allow the timer to counter down while ClientAudio sessions are playing
                //setVanillaMusicTimer(Integer.MAX_VALUE);
            }
            // Remove inactive playIDs
            removeQueuedAudioData();
            for (Map.Entry<Integer, AudioData> entry : playIDAudioData.entrySet())
            {
                AudioData audioData = entry.getValue();
                Status status = audioData.getStatus();
                if (status == Status.ERROR || status == Status.DONE || !soundEngine.field_217942_m.containsKey(audioData.getISound()))
                {
                    // Stopping playing audio takes 100 milliseconds. e.g. SoundSystem fadeOut(<source>, <delay in ms>)
                    // To prevent audio clicks/pops we have the wait at least that amount of time
                    // before removing the AudioData instance for this playID.
                    // Therefore the removal is queued for 250 milliseconds.
                    // e.g. the client tick setup to trigger once every 1/4 second.
                    queueAudioDataRemoval(entry.getKey());
                    LOGGER.debug("updateClientAudio: AudioData for playID queued for removal");
                }
            }
        }
    }

    private static void removeQueuedAudioData()
    {
        while (!delayedAudioDataRemovalQueue.isEmpty())
            if (delayedAudioDataRemovalQueue.peek() != null)
                playIDAudioData.remove(Objects.requireNonNull(delayedAudioDataRemovalQueue.poll()));
    }

    public static void queueAudioDataRemoval(int playId)
    {
        stop(playId);
        delayedAudioDataRemovalQueue.add(playId);
    }

    @SubscribeEvent
    public static void event(TickEvent.ClientTickEvent event)
    {
        if (event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END)
        {
            /* once every 1/4 second */
            if (counter++ % 5 == 0)
                initializeCodec();

            if (counter % 20 == 0)
                updateClientAudio();
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
        cleanup();
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
        if (soundLocation.equals(ModSoundEvents.PCM_PROXY.getRegistryName()))
        {
            int playId = pollPlayIDQueue01();
            if (playId != PlayIdSupplier.INVALID)
            {
                LOGGER.debug("pcm-proxy SoundEvent detected");
                event.setResultSound(new MovingMusic(playId));
            }
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
