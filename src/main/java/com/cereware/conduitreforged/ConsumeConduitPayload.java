package com.cereware.conduitreforged;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Cliente → servidor: consumir el marco del conduit en pos (ya confirmado). */
public record ConsumeConduitPayload(BlockPos pos) implements CustomPacketPayload {
	public static final Type<ConsumeConduitPayload> TYPE = new Type<>(ConduitReforged.id("consume"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConsumeConduitPayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, ConsumeConduitPayload::pos, ConsumeConduitPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
