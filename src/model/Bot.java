package model;

import java.util.ArrayList;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author typpo
 */
public class Bot extends Player {

//    private char name;
    private ArrayList<int[]> moves;
    private char opponent;
    private final int winScore = 100000000;
    private final int loseScore = -100000000;
    private static int index = 0;

    public Bot(char name) {
        super(name);
        moves = new ArrayList<>();
    }

    public char getOpponent() {
        return opponent;
    }

    public void setOpponent(char opponent) {
        this.opponent = opponent;
//        System.out.println("Opponent: "+ opponent);
    }

    @Override
    public boolean makeMove(int row, int col, Board board) {
        if (board.isEmptyAt(row, col)) {
            board.setValAt(row, col, this.getName());
//            moves.add(new int[]{row, col});
            return true;
        }
//        System.out.println("hehe");
        return false;
    }

    public boolean undoMove(int row, int col, Board board) {
        if (!board.isEmptyAt(row, col)) {
            board.setValAt(row, col, '.');
//            moves.add(new int[]{row, col});
            return true;
        }
//        System.out.println("hehe");
        return false;
    }

    public boolean makeMoveOpponent(int row, int col, Board board) {
        if (board.isEmptyAt(row, col)) {
            board.setValAt(row, col, this.opponent);
//            moves.add(new int[]{row, col});
//            System.out.println("Bot made move for player: " + row + ", " + col);
            return true;
        }
//        System.out.println("hehe");
        return false;
    }

    @Override
    public int[] randomChoice(Board board) {
        int row = 0, col = 0;
        Random rand = new Random();
        do {
            row = rand.nextInt(board.getSize()); //0 - n-1
            col = rand.nextInt(board.getSize());
        } while (!board.isEmptyAt(row, col)); //        System.out.println(row+" "+col);
//        System.out.println("new move: " + row + ", " + col);
        return new int[]{row, col};
    }

    public int[] bestChoice(Board board){
        int row = 0, col = 0;
        int[] move;
        double best = -100000000.0;
        if((move = searchWinMove(board))!=null){
            return move;
        }
        if((move = searchLoseMove(board))!=null){
            return move;
        }
        Node res = minimax(board, 3, -1, winScore, true);
        System.out.println("Best move(" + ++index +"): " + res.x + ", " + res.y);
        return new int[]{res.x, res.y};
    }

    private int[] searchWinMove(Board board){
        ArrayList<int[]> allPossibleMoves = generateMoves(board);
        for(int[] move : allPossibleMoves){
            Board dummyBoard = new Board();
            dummyBoard.setVal(board.getVal());
            makeMove(move[0], move[1], dummyBoard);
            if(dummyBoard.checkWin(this.name)){
                return move;
            }
        }
        return null;
    }

    private int[] searchLoseMove(Board board){
        ArrayList<int[]> allPossibleMoves = generateMoves(board);
        for(int[] move : allPossibleMoves){
            Board dummyBoard = new Board();
            dummyBoard.setVal(board.getVal());
            makeMoveOpponent(move[0], move[1], dummyBoard);
            if(dummyBoard.checkWin(this.opponent)){
                return move;
            }
        }
        return null;
    }

    public Node minimax(Board board, int depth, double alpha, double beta, boolean isMax) {
        double score = evaluateBoardForBot(board, !isMax); //danh gia neu dang la luot cua doi thu (cho TH nut la)
        ArrayList<int[]> allPossibleMoves = generateMoves(board);
        Board dummyBoard = new Board();
        dummyBoard.setVal(board.getVal());

        if (allPossibleMoves.size()==0) {
            return new Node(score);//điểm ở nút lá
        }

        if (depth == 0) {
            return new Node(score);//điểm ở nút lá
        }

        Node node = new Node();

        if (isMax) {
            double maxScore = -100000000.0;//max khởi tạo
            Node maxNode = new Node(maxScore);//node ban đầu
            for(int move[] : allPossibleMoves){//tất cả nước đi
                int i = move[0], j = move[1];
                makeMove(i, j, dummyBoard);//đi thử nước hiện tại
                Node eval = minimax(dummyBoard, depth - 1, alpha, beta, !isMax);//xét điểm của nước đi vừa xong và gán điểm cho nút
                eval.x = i;//gán nước đi vừa thử cho nút eval
                eval.y = j;
                maxNode = Node.max(maxNode, eval);//tìm nút có điểm lớn hơn
                undoMove(i, j, dummyBoard);//rút lại nước đi để đánh giá nước đi khác
                alpha = Math.max(alpha, eval.score);//cập nhật alpha
                if(beta<=alpha){
                    break;
                }
            }
            node = maxNode;
            return node;
        } else {
            double minScore = 100000000.0;
            Node minNode = new Node(minScore);
            for(int move[] : allPossibleMoves){
                int i = move[0], j = move[1];
                makeMoveOpponent(i, j, dummyBoard);
                Node eval = minimax(dummyBoard, depth - 1, alpha, beta, !isMax);
                eval.x = i;
                eval.y = j;
                minNode = Node.min(minNode, eval);
                undoMove(i, j, dummyBoard);
                beta = Math.min(beta, eval.score);
                if(beta<=alpha){
                    break;
                }
            }
            node = minNode;
            return node;
        }
    }
    //các hàm đánh giá
    public double evaluateBoardForBot(Board board, boolean userTurn) {

        double opponentScore = getScore(board, true, userTurn);//điểm của đối thủ theo lượt userTurn
        double botScore = getScore(board, false, userTurn);//điểm của bot theo lượt của userTurn

        if(opponentScore == 0) opponentScore = 1.0;

        return botScore / opponentScore;//0<score<1: đối thủ thắng thế; >1: bot đang thắng thế

    }

    public ArrayList<int[]> generateMoves(Board boardMatrix) {//tìm những ô trống xung quanh sát với vùng đã đánh
        ArrayList<int[]> moveList = new ArrayList<int[]>();

        int boardSize = boardMatrix.getSize();


        // Tìm những tất cả những ô trống nhưng có đánh XO liền kề
        for(int i=0; i<boardSize; i++) {
            for(int j=0; j<boardSize; j++) {

                if(boardMatrix.getValAt(i,j) != '.') continue;

                if(i > 0) {
                    if(j > 0) {
                        if(boardMatrix.getValAt(i-1,j-1) != '.' ||
                                boardMatrix.getValAt(i,j-1) != '.') {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < boardSize-1) {
                        if(boardMatrix.getValAt(i-1,j+1) != '.' ||
                                boardMatrix.getValAt(i,j+1) != '.') {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix.getValAt(i-1,j) != '.') {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }
                if( i < boardSize-1) {
                    if(j > 0) {
                        if(boardMatrix.getValAt(i+1,j-1) != '.' ||
                                boardMatrix.getValAt(i,j-1) != '.') {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < boardSize-1) {
                        if(boardMatrix.getValAt(i+1,j+1) != '.' ||
                                boardMatrix.getValAt(i,j+1) != '.') {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix.getValAt(i+1, j) != '.') {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }

            }
        }
        return moveList;
    }

    public int getScore(Board board, boolean forOpponent, boolean botTurn){
        char[][] boardMatrix = board.getVal();
        return evaluateHorizontal(boardMatrix, forOpponent, botTurn)
                + evaluateVertical(boardMatrix, forOpponent, botTurn) + evaluateDiagonal(boardMatrix,forOpponent,botTurn);
    }

    public int getConsecutiveSetScore(int count, int blocks, boolean currentTurn) {
        final int winGuarantee = 1000000;
        if(blocks == 2 && count <= 5) return 0;
        switch(count) {
            // Ăn 5 -> Cho điểm cao nhất
            case 5: {
                return winScore;
            }
            case 4: {
                // Đang 4 -> Tuỳ theo lượt và bị chặn: winGuarantee, winGuarantee/4, 200
                if(currentTurn) return winGuarantee;
                else {
                    if(blocks == 0) return winGuarantee/4;
                    else return 200;
                }
            }
            case 3: {
                // Đang 3: Block = 0
                if(blocks == 0) {
                    // Nếu lượt của currentTurn thì ăn 3 + 1 = 4 (không bị block) -> 50000 -> Khả năng thắng cao.
                    // Ngược lại không phải lượt của currentTurn thì khả năng bị blocks cao
                    if(currentTurn) return 50000;
                    else return 200;
                }
                else {
                    // Block == 1 hoặc Blocks == 2
                    if(currentTurn) return 10;
                    else return 5;
                }
            }
            case 2: {
                // Tương tự với 2
                if(blocks == 0) {
                    if(currentTurn) return 7;
                    else return 5;
                }
                else {
                    return 3;
                }
            }
            case 1: {
                return 1;
            }
        }
        return winScore*2;
    }

    public int evaluateHorizontal(char[][] boardMatrix, boolean forOpponent, boolean botTurn ) {
        int consecutive = 0;
        int blocks = 2;
        int score = 0;
//        System.out.println("Bot.evaluateHorizontal forOpponent: " + forOpponent + "; Bot: " + this.name + " User: " + this.opponent);
//        for(int i = 0; i < boardMatrix.length; i++){
//            for(int j = 0; j < boardMatrix[0].length; j++){
//                System.out.print(boardMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }

        for(int i=0; i<boardMatrix.length; i++) {
            for(int j=0; j<boardMatrix[0].length; j++) {
//                ......oxxxxxo
                if(boardMatrix[i][j] == (forOpponent ?  this.opponent:this.name)) {
                    //2. Đếm...
                    consecutive++;
//                    System.out.println(boardMatrix[i][j] +" ngang: "+ consecutive);
                }
                // gặp ô trống xxx.oooo..
                else if(boardMatrix[i][j] == '.') {
                    if(consecutive > 0) {
                        // Ra: Ô trống ở cuối sau khi đếm. Giảm block rồi bắt đầu tính điểm sau đó reset lại ban đầu
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                        consecutive = 0;
                        blocks = 1; //reset = 1 vì TH sau tính từ ô trống này
                    }
                    else {
                        // 1. Vào reset lại blocks = 1 rồi bắt đầu đếm
                        blocks = 1;
                    }
                }
                //gặp quân địch khi đang có chuỗi
                else if(consecutive > 0) {
                    // 2.Ra:  Ô bị chặn sau khi đếm. Tính điểm sau đó reset lại.
                    score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                    consecutive = 0;
                    blocks = 2; //reset = 2 vì tính tiếp cho quân địch
                }
                //gặp quân địch khi chưa có chuỗi
                else {
                    //1. Vào: reset lại blocks = 2 rồi bắt đầu đếm
                    blocks = 2;
                }
            }

            // 3. Ra: nhưng lúc này đang ở cuối. Nếu liên tục thì vẫn tính cho đến hết dòng
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);

            }
            // reset lại để tiếp tục chạy cho dòng tiếp theo
            consecutive = 0;
            blocks = 2;

        }
        return score;
    }
    // hàm tính toán đường dọc tương tự như đường ngan
    public int evaluateVertical(char[][] boardMatrix, boolean forOpponent, boolean botTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;
//        System.out.println("Bot.evaluateVertical forOpponent: " + forOpponent);
//        for(int i = 0; i < boardMatrix.length; i++){
//            for(int j = 0; j < boardMatrix[0].length; j++){
//                System.out.print(boardMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
        for(int j=0; j<boardMatrix[0].length; j++) {
            for(int i=0; i<boardMatrix.length; i++) {
                if(boardMatrix[i][j] == (forOpponent ?  this.opponent:this.name)) {
                    consecutive++;
//                    System.out.println(boardMatrix[i][j] +" doc: "+ consecutive);
                }
                else if(boardMatrix[i][j] == '.') {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }
            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);

            }
            consecutive = 0;
            blocks = 2;

        }
        return score;
    }
    // Hàm tính toán 2 đường chéo tương tự như hàng ngan
    public int evaluateDiagonal(char[][] boardMatrix, boolean forOpponent, boolean botTurn ) {

        int consecutive = 0;
        int blocks = 2;
        int score = 0;
//        System.out.println("Bot.evaluateDiagonal forOpponent: " + forOpponent);
//        for(int i = 0; i < boardMatrix.length; i++){
//            for(int j = 0; j < boardMatrix[0].length; j++){
//                System.out.print(boardMatrix[i][j] + " ");
//            }
//            System.out.println();
//        }
        // Đường chéo /
        for (int k = 0; k <= 2 * (boardMatrix.length - 1); k++) {
            int iStart = Math.max(0, k - boardMatrix.length + 1);
            int iEnd = Math.min(boardMatrix.length - 1, k);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = k - i;

                if(boardMatrix[i][j] == (forOpponent ?  this.opponent:this.name)) {
                    consecutive++;
//                    System.out.println(boardMatrix[i][j] +" cheo/ : "+ consecutive);
                }
                else if(boardMatrix[i][j] == '.') {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);

            }
            consecutive = 0;
            blocks = 2;
        }
        // Đường chéo \
        for (int k = 1-boardMatrix.length; k < boardMatrix.length; k++) {
            int iStart = Math.max(0, k);
            int iEnd = Math.min(boardMatrix.length + k - 1, boardMatrix.length-1);
            for (int i = iStart; i <= iEnd; ++i) {
                int j = i - k;

                if(boardMatrix[i][j] == (forOpponent ?  this.opponent:this.name)) {
                    consecutive++;
//                    System.out.println(boardMatrix[i][j] +" cheo"+ "\\"+ ":" + consecutive);
                }
                else if(boardMatrix[i][j] == '.') {
                    if(consecutive > 0) {
                        blocks--;
                        score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                        consecutive = 0;
                        blocks = 1;
                    }
                    else {
                        blocks = 1;
                    }
                }
                else if(consecutive > 0) {
                    score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);
                    consecutive = 0;
                    blocks = 2;
                }
                else {
                    blocks = 2;
                }

            }
            if(consecutive > 0) {
                score += getConsecutiveSetScore(consecutive, blocks, forOpponent == botTurn);

            }
            consecutive = 0;
            blocks = 2;
        }
        return score;
    }
//    public int[]
}
