package net.mcreator.terroristbunswagy.init;

import org.bukkit.plugin.java.JavaPlugin;

import net.mcreator.terroristbunswagy.commands.Terror;

public class TerroristBunswagyModCommands {
	public static void register(JavaPlugin plugin) {
		plugin.getCommand("테러범분쇄기").setExecutor(new Terror());
	}
}
