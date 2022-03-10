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
public class Player {

    protected char name;

    public Player(char name) {
        this.name = name;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public boolean makeMove(int row, int col, Board board) {
        if (board.isEmptyAt(row, col)) {
            board.setValAt(row, col, this.getName());
//            System.out.println("Player made move: " + row + ", " + col);
            return true;
        }
//        System.out.println("hehe");
        return false;
    }
    
    public int[] randomChoice(Board board){
        return new int[2];
    }
    
    public boolean isBot() {
        if (this instanceof Bot) {
            return true;
        }
        return false;
    }
}
