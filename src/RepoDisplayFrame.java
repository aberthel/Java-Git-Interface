/**
 * Frame allows user to view all commits in repository and diff selected commit with latest commit.
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class RepoDisplayFrame {
	JFrame displayFrame;
	JPanel displayPanel;
	JList textBox;
	JList fileList;
	JButton diffButton;
	RepoBase rbase;
	String[] nullstring = {"Could not find any commits/files in the repository"};
	
	//constructor
	public RepoDisplayFrame(RepoBase base) throws HeadlessException, IOException, GitAPIException {
		rbase = base;
		
		//create the JFrame
		displayFrame = new JFrame("All Commits");
				
		//initialize and add components
		displayPanel = new JPanel();
		displayFrame.add(displayPanel);
		
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		List<String> text = rbase.showLog();
		
		JLabel commitLabel = new JLabel("List of commits:");
		displayPanel.add(commitLabel);
		
		textBox = new JList(text.toArray());
		textBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		displayPanel.add(textBox);
		
		JLabel fileLabel = new JLabel("List of files in latest commit:");
		displayPanel.add(fileLabel);
		
		fileList = new JList(rbase.getLatestFileList());
		displayPanel.add(fileList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		diffButton = new JButton("Diff selected commit with latest commit");
		displayPanel.add(diffButton);
		
		//set size
		displayFrame.setSize(1200, 600);
		
		//show frame
		displayFrame.setVisible(true);
	}
	
	//update list of commits shown
	public void updateText() {
		try {
			textBox.setListData(rbase.showLog().toArray());
		} catch (IOException | GitAPIException e) {
			textBox.setListData(nullstring);
		}
	}
	
	//update list of files shown
	public void updateFiles() {
		fileList.setListData(rbase.getLatestFileList());
	}
    
	//get name of selected commit
    public String getCommitName() {
    		return rbase.getName(textBox.getSelectedIndex());
    }
    
    //get name of selected file
    public String getFileName() {
    		return fileList.getSelectedValue().toString();
    		
    }
}