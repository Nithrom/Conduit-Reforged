package com.cereware.conduitreforged;

import com.cereware.slots.api.CerewareSlotsApi;
import com.cereware.slots.api.SlotDefinition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Conduit Reforged: consume el marco del conduit y llévalo contigo (solo funciona en el agua). */
public class ConduitReforged implements ModInitializer {
	public static final String MOD_ID = "conduitreforged";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier SLOT = id("conduit");

	public static final DataComponentType<ConduitData> CONDUIT_DATA = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		id("conduit_data"), new DataComponentType.Builder<ConduitData>()
			.persistent(ConduitData.CODEC).networkSynchronized(ConduitData.STREAM_CODEC).cacheEncoding().build());

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static boolean isPortableConduit(ItemStack stack) {
		return stack.is(Items.CONDUIT) && stack.has(CONDUIT_DATA);
	}

	@Override
	public void onInitialize() {
		ConduitConfig.load();
		ItemComponentTooltipProviderRegistry.addLast(CONDUIT_DATA);
		CerewareSlotsApi.register(new SlotDefinition(SLOT, Component.translatable("slot.conduitreforged.conduit"),
			Items.CONDUIT, ConduitReforged::isPortableConduit, 1, 20, () -> ConduitConfig.get().keepConduitOnDeath));

		PayloadTypeRegistry.serverboundPlay().register(ConsumeConduitPayload.TYPE, ConsumeConduitPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ConsumeConduitPayload.TYPE,
			(payload, context) -> ConduitFrame.consume(context.player(), payload.pos()));

		// Agacharse + clic derecho al aire: intercambia el conduit de la mano con el equipado
		UseItemCallback.EVENT.register((player, level, hand) -> {
			ItemStack held = player.getItemInHand(hand);
			if (!isPortableConduit(held) || !player.isShiftKeyDown()) return InteractionResult.PASS;
			if (player instanceof ServerPlayer sp && held.getCount() == 1) {
				ItemStack previous = CerewareSlotsApi.getStack(sp, SLOT);
				CerewareSlotsApi.setStack(sp, SLOT, held.copy());
				sp.setItemInHand(hand, previous);
				sp.level().playSound(null, sp.blockPosition(), SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.PLAYERS, 0.8f, 1.2f);
				sp.sendOverlayMessage(Component.translatable("message.conduitreforged.equipped", held.getHoverName()));
			}
			return InteractionResult.SUCCESS;
		});
		ConduitEffects.init();
	}
}
