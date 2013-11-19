package sheepdog.g1;

import sheepdog.sim.Point;
import java.util.ArrayList;

public class Player extends sheepdog.sim.Player {
    private int nblacks;
    private boolean mode;
    public int state;
    public Point pos;
    public int ndogs;
    public ArrayList<ArrayList<Point>> partitions; 
    public final double MAX_SPEED = 1.98;
    public final Point MIDPOINT = new Point(50, 50);
    static double OPEN_LEFT = 49.0; // left side of center openning
    static double OPEN_RIGHT = 51.0;
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
        this.partitions = new ArrayList<ArrayList<Point>>();
        setPos(dogs[id-1]);
        Point next = new Point();
        if (sheeps.length >= 10) {
            createPartitions(dogs, sheeps);
            next = this.many_dogs_strategy(dogs, sheeps);
        }
        Point current = dogs[id-1];
        System.out.println("state:" + this.state);
        return next;
    }

    public void createPartitions(Point[] dogs, Point[] sheeps) {
        double[] y_pos = evenSpread();
        for (int idx=0; idx<dogs.length; idx++) {
            ArrayList<Point> group = new ArrayList<Point>();
            double range_bottom, range_top;
            if (idx-1 < 0) {
                range_bottom = 0.0;
            }
            else {
                range_bottom = (y_pos[idx-1] + y_pos[idx])/2.0;
            }
            if (idx+1 >= dogs.length) {
                range_top = 100.0;
            }
            else {    
                range_top = (y_pos[idx] + y_pos[idx+1])/2.0;
            }
            for (int i=0; i<sheeps.length; i++) {
                if (sheeps[i].y >= range_bottom && sheeps[i].y <= range_top) {
                    group.add(sheeps[i]);
                }
            }
            this.partitions.add(group);
        }
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

                // dest = chaseClosestTowards(sheeps, MIDPOINT);
                ArrayList<Point> group = this.partitions.get(id-1);
                System.out.println(group.size());
                if (group.size() == 0) {
                    // System.out.println("boom");
                    this.state = 5;
                    System.out.println(this.pos.x + " , " + this.pos.y);
                    return this.pos;
                }
                dest = chaseFurthestFromGoal(group, MIDPOINT);
                if (distanceFrom(MIDPOINT) < 3) {
                    this.state = 6;
                    System.out.println("boom");
                    return this.move_straight(dogs[id-1], dest, MAX_SPEED);       
                }
                // if (distanceFrom(dest)< 10.0 && isClosestTo(dogs, dest)) {
                return this.move_straight(dogs[id-1], dest, MAX_SPEED);
                // }
                // dest = new Point(x - MAX_SPEED/10, y);
                // return dest;
            case 5:
                dest = chaseClosestTowards(sheeps, MIDPOINT);
                if (distanceFrom(MIDPOINT) < 3) {
                    this.state = 6;       
                }
                if (!isClosestTo(dogs, dest)) {
                    group = this.partitions.get(id-1);
                    if (group.size() != 0) {
                        this.state = 4;
                    }
                    else {
                        this.state = 8;
                    }
                    dest = chaseFurthestFromGoal(sheeps, MIDPOINT);
                    return this.move_straight(dogs[id-1], dest, MAX_SPEED);
                }
                return this.move_straight(dogs[id-1], dest, MAX_SPEED);
            case 6:
                dest = chaseClosestTowards(sheeps, new Point(35.0, 50.0));
                if (distanceFrom(new Point(40.0, 50.0)) < 3) {
                    this.state = 7;      
                }
                return this.move_straight(dogs[id-1], dest, MAX_SPEED);
            case 7:
                if (isWithinRange(MIDPOINT, 3.0)) {
                   this.state = 8; 
                }  
                return this.move_straight(dogs[id-1], MIDPOINT, MAX_SPEED);
            case 8: 
                dest = chaseFurthestFromGoal(sheeps, MIDPOINT);
                if (distanceFrom(dest) < 2) {
                    this.state = 5;   
                }
                return this.move_straight(dogs[id-1], dest, MAX_SPEED); 

        }
        return dogs[id-1];
        
    }

    public int dogsNearby(Point[] dogs) {
        int count = 0;
        for (int i=0; i<dogs.length; i++) {
            if (i != id-1 && distanceFrom(dogs[i]) < 5) {
                count++;
            }
        }
        return count;
    }

    public boolean isClosestTo(Point[] dogs, Point dest) {
        double closest_dist = 200;
        int closest_idx = 0;
        double dist_to;
        for (int i=0; i<dogs.length; i++) {
            dist_to = distanceBetween(dogs[i], dest);
            if (dist_to <= closest_dist) {
                closest_dist = dist_to;
                closest_idx = i;
            }
        }
        if (closest_idx == id-1) {
            return true;
        }
        else {
            return false;
        }
    }

    public Point chaseFurthestFromGoal(Point[] sheeps, Point dest) {
        ArrayList<Point> temp = new ArrayList<Point>();
        for (int i=0; i<sheeps.length; i++) {
            temp.add(sheeps[i]);
        }
        return chaseFurthestFromGoal(temp, dest);
    }

    public Point chaseFurthestFromGoal(ArrayList<Point>sheeps, Point dest) {
        double furthest_dist = 0;
        Point furthest_sheep = dest;
        double dist_to;
        for (Point sheep : sheeps) {
            dist_to = distanceBetween(sheep, dest);
            if (dist_to >= furthest_dist) {
                furthest_dist = dist_to;
                furthest_sheep = sheep;
            }

        }
        return chaseTowards(furthest_sheep, dest);
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
        return chaseTowards(closestSheep, dest);
    }

    public Point chaseTowards(Point sheep, Point dest) {
        Vector dir = new Vector(sheep, dest);
        dir.reverse();
        Vector temp = dir.get_unit();
        temp.times(1.9);
        temp.plus(sheep);
        return temp.toPoint();
    }

    public boolean isValid(Point dest) {
        if (dest.x > 100 || dest.x < 0 || dest.y > 100 || dest.y < 0 || hitTheFence(pos, dest)) {
            return false;
        }
        else {
            return true;
        }
    }

    public double[] evenSpread() {
        double[] pos = new double[ndogs];
        for (int d = 0; d < ndogs; ++d) {
            pos[d] = 1.0 * (d+1) / (ndogs+1) * dimension;
        }
        return pos;
    }

    public Point move_straight(Point start, Point dest, double speed) {
        boolean valid = false; 
        Vector dir = new Vector(start, dest);
        while (!valid) {
            dir = new Vector(start, dest);
            dir = dir.get_unit();
            dir.times(speed);
            dir.plus(start);
            if (isValid(dir.toPoint()) || speed <= 0.1) {
                valid = true;
            }
            else {
                speed = speed - 0.1;
            }
        }
        if (speed <= 0.1) {
            Point p = dir.toPoint();
            return new Point(50.0, p.y);
        }
        else {
           return dir.toPoint(); 
        }   
    }

    public boolean isWithinRange(Point dest, double range) {
        double dist = distanceFrom(dest);
        return dist <= range;
    }

    public double distanceFrom(Point dest) {
        Vector trayectory = new Vector(pos, dest);
        return trayectory.magnitude();
    }

    public double distanceBetween(Point start, Point dest) {
        Vector trayectory = new Vector(start, dest);
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

    int getSide(double x, double y) {
        if (x < dimension * 0.5)
            return 0;
        else if (x > dimension * 0.5)
            return 1;
        else
            return 2;
    }

    int getSide(Point p) {
        return getSide(p.x, p.y);
    }

     boolean hitTheFence(Point p1, Point p2) {
        return hitTheFence(p1.x, p1.y, p2.x, p2.y);
     }

    boolean hitTheFence(double x1, double y1, double x2, double y2) {
        if (getSide(x1,y1) == getSide(x2, y2))
            return false;
        if (getSide(x1,y1) == 2 || getSide(x2,y2) == 2)
            return false;
        
        double y3 = (y2-y1)/(x2-x1)*(50-x1)+y1;

        assert y3 >= 0 && y3 <= 100;

        if (y3 >= OPEN_LEFT && y3 <= OPEN_RIGHT)
            return false;
        else{
            System.out.println("fence");
            return true;
        }
    }
}
