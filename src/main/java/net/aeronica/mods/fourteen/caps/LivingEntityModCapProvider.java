package net.aeronica.mods.fourteen.caps;

import net.aeronica.mods.fourteen.Reference;
import net.aeronica.mods.fourteen.util.AntiNull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public final class LivingEntityModCapProvider
{
    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    @CapabilityInject(ILivingEntityModCap.class)
    public static final Capability<ILivingEntityModCap> LIVING_ENTITY_MOD_CAP_CAPABILITY = AntiNull.nonNullInjected();

    private LivingEntityModCapProvider() { /* NOP */ }

    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "living_mod_cap");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ILivingEntityModCap.class, new Capability.IStorage<ILivingEntityModCap>()
        {
            @Nullable
            @Override
            public INBT writeNBT(final Capability<ILivingEntityModCap> capability, final ILivingEntityModCap instance, final Direction side)
            {
                return new IntNBT(instance.getPlayId());
            }

            @Override
            public void readNBT(final Capability<ILivingEntityModCap> capability, final ILivingEntityModCap instance, final Direction side, final INBT nbt)
            {
                instance.setPlayId(((IntNBT) nbt).getInt());
            }
        }, () -> new LivingEntityModCap(null));
    }

    public static LazyOptional<ILivingEntityModCap> getLivingEntityModCap(final LivingEntity entity)
    {
        return entity.getCapability(LIVING_ENTITY_MOD_CAP_CAPABILITY, null);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler
    {
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event)
        {
            if (event.getObject() instanceof LivingEntity)
            {
                final LivingEntityModCap livingEntityModCap = new LivingEntityModCap((LivingEntity) event.getObject());
                event.addCapability(ID, new SerializableCapabilityProvider<>(LIVING_ENTITY_MOD_CAP_CAPABILITY, null, livingEntityModCap));
                LOGGER.debug("LivingEntityModCapProvider#attachCapabilities: {}", ((LivingEntity)event.getObject()));
            }
        }

        /**
         * Copy the player's playId when they respawn after dying or returning from the end.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event)
        {
            getLivingEntityModCap(event.getOriginal()).ifPresent(oldLivingEntityCap -> {
                getLivingEntityModCap(event.getEntityPlayer()).ifPresent(newLivingEntityCap -> {
                    newLivingEntityCap.setPlayId(oldLivingEntityCap.getPlayId());
                    LOGGER.debug("LivingEntityModCapProvider#PlayerEvent.Clone: {}", event.getEntityPlayer());
                });
            });
        }

        /**
         * Synchronise a player's playId to watching clients when they change dimensions.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerChangeDimension(final PlayerChangedDimensionEvent event)
        {
            getLivingEntityModCap(event.getPlayer()).ifPresent(ILivingEntityModCap::synchronise);
            LOGGER.debug("LivingEntityModCapProvider#PlayerChangedDimensionEvent: {}", event.getPlayer());
        }
    }
}
