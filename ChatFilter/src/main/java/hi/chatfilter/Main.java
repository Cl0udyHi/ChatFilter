package hi.chatfilter;

import hi.chatfilter.commands.chatfilter;
import hi.chatfilter.events.Talking;
import hi.chatfilter.menus.WordsMenu;
import hi.chatfilter.models.CommandHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    private void registerCommand(String command, CommandHandler handler) {
        PluginCommand cmd = getCommand(command);

        if (cmd != null) {
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        
        registerCommand("chatfilter", new chatfilter());

        getServer().getPluginManager().registerEvents(new WordsMenu(1, null), this);
        getServer().getPluginManager().registerEvents(new Talking(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return instance;
    }
}
