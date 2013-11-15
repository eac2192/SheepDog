package sheepdog.g1;

import sheepdog.sim.Point;

public class Player extends sheepdog.sim.Player {
    private int nblacks;
    private boolean mode;
    public final double MAX_SPEED = 1.98;

    public void init(int nblacks, boolean mode) {
        this.nblacks = nblacks;
        this.mode = mode;
    }
    
    // Return: the next position
    // my position: dogs[id-1]
    public Point move(Point[] dogs, Point[] sheeps) { 
        Point next = new Point();
        if (sheeps.length >= 10) {
            next = this.many_dogs_strategy(dogs, sheeps);
        }
        Point current = dogs[id-1];

        return next;
    }

    public Point many_dogs_strategy(Point[] dogs, Point[] sheeps) {
        return this.move_straight(dogs[id-1], new Point(50, 50), MAX_SPEED);
    }

    public Point move_straight(Point start, Point dest, double speed) {
        Vector dir = new Vector(start, dest);
        dir = dir.get_unit();
        dir.times(speed);
        dir.plus(start);
        Point p = dir.toPoint();
        return dir.toPoint();
    }
}
