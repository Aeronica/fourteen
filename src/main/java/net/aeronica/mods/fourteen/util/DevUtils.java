package net.aeronica.mods.fourteen.util;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.config.FourteenConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class DevUtils
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Mod.EventBusSubscriber(modid=Reference.MOD_ID, value=Dist.CLIENT)
    public static class DevEvents
    {
        private static Minecraft mc = Minecraft.getInstance();
        private static boolean lastShowPlayerNameState;

        // OBS likes unique window titles. Update on logon and/or changing dimension
        @SubscribeEvent
        public static void onEvent(EntityJoinWorldEvent event)
        {
            if ((event.getEntity() instanceof ClientPlayerEntity))
            {
                ClientPlayerEntity player = (ClientPlayerEntity) event.getEntity();
                String windowTitle = String.format("Minecraft %s", SharedConstants.getVersion().getName());

                if (FourteenConfig.CLIENT.showPlayerName.get())
                    windowTitle = String.format("Minecraft %s - %s", SharedConstants.getVersion().getName(), player.getScoreboardName());

                if (lastShowPlayerNameState != FourteenConfig.CLIENT.showPlayerName.get())
                {
                    long handle = mc.mainWindow.getHandle();
                    GLFW.glfwSetWindowTitle(handle, windowTitle);
                    lastShowPlayerNameState = FourteenConfig.CLIENT.showPlayerName.get();
                }
            }
        }
    }
}
