package maxtigon.game2048.model;

import java.awt.*;

public class Tile {
    public int value;

    public Tile(int value) {
        this.value = value;
    }

    public Tile() {
        this.value = 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Color getFontColor() {
        return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
    }

    public Color getTileColor() {
        return switch (value) {
            case (0) -> new Color(0xcdc1b4);
            case (2) -> new Color(0xeee4da);
            case (4) -> new Color(0xede0c8);
            case (8) -> new Color(0xf2b179);
            case (16) -> new Color(0xf59563);
            case (32) -> new Color(0xf67c5f);
            case (64) -> new Color(0xf65e3b);
            case (128) -> new Color(0xedcf72);
            case (256) -> new Color(0xedcc61);
            case (512) -> new Color(0xedc850);
            case (1024) -> new Color(0xedc53f);
            case (2048) -> new Color(0xedc22e);
            default -> new Color(0xff0000);
        };
    }
}
