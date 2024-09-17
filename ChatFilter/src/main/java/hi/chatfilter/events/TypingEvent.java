package hi.chatfilter.events;

import hi.chatfilter.Main;
import hi.chatfilter.models.TypeAction;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

public class TypingEvent implements Listener {
    private Inventory previousInventory;
    private static Consumer<TypeAction> action;

    public TypingEvent(Inventory previousInventory, Consumer<TypeAction> action) {
        if (previousInventory != null)
            this.previousInventory = previousInventory;
        if (action != null)
            TypingEvent.action = action;
    }

    @EventHandler
    public void onSpeak(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component messageComponent = event.message();

        String message = LegacyComponentSerializer.legacySection().serialize(messageComponent);
        event.setCancelled(true);

        if (!message.equalsIgnoreCase("cancel")) {
            AsyncChatEvent.getHandlerList().unregister(this); //Stop Listening

            action.accept(new TypeAction(message, player));

            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
        } else {
            AsyncChatEvent.getHandlerList().unregister(this); //Stop Listening

            if (action != null)
                Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                    player.openInventory(previousInventory);
                });

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
        }
    }
}
