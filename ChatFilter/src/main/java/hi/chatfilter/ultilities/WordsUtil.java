package hi.chatfilter.ultilities;

import hi.chatfilter.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class WordsUtil {
    private final FileConfiguration config = Main.getPlugin().getConfig();
    private final List<String> wordsList = config.getStringList("words");

    // CRUD: Create, Read, Update, Delete

    public String addWord(String word) {
        String lowercasedWord = word.toLowerCase();
        wordsList.add(lowercasedWord);
        config.set("words", wordsList);

        saveList();

        return word;
    }

    public void removeWord(String word) {
        wordsList.remove(word);
        config.set("words", wordsList);

        saveList();
    }

    private void saveList() {
        Main.getPlugin().saveConfig();
    }
}
