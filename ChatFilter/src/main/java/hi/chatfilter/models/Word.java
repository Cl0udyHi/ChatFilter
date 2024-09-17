package hi.chatfilter.models;

public class Word {
    private String word;
    private boolean activated;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Word(String word) {
        this.word = word;
    }
}
