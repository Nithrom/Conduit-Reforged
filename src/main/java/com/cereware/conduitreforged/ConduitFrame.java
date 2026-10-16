package com.cereware.conduitreforged;

import com.cereware.conduitreforged.mixin.ConduitBlockEntityAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Detección (con la lógica vanilla) y consumo del marco del conduit. */
public final class ConduitFrame {
	private ConduitFrame() {}

	/** Bloques del marco activo (vacía si el conduit no está activo). */
	public static List<BlockPos> scan(Level level, BlockPos pos) {
		List<BlockPos> frame = new ArrayList<>();
		if (!ConduitBlockEntityAccessor.conduitreforged$updateShape(level, pos, frame)) frame.clear();
		return frame;
	}

	public static void consume(ServerPlayer player, BlockPos pos) {
		Level level = player.level();
		if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64) return;
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof ConduitBlockEntity) || !(be instanceof ConduitDataHolder holder)) return;
		List<BlockPos> frame = scan(level, pos);
		int newLevel = ConduitData.levelFor(frame.size());
		ConduitData current = holder.conduitreforged$getData();
		if (newLevel == 0 || (current != null && newLevel <= current.level())) {
			player.sendSystemMessage(Component.translatable(newLevel == 0
				? "message.conduitreforged.not_active" : "message.conduitreforged.not_better"));
			return;
		}
		BlockState replacement = consumedState();
		for (BlockPos p : frame) level.setBlock(p, replacement, Block.UPDATE_ALL);
		holder.conduitreforged$setData(new ConduitData(newLevel, frame.size()));
		be.setChanged();
		level.playSound(null, pos, SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
		level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.BLOCKS, 1.5f, 0.8f);
		player.sendSystemMessage(Component.translatable("message.conduitreforged.consumed", frame.size(),
			Component.translatable("enchantment.level." + newLevel)));
	}

	private static BlockState consumedState() {
		try {
			return BuiltInRegistries.BLOCK.getValue(Identifier.parse(ConduitConfig.get().consumedBlock)).defaultBlockState();
		} catch (Exception e) {
			return Blocks.WATER.defaultBlockState();
		}
	}
}
