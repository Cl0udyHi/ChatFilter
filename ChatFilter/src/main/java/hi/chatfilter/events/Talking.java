package hi.chatfilter.events;

import hi.chatfilter.Main;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;

public class Talking implements Listener {

    private final FileConfiguration config;

    public Talking() {
        this.config = Main.getPlugin().getConfig();
    }

    private boolean isFlagged(String input) {
        Set<String> badWords = new HashSet<>(config.getStringList("words"));
        String[] words = input.toLowerCase().split("\\s+");

        for (String word : words) {
            for (String badWord : badWords) {
                String pureWord = word.replaceAll("[^a-zA-Z0-9]", "");

                if (pureWord.contains(badWord) || word.contains(badWord)) return true;
            }
        }

        return false;
    }

    @EventHandler
    private void onSpeak(AsyncChatEvent event) {
        RegisteredListener[] RegisteredListeners = AsyncChatEvent.getHandlerList().getRegisteredListeners();

        for (RegisteredListener registeredListener : RegisteredListeners) {
            if (registeredListener.getListener() instanceof TypingEvent) {
                return;
            }
        }

        Player player = event.getPlayer();
        List<String> badWords = config.getStringList("words");
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());

        if (isFlagged(message)) {
            Component msg = Component.text()
                    .append(Component.text("\n"))
                    .append(Component.text("WOAH! WATCH YOUR LANGUAGE\n").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text("Your message was flagged because it contained a word that isn't allowed.").color(NamedTextColor.RED))
                    .append(Component.text("\n"))
                    .build();

            player.sendMessage(msg);
            event.setCancelled(true);
        }
    }
}
