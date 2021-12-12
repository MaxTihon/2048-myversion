package maxtigon.game2048.model;

import java.util.*;
import java.util.stream.Collectors;


public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    public int score;
    public int maxTile;
    private boolean isSaveNeeded = true;

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        resetGameTiles();
    }

    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }

        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles() {
        return Arrays.stream(gameTiles).flatMap(Arrays::stream).filter(Tile::isEmpty).collect(Collectors.toList());
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();

        if (emptyTiles.isEmpty()) {
            return;
        }

        int randomEmptyTileIndex = (int) (Math.random() * emptyTiles.size());
        emptyTiles.get(randomEmptyTileIndex).value = Math.random() < 0.9 ? 2 : 4;
    }

    public boolean canMove() {
        if (getEmptyTiles().size() != 0) {
            return true;
        }

        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                Tile t = gameTiles[x][y];
                if ((x < FIELD_WIDTH - 1 && t.value == gameTiles[x + 1][y].value)
                        || ((y < FIELD_WIDTH - 1) && t.value == gameTiles[x][y + 1].value)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean isModified = false;

        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value == 0) {
                for (int j = i + 1; j < tiles.length; j++) {
                    if (tiles[j].value != 0) {
                        int tmp = tiles[j].value;
                        tiles[j].value = tiles[i].value;
                        tiles[i].value = tmp;
                        isModified = true;
                        break;
                    }
                }
            }
        }

        return isModified;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isModified = false;

        for (int i = 0; i < tiles.length - 1; i++) {
            int firstValue = tiles[i].value;
            int secondValue = tiles[i + 1].value;

            if (firstValue != 0 && firstValue == secondValue) {
                tiles[i].value = firstValue + secondValue;
                tiles[i + 1].value = 0;
                isModified = true;

                score += tiles[i].value;

                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
            }
        }

        compressTiles(tiles);

        return isModified;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }

        boolean isModified = false;

        for (Tile[] tiles : gameTiles) {
            if (compressTiles(tiles) | mergeTiles(tiles)) {
                isModified = true;
                isSaveNeeded = true;
            }
        }

        if (isModified) {
            addTile();
        }
    }

    public void right() {
        saveState(gameTiles);

        invert();
        swapLines();
        invert();
        left();
        invert();
        swapLines();
        invert();
    }

    public void up() {
        saveState(gameTiles);

        invert();
        swapLines();
        invert();
        swapLines();
        invert();
        left();
        invert();
        swapLines();
        invert();
        swapLines();
        invert();
    }

    public void down() {
        saveState(gameTiles);

        invert();
        left();
        invert();
    }

    private void invert() {
        int maxIndex = gameTiles.length - 1;
        int secondLength = gameTiles.length;

        for (int i = 0; i < gameTiles.length; i++) {
            int i2 = maxIndex;
            int j2 = maxIndex - i;
            for (int j = 0; j < secondLength; j++) {
                Tile tmp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[i2][j2];
                gameTiles[i2][j2] = tmp;

                i2--;
            }
            secondLength--;
        }
    }

    private void swapLines() {
        Tile[] tmp = gameTiles[0];
        gameTiles[0] = gameTiles[3];
        gameTiles[3] = tmp;

        Tile[] tmp2 = gameTiles[1];
        gameTiles[1] = gameTiles[2];
        gameTiles[2] = tmp2;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }

        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int keyCode = ((int) (Math.random() * 100)) % 4;
        switch (keyCode) {
            case (0) -> left();
            case (1) -> right();
            case (2) -> up();
            case (3) -> down();
        }
    }

    public boolean hasBoardChanged() {
        Tile[][] savedGameTiles = previousStates.peek();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != savedGameTiles[i][j].value) {
                    return true;
                }
            }
        }

        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveEfficiency;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());

        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));

        if(!priorityQueue.isEmpty()) {
            priorityQueue.peek().getMove().move();
        }
    }
}
