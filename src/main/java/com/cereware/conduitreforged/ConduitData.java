package com.cereware.conduitreforged;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

/** Datos de un conduit que ha consumido su marco. level 1-3, frameBlocks = bloques del marco consumido. */
public record ConduitData(int level, int frameBlocks) implements TooltipProvider {
	public static final Codec<ConduitData> CODEC = RecordCodecBuilder.create(i -> i.group(
		ExtraCodecs.intRange(1, 3).fieldOf("level").forGetter(ConduitData::level),
		ExtraCodecs.intRange(0, 42).optionalFieldOf("frame_blocks", 0).forGetter(ConduitData::frameBlocks)
	).apply(i, ConduitData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConduitData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, ConduitData::level, ByteBufCodecs.VAR_INT, ConduitData::frameBlocks, ConduitData::new);

	/** Nivel según bloques del marco: 16-27 → I, 28-41 → II, 42 → III. */
	public static int levelFor(int blocks) {
		return blocks >= 42 ? 3 : blocks >= 28 ? 2 : blocks >= 16 ? 1 : 0;
	}

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> out, TooltipFlag flag, DataComponentGetter components) {
		out.accept(Component.translatable("tooltip.conduitreforged.level",
			Component.translatable("enchantment.level." + level), frameBlocks).withStyle(ChatFormatting.AQUA));
		out.accept(Component.translatable("tooltip.conduitreforged.power").withStyle(ChatFormatting.GRAY));
		if (level >= 2) out.accept(Component.translatable("tooltip.conduitreforged.dolphin").withStyle(ChatFormatting.GRAY));
		if (level >= 3) out.accept(Component.translatable("tooltip.conduitreforged.attack").withStyle(ChatFormatting.GRAY));
		out.accept(Component.translatable("tooltip.conduitreforged.water_only").withStyle(ChatFormatting.DARK_GRAY));
	}
}
