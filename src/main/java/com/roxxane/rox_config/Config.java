package com.roxxane.rox_config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import oshi.util.tuples.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Config {
	private static final ArrayList<Config> configs = new ArrayList<>();
	private boolean isLoaded = false;
	private final HashMap<String, Pair<Consumer<JsonObject>, Consumer<JsonObject>>> parts = new HashMap<>();
	private final ArrayList<String> blockedParts = new ArrayList<>();

	public final Path path;
	public final String id;
	public final Logger logger = LogUtils.getLogger();

	public Config(String id, String path) {
		this.id = id;
		this.path = Path.of(FMLPaths.CONFIGDIR.get().toString() + "/" + path + ".json");

		configs.add(this);
	}

	public Config(String id) {
		this(id, id);
	}

	protected void addPart(String partName, Consumer<JsonObject> getFunction, Consumer<JsonObject> defaultFunction) {
		if (parts.containsKey(partName))
			blockedParts.add(partName);

		if (blockedParts.contains(partName)) {
			parts.remove(partName);
			logger.warn("Tried to register part with duplicate name \"{}\" in config \"{}\". " +
				"Will not register any of the duplicates parts.", partName, id);
		} else
			parts.put(partName, new Pair<>(getFunction, defaultFunction));
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public void reload() throws IOException {
		isLoaded = false;

		Gson gson = new Gson();
		if (!Files.exists(path)) {
			JsonWriter writer = new JsonWriter(new FileWriter(path.toString()));
			JsonObject defaultJsonObject = new JsonObject();

			for (var entry : parts.entrySet())
				try {
					entry.getValue().getB().accept(defaultJsonObject);
				} catch (Exception exception) {
					logger.warn("Could not set default function for part \"{}\" in config \"{}\".", entry.getKey(), id);
					exception.printStackTrace();
				}

			// Closing
			gson.toJson(defaultJsonObject, writer);
			writer.close();
		}

		JsonReader reader = new JsonReader(new FileReader(path.toString()));
		JsonObject data = gson.fromJson(reader, JsonObject.class);

		for (var entry : parts.entrySet())
			try {
				entry.getValue().getA().accept(data);
			} catch (Exception exception) {
				logger.warn("Could not get function for part \"{}\" in config \"{}\".", entry.getKey(), id);
				exception.printStackTrace();
			}

		isLoaded = true;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	/** Gets a clone of available configs */
	public static Config[] getConfigs() {
		return configs.toArray(new Config[0]);
	}
}