package com.softwarelma.efe.server;

public class EfeServerPoint2D {

    private final int x;
    private final int y;

    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

    public EfeServerPoint2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
