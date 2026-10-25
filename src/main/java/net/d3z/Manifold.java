package net.d3z;

import net.d3z.command.TeamCommands;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Manifold implements ModInitializer {
	public static final String MOD_ID = "manifold";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		TeamCommands.registerCommands();

		LOGGER.info("Initialization complete");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
