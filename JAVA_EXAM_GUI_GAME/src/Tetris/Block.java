package Tetris;

import java.util.List;

public class Block {
    private List<Point> shape;
    private int color; // »ö»ó

    public Block(List<Point> shape, int color) {
        this.shape = shape;
        this.color = color;
    }

    public List<Point> getShape() {
        return shape;
    }

    public int getColor() {
        return color;
    }
}