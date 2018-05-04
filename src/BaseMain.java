/**
 * Main file for the java Git interface.
 * Required packages:
 * jdiff
 * org.eclipse.jgit-4.9.0.201710071750-r
 * slf4j-api-1.7.25
 * slf4j-simple-1.7.25
 * 
 * Author: Ana Berthel
 * Date: May 3, 2018
 */
import javax.swing.JFrame;

public class BaseMain {
	public static void main(String[] args) {
		JFrame mainFrame;
		
		//create the JFrame
		mainFrame = new JFrame("GUI");
		
		//set size
		mainFrame.setSize(1200, 60);
		
		//add display panel
		mainFrame.add(new ButtonPanel());
		
		//exit on close
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 		
		//show frame
		mainFrame.setVisible(true);

	}
}