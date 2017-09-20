package app.game;

public class Delta {
    public double x, y, old_x, old_y;
    public int src, target;

    public void clear() {
        src = -1;
        target = -1;
    }
}
