package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.audio.IAudioStream;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import static net.aeronica.mods.fourteen.audio.ClientAudio.Status.*;

public class PCMNoiseStream implements IAudioStream
{
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    private static final int SAMPLE_SIZE = 8192;
    private final AudioData audioData;
    private AudioInputStream audioInputStream = null;
    private ByteBuffer noiseBuffer = BufferUtils.createByteBuffer(SAMPLE_SIZE * 2);
    private Random randInt;
    private boolean hasStream = false;
    private int zeroBufferCount = 0;

    public PCMNoiseStream(AudioData audioData) throws IOException
    {
        this.audioData = audioData;
        randInt = new java.util.Random(System.currentTimeMillis());
        nextNoiseZeroBuffer();
    }

    private void nextNoiseZeroBuffer()
    {
        for (int i = 0; i < SAMPLE_SIZE; i++)
        {
            int x = (short) (randInt.nextInt() / 3) * 2;
            noiseBuffer.put((byte) x);
            noiseBuffer.put((byte) (x >> 8));
        }
        noiseBuffer.flip();
    }

    @Override
    public AudioFormat func_216454_a()
    {
        // PCM Signed Monaural little endian
        return audioData.getAudioFormat();
    }

    @Override
    /*
     * read - for static pre-loaded audio - not used
     */
    public ByteBuffer func_216453_b() throws IOException
    {
        LOGGER.debug("ByteBuffer func_216453_b()");
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(SAMPLE_SIZE * 2);
        byteBuffer.put(noiseBuffer);
        noiseBuffer.flip();
        nextNoiseZeroBuffer();
        byteBuffer.flip();
        return byteBuffer;
    }

    @Nullable
    @Override
    /*
     * streamRead(int bufferSize) - for streaming audio
     */
    public ByteBuffer func_216455_a(int p_216455_1_) throws IOException
    {
        if (hasInputStreamError())
            return null;
        notifyOnInputStreamAvailable();

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(p_216455_1_ + (SAMPLE_SIZE * 2));
        LOGGER.debug("ByteBuffer func_216455_a( {} )", p_216455_1_);
        byteBuffer.put(noiseBuffer);
        noiseBuffer.flip();
        nextNoiseZeroBuffer();
        byteBuffer.flip();
        return byteBuffer;
    }

    private void notifyOnInputStreamAvailable()
    {
        if (!hasStream && (audioData.getStatus() == READY))
        {
            audioInputStream = audioData.getAudioStream();
            try
            {
                if (audioInputStream.available() > 0)
                    hasStream = true;
            }
            catch (IOException e)
            {
                LOGGER.error("audioInputStream error");
                audioDataSetStatus(ERROR);
            }
        }
    }

    private void audioDataSetStatus(ClientAudio.Status status)
    {
        if (audioData != null) audioData.setStatus(status);
    }

    private boolean hasInputStreamError()
    {
        if (audioData == null || audioData.getStatus() == ERROR)
        {
            LOGGER.error("Not initialized in 'read'");
            return true;
        }

        return false;
    }

    @Override
    public void close() throws IOException
    {
        if (audioInputStream != null)
            audioInputStream.close();
    }
}
