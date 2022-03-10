/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Board;
import model.Bot;
import model.Game;
import model.Player;
import view.GameView;

/**
 *
 * @author typpo
 */
public class Controller {

    private volatile Game game;
    private volatile GameView gameView;

    public Controller(Game game, GameView gameView) {
        this.game = game;
        this.gameView = gameView;

        gameView.addStartGameListener(new StartGameListener());
        gameView.addMakeMoveListener(new MakeMoveListener());
    }

    public class MakeMoveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < gameView.getBoardSize(); i++) {
                for (int j = 0; j < gameView.getBoardSize(); j++) {
                    if (e.getSource() == gameView.getButtonGroup()[i][j]) {
                        if (game.getBoard().isEmptyAt(i, j)) {
//                            System.out.println("Current player =" + game.getCurrentPlayer().getName());
                            gameView.setMove(i, j, game.getCurrentPlayer().getName());
                            game.getCurrentPlayer().makeMove(i, j, game.getBoard());
//                            game.getBoard().printBoard();

                            if (game.checkWin(game.getCurrentPlayer().getName()) == true) {
                                System.out.println(game.getCurrentPlayer().getName() + " won");
//                                System.out.println(game.getP1().getName());

                                if (game.getCurrentPlayer().getName() == game.getP1().getName()) {
                                    gameView.setCurrentStatus("p1 won!");
                                } else {
                                    gameView.setCurrentStatus("p2 won!");
                                }

                                game.setGameOver(true);
                                gameView.lockEndGame();
//                                System.exit(0);
                                break;
                            } else {
                                if (game.isP1Turn()) {
                                    game.setP1Turn(false);
                                } else {
                                    game.setP1Turn(true);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    class StartGameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    Bot bot1 = null;
                    if (game.getP1().isBot()) {
                        //thinking
                        bot1 = (Bot) game.getP1();
                        bot1.setOpponent(game.getP2().getName());
                    }
                    Bot bot2 = null;
                    if (game.getP2().isBot()) {
                        bot2 = (Bot) game.getP2();
                        bot2.setOpponent(game.getP1().getName());
                    }
                    while (!game.isOver()) {

                        //p1 make move, p2 wait
                        gameView.setCurrentStatus("p1 is making a move...");
                        while (game.isP1Turn()) {
                            if (game.getP1().isBot()) {
                                //thinking
                                int choice[];
                                if(game.isEmpty()){
                                    choice = bot1.randomChoice(game.getBoard());
                                }
                                else{
                                    choice = bot1.bestChoice(game.getBoard());
                                }
                                gameView.getButtonGroup()[choice[0]][choice[1]].doClick();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }

                        //p2 make move, p1 wait
                        if (!game.isOver()) {
                            gameView.setCurrentStatus("p2 is making a move...");
                            while (!game.isP1Turn()) {
                                if (game.getP2().isBot()) {
                                    int choice[] = bot2.bestChoice(game.getBoard());
                                    gameView.getButtonGroup()[choice[0]][choice[1]].doClick();
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    }
                }
            });
            thread.start();
            if(game.isOver()) {
                thread.interrupt();
                thread.stop();
            }
            gameView.lockStartGame();
        }
    }
}
