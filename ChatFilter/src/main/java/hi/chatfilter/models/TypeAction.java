package hi.chatfilter.models;

import org.bukkit.entity.Player;

public class TypeAction {
    private final String message;
    private final Player player;

    public TypeAction(String message, Player player) {
        this.message = message;
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public Player getPlayer() {
        return player;
    }
}
