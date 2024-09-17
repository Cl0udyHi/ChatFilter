package hi.chatfilter.menus;

import hi.chatfilter.Main;
import hi.chatfilter.events.TypingEvent;
import hi.chatfilter.ultilities.WordsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WordsMenu implements InventoryHolder, Listener {
    private final Inventory inventory;
    private static int page;
    private static int maxPages;
    private static String searchInput;

    private List<List<String>> splitList(List<String> list) {
        List<List<String>> newList = new ArrayList<>();

        for (int i = 0; i < list.size(); i += 21) {
            List<String> subList = list.subList(i, Math.min(i + 21, list.size()));
            newList.add(new ArrayList<>(subList));
        }

        return newList;
    }
    
    public WordsMenu(int page, String searchInput) {
        FileConfiguration config = Main.getPlugin().getConfig();
        List<String> badWords = config.getStringList("words");

        if (searchInput != null) {
            badWords.removeIf(badWord -> !searchInput.contains(badWord) && !badWord.contains(searchInput));
        }

        Collections.sort(badWords);
        List<List<String>> lists = new ArrayList<>(splitList(badWords));

        if (lists.isEmpty())
            lists.add(new ArrayList<>());

        if (lists.get(lists.size() - 1).size() >= 21)
            lists.add(new ArrayList<>());

        WordsMenu.maxPages = lists.size();
        WordsMenu.page = page;
        WordsMenu.searchInput = searchInput;

        Component title = Component.text("Blacklisted Words")
                .append(Component.text("                     "))
                .append(Component.text("(" + page +  "/" + maxPages + ")").color(NamedTextColor.WHITE));

        this.inventory = Bukkit.createInventory(this, 45, title);

        List<String> currentPageWords = lists.isEmpty() ? new ArrayList<>() : lists.get(page - 1);

        //Next-Page Button
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.itemName(Component.text(page == maxPages ? "§8Next Page" : "§fNext Page"));
        List<Component> nextLore = new ArrayList<>();
        if (page < maxPages) {
            nextLore.add(Component.text(""));
            nextLore.add(Component.text("§6Shift-Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§e>>")));
        }
        nextMeta.lore(nextLore);
        next.setItemMeta(nextMeta);
        inventory.setItem(26, next);

        //Previous-Page Button
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.itemName(Component.text(page == 1 ? "§8Previous Page" : "§fPrevious Page"));
        List<Component> previousLore = new ArrayList<>();
        if (page > 1) {
            previousLore.add(Component.text(""));
            previousLore.add(Component.text("§6Shift-Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§e<<")));
        }
        previousMeta.lore(previousLore);
        previous.setItemMeta(previousMeta);
        inventory.setItem(18, previous);

        // Search Button
        ItemStack search = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = search.getItemMeta();
        searchMeta.itemName(Component.text("§aSearch" + (searchInput != null ? ("§a: §f" + (searchInput.length() > 10 ? (searchInput.substring(0, 10) + "...") : searchInput) ) : ""), NamedTextColor.YELLOW));
        List<Component> searchLore = new ArrayList<>();
        searchLore.add(Component.text(""));
        searchLore.add(Component.text("§6Left-Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§esearch")));
        if (searchInput != null) {
            searchLore.add(Component.text("§6Right-Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§eclear")));
            searchMeta.setEnchantmentGlintOverride(true);
        }
        searchMeta.lore(searchLore);
        search.setItemMeta(searchMeta);
        inventory.setItem(41, search);

        // Close Button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.itemName(Component.text("§cClose"));
        close.setItemMeta(closeMeta);
        inventory.setItem(40, close);

//        short[] blankSlots = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 27, 35, 36, 37, 38, 39, 41, 42, 43, 44};
//
//        for (int slot : blankSlots) {
//            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
//            ItemMeta itemMeta = item.getItemMeta();
//            itemMeta.setMaxStackSize(1);
//            Component name = Component.text("");
//            itemMeta.itemName(name);
//            itemMeta.setHideTooltip(true);
//            item.setItemMeta(itemMeta);
//
//            inventory.setItem(slot, item);
//        }

        short[] wordSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

        for (int i = 0; i < wordSlots.length; i++) {
            if ((i + 1) > currentPageWords.size()) {
                ItemStack add = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta addMeta = add.getItemMeta();
                addMeta.setMaxStackSize(1);
                Component name = Component.text("§7Empty");
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(""));
                lore.add(Component.text("§6Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§eadd new word")));
                addMeta.lore(lore);
                addMeta.itemName(name);
                add.setItemMeta(addMeta);

                inventory.setItem(wordSlots[i], add);
                continue;
            }

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setMaxStackSize(1);
            Component name = Component.text(currentPageWords.get(i));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            lore.add(Component.text("§6Right-Click: ").decoration(TextDecoration.ITALIC, false).append(Component.text("§cdelete")));
            itemMeta.lore(lore);
            itemMeta.itemName(name);
            item.setItemMeta(itemMeta);

            inventory.setItem(wordSlots[i], item);
        }
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        WordsUtil wordsUtil = new WordsUtil();

        FileConfiguration config = Main.getPlugin().getConfig();
        List<String> badWords = config.getStringList("words");

        if (event.getInventory().getHolder() instanceof WordsMenu) {
            if (event.getClickedInventory() == event.getInventory()) {
                if (clickedItem == null) return;
                String itemName = LegacyComponentSerializer.legacySection().serialize(clickedItem.getItemMeta().itemName());

                if (clickedItem.getType() == Material.PAPER && event.getClick().isRightClick()) {
                    ConfirmMenu confirm = new ConfirmMenu(new WordsMenu(page, (searchInput != null) ? searchInput : null).getInventory(), () -> {
                        Component feedbackMessage = Component.text()
                                .append(Component.text("Removed ").color(NamedTextColor.GRAY))
                                .append(Component.text(itemName).color(NamedTextColor.RED))
                                .append(Component.text(" from the blacklist!").color(NamedTextColor.GRAY))
                                .build();

                        wordsUtil.removeWord(itemName);

                        player.openInventory(new WordsMenu(page, (searchInput != null) ? searchInput : null).getInventory());
                        player.sendMessage(feedbackMessage);
                    });
                    Bukkit.getServer().getPluginManager().registerEvents(confirm, Main.getPlugin());

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    player.openInventory(confirm.getInventory());
                }

                if (clickedItem.getType() == Material.BARRIER && event.getClick().isMouseClick()) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    player.closeInventory();
                }

                if (clickedItem.getType() == Material.ARROW && event.getClick().isMouseClick()) {
                    if (itemName.contains("Next") && page < maxPages) {
                        int targetedPage = event.isShiftClick() ? maxPages : (page+=1);
                        WordsMenu menu = new WordsMenu(targetedPage, (searchInput != null) ? searchInput : null);

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        player.openInventory(menu.getInventory());
                    }

                    if (itemName.contains("Previous") && page > 1) {
                        int targetedPage = event.isShiftClick() ? 1 : (page-=1);
                        WordsMenu menu = new WordsMenu(targetedPage, (searchInput != null) ? searchInput : null);

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        player.openInventory(menu.getInventory());
                    }
                }

                if (clickedItem.getType() == Material.COMPASS) {
                    if (event.getClick().isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                        Bukkit.getServer().getPluginManager().registerEvents(new TypingEvent(new WordsMenu(page, (searchInput != null) ? searchInput : null).getInventory(), (e) -> {
                            Component feedbackMessage = Component.text("§7Searching for §a\"" + e.getMessage() + "\"");

                            e.getPlayer().sendMessage(feedbackMessage);

                            WordsMenu menu = new WordsMenu(1, e.getMessage());
                            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                                e.getPlayer().openInventory(menu.getInventory());
                            });
                        }), Main.getPlugin());
                        player.closeInventory();

                        player.sendMessage(Component.text("\n§aWrite something in the chat\n§7type §e\"cancel\" §7to exit.\n"));
                    } else if (event.getClick().isRightClick() && searchInput != null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                        player.openInventory(new WordsMenu(1, null).getInventory());
                    }
                }

                if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE && event.getClick().isMouseClick()) {
                    Bukkit.getServer().getPluginManager().registerEvents(new TypingEvent(new WordsMenu(page, (searchInput != null) ? searchInput : null).getInventory(), (e) -> {
                        String[] args = e.getMessage().split("\\s+");

                        Set<String> words = new HashSet<>(Arrays.asList(args).subList(0, args.length));
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

                            e.getPlayer().sendMessage(feedbackMsg);
                        }

                        if (repeatError) {
                            e.getPlayer().sendMessage(Component.text("Some words you gave already exist in the blacklist.").color(NamedTextColor.RED));
                        }
                    }), Main.getPlugin());
                    player.closeInventory();

                    player.sendMessage(Component.text("\n§aWrite something in the chat\n§7type §e\"cancel\" §7to exit.\n"));
                }

                event.setCancelled(true);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
