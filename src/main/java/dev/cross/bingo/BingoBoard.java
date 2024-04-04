package dev.cross.bingo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BingoBoard {

    private final BingoDigit[][] matrix;
    public BingoBoard(List<BingoDigit> digits) {
        matrix = new BingoDigit[5][5];
        for (int i = 0; i < digits.size(); i++) {
            matrix[i % 5][i / 5] = digits.get(i);
        }
    }

    private enum Direction {
        NORTH(0, 1), SOUTH(0, -1), EAST(1, 0), WEST(-1, 0), NW(-1, 1), NE(1, 1), SW(-1, -1), SE(1, -1);

        public final int x;
        public final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public List<BingoDigit> getDigits() {
        List<BingoDigit> digits = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                digits.add(matrix[j][i]);
            }
        }
        return digits;
    }

    public boolean isValid() {
        Optional<Bingo.State> state = Bingo.getState();

        if (state.isEmpty()) return false;

        List<BingoDigit> digits = getDigits();
        for (BingoDigit digit : digits) {
            if (digit.isSelected() && !digit.isMiddle()) {
                boolean b = state.get().isValid(digit.getBingoDigit());
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    // This is where the fun begins;
    public boolean isBingo() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                BingoDigit current = matrix[j][i];
                if (!current.isSelected()) continue;

                for (Direction d : Direction.values()) {
                    int x = j + d.x;
                    int y = i + d.y;
                    if (x >= matrix.length || x< 0) continue;
                    if (y >= matrix[x].length || y < 0) continue;
                    BingoDigit adj = matrix[x][y];
                    if (!adj.isSelected()) continue;
                    boolean b = bingoHelper(x, y, d, 1);
                    if (b) return true;
                }
            }
        }
        return false;
    }

    private boolean bingoHelper(int x, int y, Direction direction, int count) {
        if (x < 0 || x >= matrix.length) return false;
        if (y < 0 || y >= matrix[x].length) return false;

        BingoDigit digit = matrix[x][y];
        if (!digit.isSelected()) return false;
        if (count == 4) return true;
        return bingoHelper(x + direction.x, y + direction.y, direction, count + 1);
    }
}
