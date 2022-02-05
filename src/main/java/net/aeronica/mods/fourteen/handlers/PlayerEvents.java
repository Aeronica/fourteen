package net.aeronica.mods.fourteen.handlers;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.caps.stages.IServerStageAreas;
import net.aeronica.mods.fourteen.caps.stages.ServerStageAreaProvider;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PlayerEvents
{
    @SubscribeEvent
    public static void event(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(!event.getEntityLiving().getCommandSenderWorld().isClientSide())
            ServerStageAreaProvider.getServerStageAreas(event.getEntity().level).ifPresent(
                    IServerStageAreas::sync);
    }
}
