package ColorBalls.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

    private FieldStatus[][] board;
    private List<Coordinances> matchedCoordinates;
    private Random random = new Random();

    public Board() {
        matchedCoordinates = new ArrayList<>();
        board = new FieldStatus[10][10];
        fillEmptyBoard();
    }

    private void fillEmptyBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = FieldStatus.EMPTY;
            }
        }
    }

    public FieldStatus getStatus(Coordinances coordinances) {
        return board[coordinances.getY()][coordinances.getX()];
    }

    public FieldStatus getStatus(int x, int y) {
        return board[y][x];
    }

    public void putMark(FieldStatus status, Coordinances coordinances) {
        board[coordinances.getY()][coordinances.getX()] = status;
    }

    public void putMarkInRandomPlace(FieldStatus status) {
        if (AmountOfEmptyFields() > 0) {
            int x;
            int y;
            do {
                x = random.nextInt(board.length);
                y = random.nextInt(board.length);
            } while (board[y][x] != FieldStatus.EMPTY);
            board[y][x] = status;
        }
    }

    public void putMarksInRandomPlace(List<FieldStatus> fieldStatus) {
        for (int i =0;i<fieldStatus.size();i++) {
            putMarkInRandomPlace(fieldStatus.get(i));
        }
    }


    public void removeMark(Coordinances coordinances) {
        board[coordinances.getY()][coordinances.getX()] = FieldStatus.EMPTY;
    }

    public boolean checkIfInRowAndGetCompletMarks() {
        //vertical - i - column
        matchedCoordinates.clear();
        return checkVertical() || checkHorizontal();
    }

    private boolean checkHorizontal() {
        //horizontal  i - row
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < 6; j++) {
                if (getStatus(i, j) != FieldStatus.EMPTY &&
                        getStatus(i, j) == getStatus(i, j + 1) &&
                        getStatus(i, j + 1) == getStatus(i, j + 2) &&
                        getStatus(i, j + 2) == getStatus(i, j + 3) &&
                        getStatus(i, j + 3) == getStatus(i, j + 4)) {
                    for (int m = j; m <= j + 4; m++) {
                        matchedCoordinates.add(new Coordinances(i,m));
                    }
                    for (int mn = 1; mn <= 5; mn++) {
                        if (j + 4 + mn < board.length && getStatus(i, j) == getStatus(i, j + 4 + mn))
                            matchedCoordinates.add(new Coordinances(i,j + 4 + mn));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkVertical() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < 6; j++) {
                if (getStatus(j, i) != FieldStatus.EMPTY &&
                        getStatus(j, i) == getStatus(j + 1, i) &&
                        getStatus(j + 1, i) == getStatus(j + 2, i) &&
                        getStatus(j + 2, i) == getStatus(j + 3, i) &&
                        getStatus(j + 3, i) == getStatus(j + 4, i)) {
                    for (int m = j; m <= j + 4; m++) {
                        matchedCoordinates.add(new Coordinances(m,i));
                    }
                    for (int mn = 1; mn <= 4; mn++) {
                        if (j + 4 + mn < board.length && getStatus(j, i) == getStatus(j + 4 + mn, i))
                            matchedCoordinates.add(new Coordinances(j + 4 + mn,i));
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public List<FieldStatus> getNextThreeMarks() {
        Random random = new Random();
        List<FieldStatus> nextMarks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            nextMarks.add(FieldStatus.getRandomSymbol());
        }
        return nextMarks;
    }


    public List<Coordinances> getMatchedCoordinates() {
        return matchedCoordinates;
    }

    public int AmountOfEmptyFields() {
        int counter = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == FieldStatus.EMPTY)
                    counter++;
            }
        }
        return counter;
    }


    public void showBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getBoardSize() {
        return board.length;
    }
}
