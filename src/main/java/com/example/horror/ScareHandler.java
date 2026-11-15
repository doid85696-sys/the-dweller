package com.example.horror;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScareHandler {
    private static final SoundEvent[] FAKE = {
            SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.ENDERMAN_STARE, SoundEvents.SKELETON_AMBIENT,
            SoundEvents.ZOMBIE_AMBIENT, SoundEvents.CHEST_OPEN, SoundEvents.GRAVEL_STEP, SoundEvents.STONE_STEP
    };

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p)) return;
        if (p.tickCount % 100 != 0) return; // раз в 5 секунд

        ServerLevel lvl = p.serverLevel();
        RandomSource r = lvl.random;
        boolean dark = !lvl.isDay() || lvl.getMaxLocalRawBrightness(p.blockPosition()) < 7;
        if (!dark) return;

        Vec3 back = p.getLookAngle().multiply(1, 0, 1).normalize().scale(-1);

        // Звуки за спиной
        if (r.nextFloat() < 0.20f) {
            Vec3 pos = p.position().add(back.scale(4 + r.nextInt(6)));
            SoundEvent s = FAKE[r.nextInt(FAKE.length)];
            lvl.playSound(null, pos.x, pos.y, pos.z, s, SoundSource.AMBIENT, 1.2f, 0.6f + r.nextFloat() * 0.3f);
        }

        // Появление Наблюдателя за спиной
        if (r.nextFloat() < 0.05f
                && lvl.getEntitiesOfClass(WatcherEntity.class, p.getBoundingBox().inflate(64)).isEmpty()) {
            double x = p.getX() + back.x * 22, z = p.getZ() + back.z * 22;
            BlockPos at = BlockPos.containing(x, 0, z);
            if (!lvl.hasChunkAt(at)) return;
            BlockPos top = lvl.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at);
            WatcherEntity w = HorrorMod.WATCHER.get().create(lvl);
            if (w == null) return;
            w.moveTo(x, top.getY(), z, 0, 0);
            lvl.addFreshEntity(w);
        }
    }
}
