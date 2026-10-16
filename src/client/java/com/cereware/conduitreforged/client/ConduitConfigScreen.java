package com.cereware.conduitreforged.client;

import com.cereware.conduitreforged.ConduitConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

/** Configuración en el juego (Mod Menu o /conduitreforged config). Guarda config/conduitreforged.json. */
public class ConduitConfigScreen extends Screen {
	private final @Nullable Screen parent;
	private final ConduitConfig edit = ConduitConfig.copy();
	private EditBox block;

	public ConduitConfigScreen(@Nullable Screen parent) {
		super(Component.translatable("config.conduitreforged.title"));
		this.parent = parent;
	}

	private static Component onOff(boolean b) {
		return Component.translatable(b ? "options.on" : "options.off");
	}

	@Override
	protected void init() {
		int x = width / 2 - 100, y = 40;
		addRenderableWidget(CycleButton.builder(ConduitConfigScreen::onOff, edit.countRain).withValues(true, false)
			.create(x, y, 200, 20, Component.translatable("config.conduitreforged.rain"), (b, v) -> edit.countRain = v));
		addRenderableWidget(CycleButton.builder(ConduitConfigScreen::onOff, edit.showConsumeConfirmation).withValues(true, false)
			.create(x, y + 24, 200, 20, Component.translatable("config.conduitreforged.confirm"), (b, v) -> edit.showConsumeConfirmation = v));
		addRenderableWidget(CycleButton.builder(ConduitConfigScreen::onOff, edit.keepConduitOnDeath).withValues(true, false)
			.create(x, y + 48, 200, 20, Component.translatable("config.conduitreforged.keep"), (b, v) -> edit.keepConduitOnDeath = v));
		block = new EditBox(font, x, y + 86, 200, 18, Component.translatable("config.conduitreforged.block"));
		block.setMaxLength(100);
		block.setValue(edit.consumedBlock);
		addRenderableWidget(block);
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
			edit.consumedBlock = block.getValue().trim();
			ConduitConfig.replaceAndSave(edit);
			onClose();
		}).bounds(width / 2 - 155, y + 120, 150, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose()).bounds(width / 2 + 5, y + 120, 150, 20).build());
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().gui.setScreen(parent);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(g, mouseX, mouseY, partialTick);
		g.text(font, title, width / 2 - font.width(title) / 2, 15, 0xFFFFFFFF, true);
		Component label = Component.translatable("config.conduitreforged.block");
		g.text(font, label, width / 2 - 100, 40 + 75, 0xFFA0A0A0, true);
	}
}
