package com.tom.peripherals;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class Config {
	public static class Server {
		public IntValue maxScreenSize;
		public IntValue maxVRAMSize;

		private Server(ModConfigSpec.Builder builder) {
			maxScreenSize = builder.comment("Max Screen Size").translation("config.toms_peripherals.maxScreenSize").
					defineInRange("maxScreenSize", 16, 1, 64);

			maxVRAMSize = builder.comment("Maximum VRAM Size in GPU peripherals").translation("config.toms_peripherals.maxVRAMSize").
					defineInRange("maxVRAMSize", 16 * 1024 * 1024, 16384, Integer.MAX_VALUE);
		}
	}

	public static class Common {

		public Common(ModConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"THIS IS ONLY THE COMMON CONFIG. It does not contain all the values adjustable for Tom's Peripherals",
					"The settings have been moved to toms-peripherals-server.toml",
					"That file is PER WORLD, meaning you have to go into 'saves/<world name>/serverconfig' to adjust it. Those changes will then only apply for THAT WORLD.",
					"You can then take that config file and put it in the 'defaultconfigs' folder to make it apply automatically to all NEW worlds you generate FROM THERE ON.",
					"This may appear confusing to many of you, but it is a new sensible way to handle configuration, because the server configuration is synced when playing multiplayer.").
			define("importantInfo", true);
		}
	}

	static final ModConfigSpec commonSpec;
	public static final Common COMMON;
	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	static final ModConfigSpec serverSpec;
	public static final Server SERVER;
	static {
		final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	public static int maxScreenSize, maxVRAMSize;

	public static void load(ModConfig modConfig) {
		if (modConfig.getType() == ModConfig.Type.SERVER) {
			maxScreenSize = SERVER.maxScreenSize.get();
			maxVRAMSize = SERVER.maxVRAMSize.get();
		}
	}
}
