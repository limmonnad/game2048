package game;

import java.awt.event.KeyAdapter;

public class Controller extends KeyAdapter {
//    класс следит за нажатием клавиш во время игры
//    будет в основном использоваться для обработки пользовательского ввода с клавиатуры,
//поэтому сделаем его наследником класса KeyAdapter.

    private Model model;

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore(){
        return model.score;
    }

}
