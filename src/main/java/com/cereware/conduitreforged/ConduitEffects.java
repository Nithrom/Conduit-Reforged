package com.cereware.conduitreforged;

import com.cereware.slots.api.CerewareSlotsApi;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

/** Aplica los poderes del conduit equipado mientras el jugador está en el agua. */
public final class ConduitEffects {
	private static final int DURATION = 260;
	private static final Map<UUID, Set<Holder<MobEffect>>> APPLIED = new HashMap<>();

	private ConduitEffects() {}

	static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int t = server.getTickCount();
			if (t % 20 != 0) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) tick(p, t % 40 == 0);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((h, s) -> APPLIED.remove(h.getPlayer().getUUID()));
	}

	private static boolean inWater(ServerPlayer p) {
		return ConduitConfig.get().countRain ? p.isInWaterOrRain() : p.isInWater();
	}

	private static void tick(ServerPlayer player, boolean attackTick) {
		Set<Holder<MobEffect>> now = new HashSet<>();
		ConduitData data = CerewareSlotsApi.getStack(player, ConduitReforged.SLOT).get(ConduitReforged.CONDUIT_DATA);
		if (data != null && player.isAlive() && inWater(player)) {
			apply(player, MobEffects.CONDUIT_POWER, now);
			if (data.level() >= 2) apply(player, MobEffects.DOLPHINS_GRACE, now);
			if (data.level() >= 3 && attackTick) attack(player);
		}
		Set<Holder<MobEffect>> previous = APPLIED.put(player.getUUID(), now);
		if (previous == null) return;
		for (Holder<MobEffect> e : previous) {
			if (now.contains(e)) continue;
			MobEffectInstance cur = player.getEffect(e);
			if (cur != null && cur.isAmbient() && cur.getDuration() <= DURATION) player.removeEffect(e);
		}
	}

	private static void apply(ServerPlayer p, Holder<MobEffect> effect, Set<Holder<MobEffect>> now) {
		now.add(effect);
		p.addEffect(new MobEffectInstance(effect, DURATION, 0, true, false, true));
	}

	/** Como el conduit vanilla completo: 4 de daño cada 2 s al hostil más cercano en el agua (≤ 8 bloques). */
	private static void attack(ServerPlayer player) {
		ServerLevel level = player.level();
		List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(8.0),
			e -> e instanceof Enemy && e.isAlive() && e.isInWaterOrRain());
		LivingEntity target = null;
		double best = Double.MAX_VALUE;
		for (LivingEntity e : targets) {
			double d = e.distanceToSqr(player);
			if (d <= 64 && d < best) { best = d; target = e; }
		}
		if (target == null) return;
		level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.PLAYERS, 1.0f, 1.0f);
		target.hurtServer(level, level.damageSources().magic(), 4.0f);
	}
}
