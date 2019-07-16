package net.aeronica.mods.fourteen.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class MusicPositioned extends MxSound
{

    MusicPositioned(AudioData audioData)
    {
        super(audioData, SoundCategory.RECORDS);
        this.attenuationType = AttenuationType.LINEAR;
        BlockPos blockPos = audioData.getBlockPos();
        if (blockPos != null)
        {
            this.x = blockPos.getX();
            this.y = blockPos.getY();
            this.z = blockPos.getZ();
            this.volume = 4.0F;
        }
    }

    @Override
    protected void onUpdate()
    {
        // NOP
    }
}
