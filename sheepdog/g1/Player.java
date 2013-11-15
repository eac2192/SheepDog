package sheepdog.g1;

import sheepdog.sim.Point;

public class Player extends sheepdog.sim.Player {
    private int nblacks;
    private boolean mode;
    public int state;
    public Point pos;
    public int ndogs;
    public final double MAX_SPEED = 1.98;
    public final Point MIDPOINT = new Point(50, 50);
    static double dimension = 100.0; // dimension of the map

    public void init(int nblacks, boolean mode) {
        this.nblacks = nblacks;
        this.mode = mode;
        this.state = 0;
    }
    
    // Return: the next position
    // my position: dogs[id-1]
    public Point move(Point[] dogs, Point[] sheeps) { 
        this.ndogs = dogs.length;
        setPos(dogs[id-1]);
        Point next = new Point();
        if (sheeps.length >= 10) {
            next = this.many_dogs_strategy(dogs, sheeps);
        }
        Point current = dogs[id-1];

        return next;
    }

    public Point many_dogs_strategy(Point[] dogs, Point[] sheeps) {
        switch (state) {
            case 0:
                if (isWithinRange(MIDPOINT, 4.0)) {
                   this.state = 1; 
                }  
                return this.move_straight(dogs[id-1], MIDPOINT, MAX_SPEED);
            case 1:  
                if (isWithinRange(new Point(100, 50), 6.0)) {
                    this.state = 2;
                }
                return this.move_straight(dogs[id-1], new Point(100, 50), MAX_SPEED);
            case 2:
                double[] x_pos = evenSpread();
                Point dest = new Point(100, x_pos[id-1]);
                if (isWithinRange(dest, 2.0)) {
                    this.state = 3;
                }
                return this.move_straight(dogs[id-1], dest, MAX_SPEED);
            case 3:
                double[] y_pos = evenSpread();
                Point[] destinations = new Point[ndogs];
                for (int i=0; i<y_pos.length; i++) {
                    destinations[i] = new Point(100, y_pos[i]);

                }
                if (allInRange(dogs, destinations, 2.0)) {
                    this.state = 4;
                }
                return dogs[id-1];
            case 4:
                double x = pos.x;
                double y = pos.y;          

                dest = chaseClosestTowards(sheeps, MIDPOINT);
                if (distanceFrom(dest)< 10.0) {
                    return this.move_straight(dogs[id-1], dest, MAX_SPEED);
                }
                dest = new Point(x - MAX_SPEED/10, y);
                return dest;

        }
        return dogs[id-1];
        
    }

    public Point chaseClosestTowards(Point[] sheeps, Point dest) {
        double closest_dist = 200;
        int closest_idx = 0;
        double dist_to;
        for (int i=0; i<sheeps.length; i++) {
            dist_to = distanceFrom(sheeps[i]);
            if (dist_to <= closest_dist) {
                closest_dist = dist_to;
                closest_idx = i;
            }
        }
        Point closestSheep = sheeps[closest_idx];
        Vector dir = new Vector(closestSheep, dest);
        dir.reverse();
        dir = dir.get_unit();
        dir.times(1.9);
        dir.plus(closestSheep);
        return dir.toPoint();
    }

    public double[] evenSpread() {
        double[] pos = new double[ndogs];
        for (int d = 0; d < ndogs; ++d) {
            pos[d] = 1.0 * (d+1) / (ndogs+1) * dimension;
        }
        return pos;
    }

    public Point move_straight(Point start, Point dest, double speed) {
        Vector dir = new Vector(start, dest);
        dir = dir.get_unit();
        dir.times(speed);
        dir.plus(start);
        Point p = dir.toPoint();
        return dir.toPoint();
    }

    public boolean isWithinRange(Point dest, double range) {
        double dist = distanceFrom(dest);
        return dist <= range;
    }

    public double distanceFrom(Point dest) {
        Vector trayectory = new Vector(pos, dest);
        return trayectory.magnitude();
    }

    private void setPos(Point pos) {
        this.pos = pos;
    }

    public boolean allInRange(Point[] dogs, Point[] points, double range) {
        for (int i=0; i<points.length; i++) {
            Vector trayectory = new Vector(dogs[i], points[i]);
            if (trayectory.magnitude() >= range) {
                return false;
            }
        }
        return true;
    }
}
