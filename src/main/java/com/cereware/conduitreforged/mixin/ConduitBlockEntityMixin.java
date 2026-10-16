package com.cereware.conduitreforged.mixin;

import com.cereware.conduitreforged.ConduitData;
import com.cereware.conduitreforged.ConduitDataHolder;
import com.cereware.conduitreforged.ConduitReforged;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Conserva ConduitData al colocar/romper el conduit y al guardar/cargar el mundo. */
@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin extends BlockEntity implements ConduitDataHolder {
	@Unique private static final String KEY = "conduitreforged:conduit_data";
	@Unique private @Nullable ConduitData conduitreforged$data;

	private ConduitBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override public @Nullable ConduitData conduitreforged$getData() { return conduitreforged$data; }
	@Override public void conduitreforged$setData(@Nullable ConduitData data) { conduitreforged$data = data; }

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		conduitreforged$data = components.get(ConduitReforged.CONDUIT_DATA);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		if (conduitreforged$data != null) {
			builder.set(ConduitReforged.CONDUIT_DATA, conduitreforged$data);
			builder.set(DataComponents.MAX_STACK_SIZE, 1);
		}
	}

	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		super.removeComponentsFromTag(output);
		output.discard(KEY);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	private void conduitreforged$save(ValueOutput output, CallbackInfo ci) {
		output.storeNullable(KEY, ConduitData.CODEC, conduitreforged$data);
	}

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	private void conduitreforged$load(ValueInput input, CallbackInfo ci) {
		conduitreforged$data = input.read(KEY, ConduitData.CODEC).orElse(null);
	}
}
