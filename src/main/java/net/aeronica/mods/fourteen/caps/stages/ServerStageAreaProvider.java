package net.aeronica.mods.fourteen.caps.stages;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.caps.SerializableCapabilityProvider;
import net.aeronica.mods.fourteen.util.AntiNull;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ServerStageAreaProvider
{
    private static final Logger LOGGER = LogManager.getLogger(ServerStageAreaProvider.class);

    @CapabilityInject(IServerStageAreas.class)
    public static Capability<IServerStageAreas> STAGE_AREA_CAP = AntiNull.nonNullInjected();

    private ServerStageAreaProvider() { /* NOP */ }

    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "stage_area");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IServerStageAreas.class, new Capability.IStorage<IServerStageAreas>()
        {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IServerStageAreas> capability, final IServerStageAreas instance, Direction side)
            {
                return instance.serializeNBT();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void readNBT(Capability<IServerStageAreas> capability, final IServerStageAreas instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof ServerStageAreas))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((ServerStageAreas) instance).deserializeNBT(nbt);
            }
        }, () -> null);
    }

    public static LazyOptional<IServerStageAreas> getServerStageAreas(final World world)
    {
        return world.getCapability(STAGE_AREA_CAP, null);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void event(final AttachCapabilitiesEvent<World> event)
        {
            final World world = event.getObject();
            final ServerStageAreas serverStageAreas = new ServerStageAreas(world.dimension());
            event.addCapability(ID, new SerializableCapabilityProvider<>(STAGE_AREA_CAP, null, serverStageAreas));
            event.addListener(() -> getServerStageAreas(world).invalidate());
            LOGGER.debug("AttachCapabilitiesEvent<World> {} {}", world, world.dimension());
        }
    }
}