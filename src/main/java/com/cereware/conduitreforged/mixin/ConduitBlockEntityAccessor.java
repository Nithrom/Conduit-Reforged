package com.cereware.conduitreforged.mixin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Acceso a la detección vanilla del marco del conduit. */
@Mixin(ConduitBlockEntity.class)
public interface ConduitBlockEntityAccessor {
	@Invoker("updateShape")
	static boolean conduitreforged$updateShape(Level level, BlockPos pos, List<BlockPos> frame) {
		throw new AssertionError();
	}
}
