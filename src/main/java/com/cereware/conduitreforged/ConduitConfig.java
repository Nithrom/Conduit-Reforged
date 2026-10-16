package com.cereware.conduitreforged;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** config/conduitreforged.json */
public final class ConduitConfig {
	/** Bloque en el que se convierte el marco consumido. */
	public String consumedBlock = "minecraft:water";
	/** Pedir confirmación antes de consumir (cliente). */
	public boolean showConsumeConfirmation = true;
	/** Contar la lluvia como "estar en el agua". */
	public boolean countRain = false;
	/** Conservar el conduit del hueco al morir. */
	public boolean keepConduitOnDeath = false;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static ConduitConfig instance = new ConduitConfig();

	public static ConduitConfig get() {
		return instance;
	}

	public static ConduitConfig copy() {
		return GSON.fromJson(GSON.toJson(instance), ConduitConfig.class);
	}

	public static void replaceAndSave(ConduitConfig c) {
		instance = c;
		save();
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve(ConduitReforged.MOD_ID + ".json");
	}

	public static void load() {
		try {
			if (Files.exists(file())) {
				try (Reader r = Files.newBufferedReader(file())) {
					ConduitConfig read = GSON.fromJson(r, ConduitConfig.class);
					if (read != null) instance = read;
				}
			}
		} catch (Exception e) {
			ConduitReforged.LOGGER.error("Config inválida, usando valores por defecto", e);
		}
		save();
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(file())) {
			GSON.toJson(instance, w);
		} catch (Exception e) {
			ConduitReforged.LOGGER.error("No se pudo guardar la config", e);
		}
	}
}
