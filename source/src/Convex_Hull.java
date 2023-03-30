import java.awt.Color;
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
	
	JLabel AppTitle;
	JPanel panel = new JPanel();
	
	//Algorithm Variables:
    ArrayList <ConvexPoint> scatterPoints = new ArrayList<>(); //Actual Data Points
    ArrayList <ConvexPoint> encapsulationPoints = new ArrayList<>(); //Points that are encapsulating the scatterPoints

	Convex_Hull() {

		// Actual Window / Panel:
		getContentPane().add(panel);
		setSize(850, 850);

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

	public void paint(Graphics g) {
		super.paint(g); // fixes the immediate problem.
		Graphics2D MainGUI_Graphics = (Graphics2D) g;
		Line2D lin = new Line2D.Float(0, 100, 850, 100); // Line


		MainGUI_Graphics.draw(lin);
	}
	
    public void paintPlot(Graphics g, ArrayList<ConvexPoint> plot,ArrayList<ConvexPoint> lineList, boolean isLine) {
		super.paint(g); // fixes the immediate problem.
		Graphics2D MainGUI_Graphics = (Graphics2D) g;

        for(int z = 0; z < plot.size(); z++){
            ConvexPoint plotPoint = plot.get(z);    
            Shape newPoint = new Ellipse2D.Double( 
            		 (plotPoint.x * 100 + 200), 
            		 (plotPoint.y * 100 + 200), 
            5, 5);  
            MainGUI_Graphics.fill(newPoint);
        }

        MainGUI_Graphics.setPaint(new Color(255, 0, 0)); //changes the color of the line 

        if(isLine){
            for(int z = 0; z <= lineList.size(); z++){

				//This checks for the case where we're at the last element in the Convex Hull Connection list and need to
				// Attach the last element to the first
            	
            	//Since we're z+1 'ing we need to compare at size - 1
                if(z==lineList.size() -1 ){
                ConvexPoint point1 = lineList.get(z); 
                ConvexPoint point2 = lineList.get(0); 
                
                Line2D line = new Line2D.Float(
                		(int) (point1.x*100 + 200), 
                		(int) (point1.y*100 + 200), 
                		(int) (point2.x*100 + 200), 
                		(int) (point2.y*100 + 200)
                );
                
                MainGUI_Graphics.draw(line);
                break;
                }
                ConvexPoint point1 = lineList.get(z); 
                ConvexPoint point2 = lineList.get(z+1); 
                
                Line2D line = new Line2D.Float(
                		(int) (point1.x*100 + 200), 
                		(int) (point1.y*100 + 200), 
                		(int) (point2.x*100 + 200), 
                		(int) (point2.y*100 + 200)
                );
                
                MainGUI_Graphics.draw(line);
            }
        }
    }

	public void actionPerformed(ActionEvent e) {
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
						System.out.println(points);
						//Adding CSV points to scatterPoints ArrayList
						
//                        if (points.startsWith("ï")){ // if its first line in csv
//                            points = points.substring(3); // remove first chars
//                            points = points.substring(1, points.length() - 1); // remove quotes
                            
                            //turn array of string coords into x,y and make new convex point
                            String[] temp = points.split(",");
                            double x = Double.parseDouble(temp[0]);
                            double y = Double.parseDouble(temp[1]);
                            scatterPoints.add(new ConvexPoint(x, y));  

					}
					
					br.close(); //Close the file that's being read
					
					//Loads the algorithm with parsed data points:
					Convex_Hull_Algorithm loadedAlgorithm = new Convex_Hull_Algorithm(scatterPoints);
					
					// Run the loaded Algorithm Class, and store findings in the encapsulationPoints ArrayList
					encapsulationPoints = loadedAlgorithm.compute();
					
					for(int o = 0; encapsulationPoints.size() > o; o++) {
						
						ConvexPoint plotPoint = encapsulationPoints.get(o);
					
						//Debug:
						System.out.println(o);
						System.out.println(plotPoint.angle);
						System.out.println("AD: > X: "+ plotPoint.x +" , Y: "+ plotPoint.y +" \n");	
					}
					
					System.out.println("Algorithm Debug: convex hull size : "+encapsulationPoints.size()+", plot size : "+scatterPoints.size());
					paintPlot(getGraphics(), scatterPoints, encapsulationPoints, true);
					
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		
		//Calling GUI constructor, and setting up some parameters:
		
		Convex_Hull MainGUI = new Convex_Hull();
		MainGUI.setLayout(null);
		MainGUI.setVisible(true);
		MainGUI.setTitle("Convex Hull Algorithm | SOFE2715U Final");
		MainGUI.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
}