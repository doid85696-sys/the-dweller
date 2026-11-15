package com.example.horror;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Наблюдатель: стоит вдалеке и смотрит. Посмотришь 2 секунды — или подойдёшь — получишь скример. */
public class WatcherEntity extends Monster {
    private int life, stare, scareTicks;
    private boolean scaring;

    public WatcherEntity(EntityType<WatcherEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setSilent(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier attributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).build();
    }

    @Override public boolean isInvulnerableTo(DamageSource s) { return true; }
    @Override public boolean isPushable() { return false; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        Player p = level().getNearestPlayer(this, 64);
        if (p == null || ++life > 1200) { discard(); return; }

        lookAt(EntityAnchorArgument.Anchor.EYES, p.getEyePosition());
        setYHeadRot(getYRot());
        setYBodyRot(getYRot());

        if (scaring) {
            if (++scareTicks > 10) discard();
            return;
        }

        Vec3 look = p.getViewVector(1f).normalize();
        Vec3 toMe = getEyePosition().subtract(p.getEyePosition()).normalize();
        boolean seen = look.dot(toMe) > 0.95 && p.hasLineOfSight(this);
        if (seen) stare++;

        if (stare > 40 || distanceTo(p) < 4) scare(p);
    }

    private void scare(Player p) {
        scaring = true;
        Vec3 f = p.getLookAngle();
        moveTo(p.getX() + f.x * 1.3, p.getY(), p.getZ() + f.z * 1.3, getYRot(), 0);
        level().playSound(null, p.blockPosition(), SoundEvents.ENDERMAN_SCREAM, SoundSource.HOSTILE, 2f, 0.5f);
        level().playSound(null, p.blockPosition(), SoundEvents.GHAST_SCREAM, SoundSource.HOSTILE, 1f, 0.5f);
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120));
        p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30));
    }
}
