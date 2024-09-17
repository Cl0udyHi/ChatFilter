package hi.chatfilter.commands;

import hi.chatfilter.Main;
import hi.chatfilter.events.TypingEvent;
import hi.chatfilter.menus.WordsMenu;
import hi.chatfilter.models.CommandHandler;
import hi.chatfilter.ultilities.WordsUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class chatfilter implements CommandHandler {
    private final FileConfiguration config;

    public chatfilter() {
        this.config = Main.getPlugin().getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be ran by a Player.");
            return true;
        }

        Player player = (Player) sender;
        Set<String> badWords = new HashSet<>(config.getStringList("words"));
        WordsUtil wordsUtil = new WordsUtil();

        if (cmd.getName().equalsIgnoreCase("chatfilter")) {
            AsyncChatEvent.getHandlerList().unregister(new TypingEvent(null, null)); //Stop Listening

            if (!(args.length > 0)) {
                player.openInventory(new WordsMenu(1, null).getInventory());
                return true;
            }

            if (!args[0].isEmpty() && !(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("add"))) {
                player.sendMessage(Component.text("Usage: /chatfilter < add | remove > [words]").color(NamedTextColor.RED));
                return true;
            }

            if (!(args.length > 1)) {
                player.sendMessage(Component.text("Usage: /chatfilter " + args[0] + " [words]").color(NamedTextColor.RED));
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                Set<String> words = new HashSet<>(Arrays.asList(args).subList(1, args.length));
                Set<String> wordsToAdd = new HashSet<>();  // Use a separate set to store words to be added
                boolean repeatError = false;

                for (String word : words) {
                    if (badWords.contains(word)) {
                        repeatError = true;
                        continue;
                    }
                    wordsToAdd.add(word);  // Collect words to be added
                }

                for (String word : wordsToAdd) {
                    wordsUtil.addWord(word);
                }
                if (!wordsToAdd.isEmpty()) {
                    Component feedbackMsg = Component.text("Added ").color(NamedTextColor.GRAY)
                            .append(Component.text(String.join(", ", wordsToAdd)).color(NamedTextColor.GREEN))
                            .append(Component.text(" to the blacklist!").color(NamedTextColor.GRAY));

                    player.sendMessage(feedbackMsg);
                }

                if (repeatError) {
                    player.sendMessage(Component.text("Some words you gave already exist in the blacklist.").color(NamedTextColor.RED));
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                Set<String> words = new HashSet<>(Arrays.asList(args).subList(1, args.length));
                Set<String> wordsToRemove = new HashSet<>();  // Use a separate set to store words to be removed

                for (String word : words) {
                    if (!badWords.contains(word)) {
                        continue;
                    }
                    wordsToRemove.add(word);  // Collect words to be removed
                }

                for (String word : wordsToRemove) {
                    wordsUtil.removeWord(word);
                }

                if (!wordsToRemove.isEmpty()) {
                    Component feedbackMsg = Component.text()
                            .append(Component.text("Removed ").color(NamedTextColor.GRAY))
                            .append(Component.text(String.join(", ", wordsToRemove)).color(NamedTextColor.RED))
                            .append(Component.text(" from the blacklist!").color(NamedTextColor.GRAY))
                            .build();

                    player.sendMessage(feedbackMsg);
                }
            }

            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        List<String> badWords = config.getStringList("words");

        if (args.length == 1) {
            return Arrays.asList("add", "remove");
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            for (int i = 1; i < (args.length - 1); i++) {
                badWords.remove(args[i]);
            }

            return badWords;
        }

        return Collections.emptyList();
    }
}
