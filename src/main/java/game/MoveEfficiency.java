package game;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
//    описывающий эффективность хода

    private int numberOfEmptyTiles; //количество пустых плиток
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
//первым делом сравним количество пустых плиток (numberOfEmptyTiles), потом счет (score),
//если количество пустых плиток равное. Если и счет окажется равным, будем считать эффективность ходов равной и вернем ноль.
        if (this.numberOfEmptyTiles == o.numberOfEmptyTiles) {
            return Integer.compare(this.score, o.score);
        }
        return Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
    }
}
