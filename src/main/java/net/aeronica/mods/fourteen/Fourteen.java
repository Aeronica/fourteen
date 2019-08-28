package net.aeronica.mods.fourteen;

import net.aeronica.mods.fourteen.audio.ClientAudio;
import net.aeronica.mods.fourteen.blocks.*;
import net.aeronica.mods.fourteen.caches.FileHelper;
import net.aeronica.mods.fourteen.caps.LivingEntityModCapProvider;
import net.aeronica.mods.fourteen.config.FourteenConfig;
import net.aeronica.mods.fourteen.items.GuiTestItem;
import net.aeronica.mods.fourteen.items.MusicItem;
import net.aeronica.mods.fourteen.network.PacketDispatcher;
import net.aeronica.mods.fourteen.util.AntiNull;
import net.aeronica.mods.fourteen.util.KeyHandler;
import net.aeronica.mods.fourteen.util.MIDISystemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class Fourteen
{
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);
    private static final ItemGroup MOD_TAB = new ItemGroup(Reference.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ObjectHolders.MUSIC_BLOCK);
        }
    };

    public Fourteen()
    {
        FourteenConfig.register(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        if (EffectiveSide.get() == LogicalSide.CLIENT)
            MIDISystemUtil.mxTuneInit();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        PacketDispatcher.register();
        LivingEntityModCapProvider.register();
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(ObjectHolders.INV_TEST_CONTAINER, InvTestScreen::new);
        MinecraftForge.EVENT_BUS.register(KeyHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(ClientAudio.class);
    }

    @SubscribeEvent
    public void event(FMLServerStartingEvent event) {
        FileHelper.initialize(event.getServer());
    }

    @SubscribeEvent
    public void event(NetworkEvent.GatherLoginPayloadsEvent event) {

    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().register(new MusicBlock().setRegistryName("music_block"));
            blockRegistryEvent.getRegistry().register(new InvTestBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(1.5F)).setRegistryName("inv_test_block"));
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent)
        {
            Item.Properties properties = new Item.Properties().maxStackSize(1).group(MOD_TAB);

            itemRegistryEvent.getRegistry().register(new BlockItem(ObjectHolders.MUSIC_BLOCK, new Item.Properties().maxStackSize(64).group(MOD_TAB)).setRegistryName("music_block"));
            itemRegistryEvent.getRegistry().register(new MusicItem(properties).setRegistryName("music_item"));
            itemRegistryEvent.getRegistry().register(new GuiTestItem(properties).setRegistryName("gui_test_item"));
            itemRegistryEvent.getRegistry().register(new BlockItem(ObjectHolders.INV_TEST_BLOCK, new Item.Properties().maxStackSize(64).group(MOD_TAB)).setRegistryName("inv_test_block"));
        }

        @SubscribeEvent
        public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event)
        {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new InvTestContainer(windowId, inv.player.world, data.readBlockPos(), inv, inv.player)).setRegistryName("inv_test_container"));
        }

        @SubscribeEvent
        public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event)
        {
            event.getRegistry().register(TileEntityType.Builder.create(InvTestTile::new, ObjectHolders.INV_TEST_BLOCK).build(AntiNull.nonNullInjected()).setRegistryName("inv_test_tile"));
        }
    }

    @ObjectHolder(Reference.MOD_ID)
    public static class ObjectHolders
    {
        public static final Block MUSIC_BLOCK = AntiNull.nonNullInjected();

        public static final Block INV_TEST_BLOCK = AntiNull.nonNullInjected();
        public static final ContainerType<InvTestContainer> INV_TEST_CONTAINER = AntiNull.nonNullInjected();
        public static final TileEntityType<InvTestTile> INV_TEST_TILE = AntiNull.nonNullInjected();
    }
}
