package net.aeronica.mods.fourteen.gui;

import net.minecraft.client.Minecraft;

public class Handler
{
    private static final Minecraft mc = Minecraft.getInstance();

    public static void openTestScreen()
    {
        mc.enqueue(()->mc.displayGuiScreen(new TestScreen(null)));
    }
}
