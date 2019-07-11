package net.aeronica.mods.fourteen.audio;

import net.minecraft.util.SoundCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovingMusic extends MxSound
{
    private static final Logger LOGGER = LogManager.getLogger();
    private int ticks = 20;
    /**
     * This is used as the key for our PlaySoundEvent handler
     **/
    public MovingMusic()
    {
        super();
    }

    /**
     * Implements ISound<br></br>
     * For musical machines carried or used in the world
     */
    public MovingMusic(Integer playID)
    {
        super(playID, SoundCategory.PLAYERS);
    }

    @Override
    public void onUpdate()
    {
        if (ticks-- < 0 && !donePlaying)
        {
            this.donePlaying = true;
            LOGGER.debug("MovingMusic playID {} done", playID);
        }
    }
}
