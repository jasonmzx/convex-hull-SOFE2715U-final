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
			
			//WORKING
			if( currentLowest.y > current.y ) {
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
		
		
			for(int z = 0; plot.size() > z; z++) {
				
				//Asserting for plot lowest
			
				
				ConvexPoint iter = plot.get(z);
				
//				if(iter.x == initialPoint.x && iter.y == initialPoint.y) {
//					continue;
//				}
//				
				double iterAngle = findAngle(initialPoint, iter );
				
				iter.angle = iterAngle;
				
				plot.set(z, iter);	
				
				//Check if iter is initial point
				
				
				//Make sure that InitialPoint is ALWAYS at the top of plot Array
				if(iter.x == initialPoint.x && iter.y == initialPoint.y) {
					iter.angle = -1.0;
					plot.set(z, iter);
					
				}
			}
			
			System.out.println("\nLOWEST Y:");
			System.out.println("X: "+ initialPoint.x +" , Y: "+ initialPoint.y +" \n");
		
		//Sorted Plot points based on Angles:
		
		Collections.sort( plot, new ConvexComparator() );
		
		for(int r = 0; plot.size() > r; r++) {
			
			ConvexPoint iter = plot.get(r);
			
			System.out.println("Angle Test; >> "+iter.x + " y: "+iter.y);
			System.out.println("iA; "+iter.angle+"\n ");
		}
		
	
		
		edgeStack.push(plot.get(0)); //Assuming this is inital
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
