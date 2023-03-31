import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//Useful ressource for JfileChooser:

//https://www.javatpoint.com/java-awt
//https://gist.github.com/6footGeek/e990d3f6177625012124
//https://zetcode.com/gfx/java2d/shapesandfills/
//https://stackoverflow.com/questions/10767265/drawing-a-line-on-a-jframe

public class Convex_Hull extends JFrame implements ActionListener {
	
	//GUI Interactable & Visual Variables:
	
	JButton CSV_Button;
	JButton Line_Toggle;
	
	boolean toggledLine = true; //Initially Lines will be toggled until turned off
	
	JLabel AppTitle;
	
	JPanel panel = new JPanel();
	
	int windowWidth = 850;
	int windowHeight = 850;
	
	//Algorithm Variables:
    ArrayList <ConvexPoint> scatterPoints = new ArrayList<>(); //Actual Data Points
    ArrayList <ConvexPoint> encapsulationPoints = new ArrayList<>(); //Points that are encapsulating the scatterPoints
    static double timeBenchmark;
    
    
	Convex_Hull() {

		// Actual Window / Panel:
		getContentPane().add(panel);
		setSize(windowWidth, windowHeight);

		// Application Title :
		AppTitle = new JLabel();
		AppTitle.setBounds(10, 10, 600, 30);
		AppTitle.setText("Convex Hull Algorithm | SOFE2715U Final Project | BY:   Jason Manarroo , Jordan Hagedorn , Tejush Badal");

		// Button to Import .CSV files in:
		CSV_Button = new JButton("Import in .CSV file");
		CSV_Button.addActionListener(this);
		CSV_Button.setBounds(20, 40, 150, 20);
		
		// Button(s) to render Graphics with & Without Convex Hull Lines
		Line_Toggle = new JButton("Toggle Convex Hull Lines");
		Line_Toggle.addActionListener(this);
		Line_Toggle.setBounds(300, 40, 250, 20);
		

		// This is where you add all the components to the actual Desktop App
		add(CSV_Button);
		add(Line_Toggle);
		
		add(AppTitle);
	}

	
	//Allows for easy flipping of Y-Axis:
	
	public double coordFlip(int fullSize, double truePosition, int headerSpace) {
		return fullSize - truePosition + headerSpace;
	}
	
	public Shape pointGen(ConvexPoint point, 
			double scaleX, double scaleY, double offsetX, double offsetY, double headerOffset) {
        Shape newPoint = new Ellipse2D.Double( 
          		 (point.x * scaleX + offsetX), 
          		windowHeight - (point.y * scaleY + offsetY) + headerOffset, 
           5, 5);  
        return newPoint;
	}
	
	public Line2D lineGen(ConvexPoint point1, ConvexPoint point2, 
			double scaleX, double scaleY, double offsetX, double offsetY, double headerOffset) {

        Line2D line = new Line2D.Double(
        		 (point1.x*scaleX + offsetX), 
        		windowHeight - (point1.y*scaleY + offsetY) + headerOffset, 
        		 (point2.x*scaleX + offsetX), 
        		windowHeight - (point2.y*scaleY + offsetY) + headerOffset
        );
        
        return line;
	}
	
	
	/*
	 * GRAPHICS & PAINT FUNCTIONS
	 * 
	 *
	 * */
	
	public void paint(Graphics g) {
		super.paint(g); // fixes the immediate problem.
		Graphics2D MainGUI_Graphics = (Graphics2D) g;
		Line2D lin = new Line2D.Float(0, 100, 850, 100); // Line

		
		//If an algorithm is currently loaded in, call it and paint the plot
		//Fixes Re-scaling bug:
		
		if(scatterPoints.size() == 0 && encapsulationPoints.size() == 0) {
			paintPlot(getGraphics(), scatterPoints, encapsulationPoints, toggledLine);
			
		//Draw Menu Line if no graphics are being rendered right now:
		} else {
			MainGUI_Graphics.draw(lin);	
		}	
	}
	
    public void paintPlot(Graphics g, ArrayList<ConvexPoint> plot,ArrayList<ConvexPoint> lineList, boolean isLine) {
		
    	//XD
		double xmax = 0;
		double xmin = 0;
		double ymax = 0;	
		double ymin = 0;
		
		double paddingSize = 100;
		
		double currentWidth = windowWidth - paddingSize;

		for (int i = 0; i < plot.size(); i++) {
			ConvexPoint point = plot.get(i);
			if (point.x > xmax) {
				xmax = point.x;
			}
			if (point.x < xmin) {
				xmin = point.x;
			}
			if (point.y > ymax) {
				ymax = point.y;
			}
			if (point.y < ymin) {
				ymin = point.y;
			}
		}
		
		double xRange = xmax - xmin;
		double yRange = ymax - ymin;
		
		xRange = xRange * 1.20;
		yRange = yRange * 1.20;
//		
		double xScale = currentWidth / xRange;
		double yScale = currentWidth / yRange;
			
		//Offset Scaling:
		double offsetX = Math.abs(xmin * xScale) + paddingSize/2;
		double offsetY = Math.abs(ymin * yScale) + paddingSize/2+100;
    	
    	super.paint(g); // fixes the immediate problem.
		Graphics2D MainGUI_Graphics = (Graphics2D) g;

        for(int z = 0; z < plot.size(); z++){
           
        	ConvexPoint plotPoint = plot.get(z);  
            
            Shape newPoint = pointGen(plotPoint, xScale, yScale, offsetX, offsetY, 35);
            MainGUI_Graphics.fill(newPoint);
        }

        MainGUI_Graphics.setPaint(new Color(255, 0, 0)); //changes the color of the line 

        
        //Case where lines are draw alongst side the points:
        if(isLine){
            for(int z = 0; z <= lineList.size()-1; z++){
            	
            	Line2D line = null;
                ConvexPoint point1 = lineList.get(z); 
            	
                //Case where it's the last element in the line list, and we connect it back to the first element.
            	//Since we're z+1 'ing we need to compare at size - 1
                
                if(z==lineList.size() -1 ){
                	
                ConvexPoint point2 = lineList.get(0);                 
                 line = lineGen(point1, point2, xScale, yScale, offsetX, offsetY, 35);    
                //Regular case of drawing the line.
                } else {
                	
                ConvexPoint point2 = lineList.get(z+1); 
                line = lineGen(point1, point2, xScale, yScale, offsetX, offsetY, 35);
                }
                
                MainGUI_Graphics.draw(line);
            }
        }


        //Redraw menu line:
        
        MainGUI_Graphics.setPaint(new Color(0, 0, 0));
		Line2D menu_line = new Line2D.Float(0, 100, 850, 100); // Line

		MainGUI_Graphics.draw(menu_line);
		
		
		
    }

	public void actionPerformed(ActionEvent e) {
		
		//Input CSV button
		if (e.getSource() == CSV_Button) {
			
			JFileChooser fc = new JFileChooser();
			int i = fc.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				
				File f = fc.getSelectedFile();
				String filepath = f.getPath();
				scatterPoints = new ArrayList<>(); //Clear Points list
				
				//Basic Error Handling for BufferedReader since it can possibly throw an Exception: (Throws some Exceptions)
				try {
					BufferedReader br = new BufferedReader(new FileReader(filepath));
					String points = "";
					while ((points = br.readLine()) != null) {

                            String[] temp = points.split(",");
                            double x = Double.parseDouble(temp[0]);
                            double y = Double.parseDouble(temp[1]);
                            scatterPoints.add(new ConvexPoint(x, y));  

					}
				
					br.close(); //Close the file after reading all lines
					System.out.println("CSV Data Points have been parsed and stored !!!");
			
					long startTime = System.currentTimeMillis();
					
					// Loads the algorithm with parsed data points:s
					Convex_Hull_Algorithm loadedAlgorithm = new Convex_Hull_Algorithm(scatterPoints);
					
					// Run the loaded Algorithm Class, and store findings in the encapsulationPoints ArrayList
					encapsulationPoints = loadedAlgorithm.compute();
					
					long stopTime = System.currentTimeMillis();
					
					
					//Some console Logging:
					
					timeBenchmark = (stopTime - startTime);
					
	
					boolean algorithmDebug = false;
					
					if(algorithmDebug) {
						for(int o = 0; encapsulationPoints.size() > o; o++) {
							ConvexPoint plotPoint = encapsulationPoints.get(o);
							System.out.println(o);
							System.out.println(plotPoint.angle);
							System.out.println("AD: > X: "+ plotPoint.x +" , Y: "+ plotPoint.y +" \n");	
						}
					}
					

					System.out.println("\n>>The algorithm took: "+timeBenchmark+" ms to execute.\n");
					System.out.println("Algorithm Output: Convex hull size : "+encapsulationPoints.size()+", plot size (n.points): "+scatterPoints.size());
					paintPlot(getGraphics(), scatterPoints, encapsulationPoints, toggledLine);
					
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		//Line Toggle Event Listener
		
		if(e.getSource() == Line_Toggle) {
			toggledLine = !toggledLine; //Switch toggledLine
			paintPlot(getGraphics(), scatterPoints, encapsulationPoints, toggledLine);
		}
	}

	public static void main(String[] args) {
		
		//Calling GUI constructor, and setting up some parameters:
		
		Convex_Hull MainGUI = new Convex_Hull();
		MainGUI.setLayout(null);
		MainGUI.setVisible(true);
		MainGUI.setTitle("Convex Hull Algorithm | SOFE2715U Final");
		MainGUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Logging the Time Elapsed by Algorithm:
		
	}
}