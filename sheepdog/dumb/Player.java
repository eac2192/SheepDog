package sheepdog.dumb;

import java.util.*;
import sheepdog.sim.Point;

public class Player extends sheepdog.sim.Player {
    private int nblacks;
    private boolean mode;

    public void init(int nblacks, boolean mode) {
        this.nblacks = nblacks;
        this.mode = mode;
    }
    
    // Return: the next position
    // my position: dogs[id-1]
    public Point move(Point[] dogs, // positions of dogs
                      Point[] sheeps) { // positions of the sheeps
        Point current = dogs[id-1];
        double next_x=0;
        double next_y=0;
        double length;
        
        Point tmp;
	PointSortedByDistance[] sortedPoints = Arrays.copyOf(sheeps, sheeps.length, PointSortedByDistance[].class);
	Arrays.sort(sortedPoints);
        if (current.x < 50) {
        	length=Math.sqrt(Math.pow((current.x-51),2)+Math.pow((current.y-51),2));
        	next_x=current.x+((51-current.x)/length)*2;
        	next_y=current.y+((51-current.y)/length)*2;		
        }
        else {
        	for (int i=0; i<sheeps.length; i++){
        		if ((i % dogs.length)==id-1 & sheeps[i].x >50 ) {
        			length=Math.sqrt(Math.pow((current.x-sheeps[i].x),2)+Math.pow((current.y-sheeps[i].y),2));
        			if (length>1) {
        				System.out.printf("here it is dog %d", id);
        				System.out.printf("we are chasing sheep %d",i);
        				double offsetx;
        				double offsety;
        				double tmplength;
        				tmplength=Math.sqrt(Math.pow((sheeps[i].x-50),2)+Math.pow((sheeps[i].y-50),2));
        				offsetx=(sheeps[i].x-50)*(1+tmplength)/tmplength+50;
        				offsety=(sheeps[i].y-50)*(1+tmplength)/tmplength+50;
                	next_x=current.x+((offsetx-current.x)/length)*2;
                	next_y=current.y+((offsety-current.y)/length)*2;
                	
                	break;
                	}
        			else {
        				length=Math.sqrt(Math.pow((current.x-50),2)+Math.pow((current.y-50),2));
        	        	next_x=current.x+((50-current.x)/length)*2;
        	        	next_y=current.y+((50-current.y)/length)*2;	
        	        	break;
        			}
        			
        		}
        	}
        }
        current.x=next_x;
        current.y=next_y;
        		
        return current;
    }

    class PointSortedByDistance extends Point implements Comparable {
	public int compareTo(Object other) {
	    double dist = distanceToPoint(this.x, this.y, 50, 50);
	    Point otherPoint = (Point) other;
	    double otherDist = distanceToPoint(otherPoint.x, otherPoint.y, 50, 50);
	    if (dist > otherDist) {
		return 1;
	    } else if (dist < otherDist) {
		return -1;
	    }
	    return 0;
	}
    }

    public double distanceToPoint(double x1, double y1, double x2, double y2) {
	return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // calculates the distance to a line from (x0, y0) to (50, 50)
    public double distanceToLine(double x0, double y0, double x1, double y1) {
	double a = x0 - 50;
	double b = -(y0 - 50);
	double c = 50 * (y0 - 50) - 50 * (x0 - 50);
	return Math.abs(a * x1 + b * y1 + c) / Math.sqrt(a * a + b * b);
    }
}
