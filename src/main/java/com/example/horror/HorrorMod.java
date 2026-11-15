package com.example.horror;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(HorrorMod.MODID)
public class HorrorMod {
    public static final String MODID = "horror";
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<WatcherEntity>> WATCHER = ENTITIES.register("watcher",
            () -> EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 2.9f).build("watcher"));

    public HorrorMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        bus.addListener(this::attributes);
        MinecraftForge.EVENT_BUS.register(new ScareHandler());
    }

    private void attributes(EntityAttributeCreationEvent e) {
        e.put(WATCHER.get(), WatcherEntity.attributes());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
            e.registerEntityRenderer(WATCHER.get(), WatcherRenderer::new);
        }
    }
}
