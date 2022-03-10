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
public class Board {

    private final int size = 20;
    private volatile char val[][];
    private final int winCondition = 5;
    
    public Board() {
        val = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                val[i][j] = '.';
            }
        }
    }

    public int getSize() {
        return size;
    }

    public char[][] getVal() {
        return val;
    }

    public void setVal(char[][] val) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.setValAt(i,j,val[i][j]);
            }
        }
    }
    
    public void printBoard(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(val[i][j]+" ");
            }
            System.out.println();
        }
    }

    public boolean isEmpty(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(!isEmptyAt(i,j)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isEmptyAt(int row, int col) {
        if (val[row][col] == '.') {
            return true;
        }
        return false;
    }

    public char getValAt(int row, int col) {
        return val[row][col];
    }

    public void setValAt(int row, int col, char val) {
        this.val[row][col] = val;
    }

    public boolean checkWin(char c) {
        int dem1 = 0; //kiem tra gia tri lien tiep

        //ngang
        for (int i = 0; i < this.getSize(); i++) {
            dem1 = 0;
            for (int j = 0; j < this.getSize(); j++) {
                if (this.getValAt(i, j) == c) {
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
        for (int j = 0; j < this.getSize(); j++) {
            dem1 = 0;
            for (int i = 0; i < this.getSize(); i++) {
                if (this.getValAt(i, j) == c) {
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
        //cheo L - R: i+j = Const voi moi duong cheo (0 <= Const <= 2*n -2) => sum = i + j
        int sum = 0;
        int start = 0;
        int row = 0, col = 0;

        while (sum <= 2 * this.getSize() - 2) {
            row = start;
            col = sum - start;
            dem1 = 0;
            while (row >= 0 && col < this.getSize()) {
                if (this.getValAt(row, col) == c) {
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
            if (start != this.getSize() - 1) {
                start++;
            }
            sum++;
        }

        dem1 = 0;
        //cheo R - L: i - j = Const voi moi duong cheo (1-n <= const <= n-1) => sub = i - j
        int sub = this.getSize() - 1; //tu goc trai duoi len
        start = this.getSize() - 1;

        while (sub >= 1 - this.getSize()) {
//            System.out.println("hehe");
            row = start;
            col = row - sub;
            dem1 = 0;
            if (col > row) {
                col = row - (col - row);
            }
            while (row >= 0 && col >= 0) {
//                System.out.println("hehe");
                if (this.getValAt(row, col) == c) {
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

    public boolean moreMoves(){
        for(int i = 0; i < this.size; i++){
            for(int j = 0; j < this.size; j++){
                if(this.isEmptyAt(i, j)){
                    return true;
                }
            }
        }
        return false;
    }

}
