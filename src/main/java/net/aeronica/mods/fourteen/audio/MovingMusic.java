package net.aeronica.mods.fourteen.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovingMusic extends MxSound
{
    private static final Logger LOGGER = LogManager.getLogger();
    private Entity entity;

    /**
     * Implements ISound<br></br>
     * For musical machines carried or used in the world
     * @param audioData
     */
    public MovingMusic(AudioData audioData, Entity entity)
    {
        super(audioData, SoundCategory.PLAYERS);
        this.entity = entity;
        this.donePlaying = false;
        this.x = (float) entity.posX;
        this.y = (float) entity.posY;
        this.z = (float) entity.posZ;
        LOGGER.debug("MovingMusic entity {}", entity.getName().getUnformattedComponentText());
    }

    @Override
    public void onUpdate()
    {
        if (!entity.isAlive() && !donePlaying)
        {
            this.donePlaying = true;
            ClientAudio.queueAudioDataRemoval(playID);
            LOGGER.debug("MovingMusic playID {} done", playID);
        } else
        {
            this.x = (float) entity.posX;
            this.y = (float) entity.posY;
            this.z = (float) entity.posZ;
        }
    }
}
