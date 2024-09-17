package hi.chatfilter.menus;

import hi.chatfilter.Main;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfirmMenu implements InventoryHolder, Listener {
    private final Inventory inventory;
    private final Inventory pastInventory;
    public Runnable action;

    public ConfirmMenu(Inventory pastInventory, Runnable action) {
        this.inventory = Bukkit.createInventory(this, 27, "Are you sure?");
        this.pastInventory = pastInventory;
        this.action = action;

        ItemStack confirm = new ItemStack(Material.GREEN_TERRACOTTA);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.itemName(Component.text("Confirm").color(NamedTextColor.GREEN));
        confirm.setItemMeta(confirmMeta);
        inventory.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta cancelMeta = confirm.getItemMeta();
        cancelMeta.itemName(Component.text("Cancel").color(NamedTextColor.RED));
        cancel.setItemMeta(cancelMeta);
        inventory.setItem(15, cancel);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) continue;

            ItemStack blank = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta blankMeta = confirm.getItemMeta();
            blankMeta.itemName(Component.text(""));
            blankMeta.setHideTooltip(true);
            blank.setItemMeta(blankMeta);

            inventory.setItem(i, blank);
        }
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getInventory().getHolder() instanceof ConfirmMenu) {
            if (event.getClickedInventory() == event.getInventory()) {
                if (pastInventory == null) return;

                if (clickedItem != null && clickedItem.getType() == Material.GREEN_TERRACOTTA) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                    player.openInventory(pastInventory);
                    action.run();

                    InventoryClickEvent.getHandlerList().unregister(this);

                } else if (clickedItem != null && clickedItem.getType() == Material.RED_TERRACOTTA) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    player.openInventory(pastInventory);

                    InventoryClickEvent.getHandlerList().unregister(this);
                }

                event.setCancelled(true);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExit(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (closedInventory.getHolder() instanceof ConfirmMenu) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);

            InventoryCloseEvent.getHandlerList().unregister(this);
            InventoryClickEvent.getHandlerList().unregister(this);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
