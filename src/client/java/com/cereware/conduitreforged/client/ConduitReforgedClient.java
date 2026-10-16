package com.cereware.conduitreforged.client;

import com.cereware.conduitreforged.ConduitConfig;
import com.cereware.conduitreforged.ConduitData;
import com.cereware.conduitreforged.ConduitFrame;
import com.cereware.conduitreforged.ConsumeConduitPayload;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;

public class ConduitReforgedClient implements ClientModInitializer {
	private static boolean openConfigNextTick;

	@Override
	public void onInitializeClient() {
		// Clic derecho con la mano vacía sobre un conduit activo: consumir su marco
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (!level.isClientSide() || hand != InteractionHand.MAIN_HAND || !player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
			BlockPos pos = hit.getBlockPos();
			if (!level.getBlockState(pos).is(Blocks.CONDUIT)) return InteractionResult.PASS;
			List<BlockPos> frame = ConduitFrame.scan(level, pos);
			int lvl = ConduitData.levelFor(frame.size());
			if (lvl == 0) return InteractionResult.PASS;
			if (!ConduitConfig.get().showConsumeConfirmation) {
				ClientPlayNetworking.send(new ConsumeConduitPayload(pos));
			} else {
				Minecraft mc = Minecraft.getInstance();
				mc.gui.setScreen(new ConfirmScreen(yes -> {
					if (yes) ClientPlayNetworking.send(new ConsumeConduitPayload(pos));
					mc.gui.setScreen(null);
				}, Component.translatable("gui.conduitreforged.confirm.title"),
					Component.translatable("gui.conduitreforged.confirm.message", frame.size(),
						Component.translatable("enchantment.level." + lvl))));
			}
			return InteractionResult.SUCCESS;
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, ctx) -> dispatcher.register(
			ClientCommands.literal("conduitreforged").then(ClientCommands.literal("config").executes(c -> {
				openConfigNextTick = true;
				return 1;
			}))));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openConfigNextTick) {
				openConfigNextTick = false;
				client.gui.setScreen(new ConduitConfigScreen(null));
			}
		});
	}
}
