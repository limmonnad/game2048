package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Model {
//    класс содержит игровую логику и хранит игровое поле.
//    Он будет ответственен за все манипуляции производимые с игровым полем

    // реализуем возможность отменить последний ход

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    int score; //текущий счет
    int maxTile;//максимальный вес плитки на игровом поле
    private Model model;
    private View view;
    private Stack<Tile[][]> previousStates = new Stack();//предыдущие состояния игрового поля
    private Stack<Integer> previousScores = new Stack();//предыдущие счета
    private boolean isSaveNeeded = true;


    public boolean hasBoardChanged() {
//будет возвращать true, в случае, если вес плиток в массиве gameTiles отличается
//от веса плиток в верхнем массиве стека previousStates.
        int allTile = 0;
        int allTile1 = 0;

        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                allTile += gameTiles[i][j].value;
            }
        }

        Tile[][] peek = previousStates.peek();
        for (int i = 0; i < peek.length; i++) {
            for (int j = 0; j < peek.length; j++) {
                allTile1 += peek[i][j].value;
            }
        }
        if (allTile != allTile1) {
            return true;
        }
        return false;

    }


    public MoveEfficiency getMoveEfficiency(Move move) {
//        возвращает объект типа MoveEfficiency описывающий эффективность переданного хода
        move.move();
        MoveEfficiency moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        if(!hasBoardChanged()){
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }
        rollback();
        return moveEfficiency;


    }


    private void saveState(Tile[][] tiles) {
//будет сохранять текущее игровое состояние и счет в стеки с помощью метода push и
//устанавливать флаг isSaveNeeded равным false.
        Tile[][] tiles1 = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < tiles1.length; i++) {
            for (int j = 0; j < tiles1.length; j++) {
                tiles1[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(tiles1);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void randomMove() {
//        метод randomMove будет вызывать один из методов движения случайным образом
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }


    public void rollback() {
//устанавливает текущее игровое состояние равным последнему находящемуся в стеках с помощью метода pop.
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        if (getEmptyTiles().size() > 0) {
            return true;
        }
        for (int i = 0; i < gameTiles.length; i++) {

            for (int j = 0; j < gameTiles.length; j++) {
                if (j < 3 && gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                    return true;
                }

                if (i < 3 && gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                    return true;
                }
            }

        }
        return false;

    }

    public int getScore() {
        return score;
    }


    public Model() {
        //конструктор
        resetGameTiles();

    }

    public void resetGameTiles() {
//        вызывается при создании игры
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        this.maxTile = 0;
        this.score = 0;
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
//Сжатие плиток, таким образом, чтобы все пустые плитки были справа, т.е. ряд {4, 2, 0, 4} становится рядом {4, 2, 4, 0}
//        метод   возвращет true если он вносил изменения во входящий массив, иначе - false

        boolean x = false;
        int insertPosition = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].value > 0) {
                if (i != insertPosition) {
                    tiles[insertPosition].value = tiles[i].value;
                    tiles[i].value = 0;
                    x = true;
                }
                insertPosition++;
            }
        }
        return x;
    }

    private boolean mergeTiles(Tile[] tiles) {
//Слияние плиток одного номинала, т.е. ряд {4, 4, 2, 0} становится рядом {8, 2, 0, 0}.
// ряд {4, 4, 4, 4} превратится в {8, 8, 0, 0}, а {4, 4, 4, 0} в {8, 4, 0, 0}.
        boolean x = false;
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            if (tiles[i].isEmpty()) {
                continue;
            }
            if (tiles[i].value > 0 && tiles[i].value == tiles[i + 1].value) {
                tiles[i].value *= 2;
                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
                score += tiles[i].value;
                tiles[i + 1].value = 0;
                x = true;
            }
        }
        compressTiles(tiles);
        return x;
    }

    private Tile[][] rotateClockwise(Tile[][] tiles) {
//        поворачивает массив на 90
        Tile[][] resultArray = new Tile[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                resultArray[j][tiles.length - i - 1] = tiles[i][j];
            }
        }
        return resultArray;
    }


    public void left() {
//        вызывает для каждой строки массива gameTiles методы compressTiles и mergeTiles
//        и добавлять одну плитку с помощью метода addTile в том случае, если это необходимо.

        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }

        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;
    }

    public void up() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);

    }

    public void right() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);

    }

    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);

    }


    private void addTile() {
//изменяет значение случайной пустой плитки в массиве gameTiles
//на 2 или 4 с вероятностью 0.9 и 0.1 соответственно.
        List<Tile> emptyTiles = getEmptyTiles();
        if (emptyTiles.isEmpty()) {
            return;
        }
        int v = (int) (Math.random() * emptyTiles.size());//случайный объект из списка
        Tile tile = emptyTiles.get(v);
        tile.value = (Math.random() < 0.9 ? 2 : 4);

    }


    private List<Tile> getEmptyTiles() {
//      метод  должен возвращать список пустых плиток в массиве gameTiles
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    list.add(gameTiles[i][j]);
                }
            }
        }
        return list;
    }


}
