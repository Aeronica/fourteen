package net.aeronica.mods.fourteen;

import net.aeronica.mods.fourteen.audio.ClientAudio;
import net.aeronica.mods.fourteen.blocks.ModBlocks;
import net.aeronica.mods.fourteen.blocks.MusicBlock;
import net.aeronica.mods.fourteen.caches.FileHelper;
import net.aeronica.mods.fourteen.config.FourteenConfig;
import net.aeronica.mods.fourteen.items.GuiTestItem;
import net.aeronica.mods.fourteen.items.MusicItem;
import net.aeronica.mods.fourteen.network.PacketDispatcher;
import net.aeronica.mods.fourteen.setup.Setup;
import net.aeronica.mods.fourteen.util.MIDISystemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class Fourteen
{

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    public static Setup setup = new Setup();
    public static SimpleChannel network = PacketDispatcher.getNetworkChannel();

    public Fourteen()
    {
        FourteenConfig.register(ModLoadingContext.get());
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        MIDISystemUtil.mxTuneInit();
        MinecraftForge.EVENT_BUS.register(ClientAudio.class);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        FileHelper.initialize(event.getServer());
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new MusicBlock());
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
        {
            Item.Properties properties = new Item.Properties()
                    .group(setup.itemGroup);
            itemRegistryEvent.getRegistry().register(new BlockItem(ModBlocks.MUSICBLOCK, properties).setRegistryName("musicblock"));
            itemRegistryEvent.getRegistry().register(new MusicItem());
            itemRegistryEvent.getRegistry().register(new GuiTestItem());
        }
    }
}
