package com.example.horror;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WatcherRenderer extends MobRenderer<WatcherEntity, EndermanModel<WatcherEntity>> {
    public WatcherRenderer(EntityRendererProvider.Context c) {
        super(c, new EndermanModel<>(c.bakeLayer(ModelLayers.ENDERMAN)), 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(WatcherEntity e) {
        // Своя текстура: assets/horror/textures/entity/watcher.png (замени на любую страшную)
        return new ResourceLocation("textures/entity/enderman/enderman.png");
    }
}
