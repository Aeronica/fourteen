package net.aeronica.mods.fourteen;

import net.aeronica.mods.fourteen.audio.ClientAudio;
import net.aeronica.mods.fourteen.blocks.MusicBlock;
import net.aeronica.mods.fourteen.caches.FileHelper;
import net.aeronica.mods.fourteen.caps.LivingEntityModCapProvider;
import net.aeronica.mods.fourteen.config.FourteenConfig;
import net.aeronica.mods.fourteen.items.GuiTestItem;
import net.aeronica.mods.fourteen.items.MusicItem;
import net.aeronica.mods.fourteen.network.PacketDispatcher;
import net.aeronica.mods.fourteen.util.AntiNull;
import net.aeronica.mods.fourteen.util.MIDISystemUtil;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class Fourteen
{
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    private static final ItemGroup tab = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ObjectHolders.MUSICBLOCK);
        }
    };

    public Fourteen()
    {
        FourteenConfig.register(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        PacketDispatcher.register();
        LivingEntityModCapProvider.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MIDISystemUtil.mxTuneInit();
        MinecraftForge.EVENT_BUS.register(ClientAudio.class);
    }

    @SubscribeEvent
    public void event(FMLServerStartingEvent event) {
        FileHelper.initialize(event.getServer());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new MusicBlock().setRegistryName("musicblock"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
        {
            Item.Properties properties = new Item.Properties().maxStackSize(1).group(tab);

            itemRegistryEvent.getRegistry().register(new BlockItem(ObjectHolders.MUSICBLOCK, properties).setRegistryName("musicblock"));
            itemRegistryEvent.getRegistry().register(new MusicItem(properties).setRegistryName("musicitem"));
            itemRegistryEvent.getRegistry().register(new GuiTestItem(properties).setRegistryName("guitestitem"));
        }
    }

    @ObjectHolder(Reference.MOD_ID)
    public static class ObjectHolders
    {
        public final static MusicBlock MUSICBLOCK = AntiNull.nonNullInjected();
    }
}
