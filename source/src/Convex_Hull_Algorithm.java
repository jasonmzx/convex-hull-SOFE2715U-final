import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;



public class Convex_Hull_Algorithm {

	ArrayList<ConvexPoint> plot;
	
	//Construct the inputs to our Algorithm:
	
	public Convex_Hull_Algorithm(ArrayList<ConvexPoint> Plot) {
		this.plot = Plot;
	}
	
	
	ConvexPoint findLowestPoint() {
		
		ConvexPoint currentLowest = null;
		
		for(int x = 0; plot.size() > x; x++) {

			ConvexPoint current = plot.get(x);
			
			if(currentLowest == null) {
				currentLowest = current;
			}
			
			if(currentLowest.y > current.y ) {
				currentLowest = current;
			}
		}
		
		return currentLowest;
	}
	
	int ccw(ConvexPoint A, ConvexPoint B, ConvexPoint C) {
		double area = (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
		
		if(area < 0) return -1; // Clockwise
		if(area > 0) return  1; // Counter - Clockwise
		
		return 0;
	}
	
	
	double findAngle(ConvexPoint A, ConvexPoint B) {
		
		double opposite = Math.abs(A.y - B.y);
		double adjacent = Math.abs(A.x - B.x );
		
		if(adjacent == 0) {
			return 0;
		}
		
		double ratio = opposite / adjacent;
		
		if(A.x > B.x) {
			return 180 - Math.atan(ratio) * (180 / Math.PI);
		}
		
		return Math.atan(ratio) * (180 / Math.PI); //* (180 / Math.PI) ;
	}
	
	
	
	Stack<ConvexPoint> edgeStack = new Stack();
	
	ArrayList<ConvexPoint> compute() {
		
		//Initial Setup:s
		
		ConvexPoint initialPoint = findLowestPoint();
		
		System.out.println("LOWEST Y:");
		System.out.println("X: "+ initialPoint.x +" , Y: "+ initialPoint.y +" \n");
		
			for(int z = 0; plot.size() > z; z++) {
				
				ConvexPoint iter = plot.get(z);
				
				double iterAngle = findAngle(initialPoint, iter );
				
				iter.angle = iterAngle;
				
				plot.set(z, iter);	
			}
		
		//Sorted Plot points based on Angles:
		
		Collections.sort( plot, new ConvexComparator() );
		
		boolean running = true;
		int whileIter = 0;
		
		int currentNode = 1;
		
		for(int o = 0; plot.size() > o; o++) {
			
			ConvexPoint plotPoint = plot.get(o);
		
			//Debug:
			System.out.println(plotPoint.angle);
			System.out.println(">> X: "+ plotPoint.x +" , Y: "+ plotPoint.y +" \n");	
		}
		
		edgeStack.push(plot.get(0));
		edgeStack.push(plot.get(1));
		
		//The iterator is starting at 2, since 2 elements are initially pushed onto the Stack 
		//(Lowest node, and relative node with smallest angle (Relative to lowest node))
		
		for(int z = 2; z < plot.size(); z++) {
			
			ConvexPoint nextPoint = plot.get(z);
			ConvexPoint currentPop = edgeStack.pop();
			
			
			//Fixes EmptyStackException throwing
			try {
			
			while(edgeStack.peek() != null && ccw(edgeStack.peek(), currentPop, nextPoint) <= 0 ) {
				currentPop = edgeStack.pop();
			}
			
			} catch (EmptyStackException empty) {
				System.out.println("ESE Patch !");
			}
			
			edgeStack.push(currentPop);
			edgeStack.push(plot.get(z));
			
		}
		
		//TODO: Check for Colinearity
		ConvexPoint Co = edgeStack.pop();
		
		if(ccw(edgeStack.peek(), Co, initialPoint) > 0) {
			edgeStack.push(Co);
		}
		
		return new ArrayList<>(edgeStack);
	
		
	}
	
	
	
}
