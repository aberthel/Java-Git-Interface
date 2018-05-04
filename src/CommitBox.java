/**
 * Frame confirms files to be committed and prompts user for commit message.
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class CommitBox {
	JFrame commitFrame;
	JPanel commitPanel;
	JTextArea addArea;
	JTextField messageField;
	JButton commitButton;
	RepoBase rbase;
	
	//constructor
	public CommitBox(RepoBase base) {
		rbase = base;
		//create the JFrame
		commitFrame = new JFrame();
						
		//add display panel
		commitPanel = new JPanel(new BorderLayout());
		
		String text = "";
		try {
			text = rbase.showStatus();
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		
		//add components
		addArea = new JTextArea(text);
		commitPanel.add(addArea, BorderLayout.NORTH);
		
		messageField = new JTextField("Commit Message");
		commitPanel.add(messageField, BorderLayout.CENTER);
		
		commitButton = new JButton("Commit Changes");
		commitPanel.add(commitButton, BorderLayout.SOUTH);
		
		commitFrame.add(commitPanel);
				
		//set size
		commitFrame.setSize(200, 400);
				
	}
	
	//make commit box visible
	public void showBox() {
		String text = "";
		try {
			text = rbase.showStatus();
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		addArea.setText(text);
		commitFrame.setVisible(true);
	}
	
	//commit all files that have been added to repository
	public void commit() {
		try {
			rbase.commitFile(messageField.getText());
		} catch (IOException | GitAPIException e1) {
			System.out.println("Could not commit files");
			e1.printStackTrace();
		}
		commitFrame.setVisible(false);
	}
	
}
