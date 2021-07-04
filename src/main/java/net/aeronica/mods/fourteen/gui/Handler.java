package net.aeronica.mods.fourteen.gui;

import net.minecraft.client.Minecraft;

public class Handler
{
    private static final Minecraft mc = Minecraft.getInstance();

    private Handler()
    {
        // NOP
    }

    public static void openTestScreen()
    {
        mc.tell(()->mc.setScreen(new TestScreen(null)));
    }
}
