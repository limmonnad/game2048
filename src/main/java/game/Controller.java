package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
//    класс следит за нажатием клавиш во время игры
//    будет в основном использоваться для обработки пользовательского ввода с клавиатуры,
//поэтому сделаем его наследником класса KeyAdapter.

    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048;//вес плитки при достижении которого игра будет считаться выигранной.


    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public void resetGame() {
//        метод resetGame позволит вернуть игровое поле в начальное состояние
        model.score = 0;
        view.isGameWon = false;
        view.isGameLost = false;
        model.resetGameTiles();
    }

    public View getView() {
        return view;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
//        Для того чтобы иметь возможность обрабатывать пользовательский ввод, необходимо
//переопределить метод keyPressed с одним параметром типа KeyEvent
        int keyCode = keyEvent.getKeyCode();
        if (!model.canMove()) {
            view.isGameLost = true;
        }
        if (!(view.isGameLost && view.isGameWon)) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    model.left();
                    break;
                case KeyEvent.VK_RIGHT:
                    model.right();
                    break;
                case KeyEvent.VK_UP:
                    model.up();
                    break;
                case KeyEvent.VK_DOWN:
                    model.down();
                    break;
                case KeyEvent.VK_ESCAPE:
                    resetGame();
                    break;
                case KeyEvent.VK_Z:
                    model.rollback();
                    break;
                case KeyEvent.VK_R:
                    model.randomMove();
                    break;
            }
        }
        if (model.maxTile == WINNING_TILE) {
            view.isGameWon = true;
        }
        view.repaint();


    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.score;
    }

}

//            switch (keyCode) {
//                case KeyEvent.VK_ESCAPE:
//                    resetGame();
//                    break;
//            }
