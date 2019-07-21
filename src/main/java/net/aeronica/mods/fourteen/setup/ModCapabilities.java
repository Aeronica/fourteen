package net.aeronica.mods.fourteen.setup;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.caps.LivingEntityModCapProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Bus.MOD)
public class ModCapabilities
{
    private ModCapabilities() { /* NOP */ }
    /**
     * Register the capabilities.
     */
    @SubscribeEvent
    public static void registerCapabilities(final FMLCommonSetupEvent event) {
        LivingEntityModCapProvider.register();
    }
}
