package model;

public class Node {
    protected double score;
    protected int x;
    protected int y;

    public Node() {
    }

    public Node(double score) {
        this.score = score;
    }

    public Node(double score, int x, int y) {
        this.score = score;
        this.x = x;
        this.y = y;
    }

    public static Node max(Node node1, Node node2){
        if(node1.score>=node2.score){
            return node1;
        }
        return node2;
    }

    public static Node min(Node node1, Node node2){
        if(node1.score<=node2.score){
            return node1;
        }
        return node2;
    }
}
