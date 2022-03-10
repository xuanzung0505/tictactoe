package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author typpo
 */
public class Game {

    private int gameMode;
    private volatile Player p1, p2;
    private volatile Board board;
    private volatile boolean gameOver;
    private volatile boolean p1Turn;
    private final int winCondition = 5;

    public Game(Player p1, Player p2, Board board, int gameMode) {
        this.p1 = p1;
        this.p2 = p2;
        this.board = board;
        this.gameMode = gameMode;
        this.gameOver = false;
        p1Turn = true;
    }

    public boolean isP1Turn() {
        return p1Turn;
    }

    public void setP1Turn(boolean p1Turn) {
        this.p1Turn = p1Turn;
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    public Player getCurrentPlayer() {
        if (p1Turn) {
            return p1;
        }
        return p2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean checkWin(char c) {
        board.printBoard();
        System.out.println();
        int dem1 = 0; //kiem tra gia tri lien tiep

        //ngang
        for (int i = 0; i < board.getSize(); i++) {
            dem1 = 0;
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getValAt(i, j) == c) {
                    dem1++;
//                    System.out.println("dem1 ngang " + getCurrentPlayer().getName() + " = " + dem1);
                    if (dem1 == winCondition) {
                        return true;
                    }
                } else {
                    dem1 = 0;
                }
            }
        }

        dem1 = 0;
        //doc
        for (int j = 0; j < board.getSize(); j++) {
            dem1 = 0;
            for (int i = 0; i < board.getSize(); i++) {
                if (board.getValAt(i, j) == c) {
                    dem1++;
//                    System.out.println("dem1 doc " + getCurrentPlayer().getName() + " = " + dem1);
                    if (dem1 == winCondition) {
                        return true;
                    }
                } else {
                    dem1 = 0;
                }
            }
        }

        dem1 = 0;
        //cheo L - R (/): i+j = Const voi moi duong cheo (0 <= Const <= 2*n -2) => sum = i + j
        int sum = 0;
        int start = 0;
        int row = 0, col = 0;

        while (sum <= 2 * board.getSize() - 2) {//duyet tat ca duong cheo
            row = start;
            col = sum - start;
            dem1 = 0;
            while (row >= 0 && col < board.getSize()) {
                if (board.getValAt(row, col) == c) {
                    dem1++;
//                    System.out.println("dem1 cheo sang trai " + getCurrentPlayer().getName() + " = " + dem1);
                    if (dem1 == winCondition) {
                        return true;
                    }
                } else {
                    dem1 = 0;
//                    System.out.println("dem1 cheo sang trai " + getCurrentPlayer().getName() + " = " + dem1);
                }
                row--;
                col++;
            }
            if (start != board.getSize() - 1) {
                start++;
            }
            sum++;
        }

        dem1 = 0;
        //cheo R - L (\): i - j = Const voi moi duong cheo (1-n <= const <= n-1) => sub = i - j
        int sub = board.getSize() - 1; //tu goc trai duoi len
        start = board.getSize() - 1;

        while (sub >= 1 - board.getSize()) {
//            System.out.println("hehe");
            row = start;
            col = row - sub;
            dem1 = 0;
            if (col > row) {
                col = row - (col - row);
            }
            while (row >= 0 && col >= 0) {
//                System.out.println("hehe");
                if (board.getValAt(row, col) == c) {
//                    System.out.println("hehe");
                    dem1++;
//                    System.out.println("dem1 cheo sang phai " + getCurrentPlayer().getName() + " = " + dem1);
                    if (dem1 == winCondition) {
                        return true;
                    }
                } else {
                    dem1 = 0;
//                    System.out.println("dem1 cheo sang phai " + getCurrentPlayer().getName() + " = " + dem1);
                }
                row--;
                col--;
            }
            sub--;
        }

        return false;
    }

    public boolean isEmpty(){
        return this.board.isEmpty();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isOver() {
        return gameOver;
    }
}
