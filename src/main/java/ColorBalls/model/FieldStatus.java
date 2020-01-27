package ColorBalls.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public enum FieldStatus {

    EMPTY(".", "view/empty.jpg"),
    YELLOW("!", "view/yellow.jpg"),
    ORANGE("@", "view/orange.jpg"),
    RED("#", "view/red.jpg"),
    BLUE("%", "view/blue.jpg"),
    GREEN("&", "view/green.jpg"),
    PURPLE("*", "view/purple.jpg");

    private static Random random = new Random();
    private String symbol;
    private String path;

    FieldStatus(String symbol, String path) {
        this.symbol = symbol;
        this.path = path;

    }

    public static FieldStatus getRandomSymbol() {
        return values()[random.nextInt(values().length - 1) + 1];
    }

    public String getSymbol() {
        return symbol;
    }

    public String getImagePath() {
        return path;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
