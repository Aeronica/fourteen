package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.audio.IAudioStream;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class PCMNoiseStream implements IAudioStream
{
    private static final int SAMPLE_SIZE = 8192;
    private ByteBuffer noiseBuffer = BufferUtils.createByteBuffer(SAMPLE_SIZE * 2);
    private Random randInt;

    public PCMNoiseStream() throws IOException
    {
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
    }

    @Override
    public AudioFormat func_216454_a()
    {
        // PCM Signed Monaural little endian
        return new AudioFormat(48000, 16, 1, true, false);
    }

    @Override
    public ByteBuffer func_216453_b() throws IOException
    {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(SAMPLE_SIZE * 2);
        byteBuffer.put(noiseBuffer);
        noiseBuffer.flip();
        nextNoiseZeroBuffer();
        byteBuffer.flip();
        return byteBuffer;
    }

    @Nullable
    @Override
    public ByteBuffer func_216455_a(int p_216455_1_) throws IOException
    {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(p_216455_1_ + (SAMPLE_SIZE * 2));
        byteBuffer.put(noiseBuffer);
        noiseBuffer.flip();
        nextNoiseZeroBuffer();
        byteBuffer.flip();
        return byteBuffer;
    }

    @Override
    public void close() throws IOException
    {
        // NOP
    }
}
