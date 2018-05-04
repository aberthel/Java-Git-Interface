/**
 * Frame allows user to search commits by commit message and diff selected commit with latest commit.
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class CommitMessageFrame implements ActionListener{
	JFrame commitFrame;
	JPanel mainPanel;
	JList displayArea;
	JList fileList;
	JTextField inputField;
	JButton searchButton;
	JButton diffButton;
	RepoBase rbase;
	String[] temp = {"Commits will appear here"};
	String[] failsearch = {"No messages found"};
    
	//constructor
	public CommitMessageFrame(RepoBase base) {
		rbase = base;
	
		commitFrame = new JFrame();
		commitFrame.setSize(800, 400);
		
		//initialize and add components
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		commitFrame.add(mainPanel);
		
		inputField = new JTextField("word/phrase to search");
		mainPanel.add(inputField);
		
		displayArea = new JList(temp);
		mainPanel.add(displayArea);
		displayArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		fileList = new JList(rbase.getLatestFileList());
		mainPanel.add(fileList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		searchButton = new JButton("Search");
		mainPanel.add(searchButton);
		searchButton.addActionListener(this);
		searchButton.setActionCommand("search");
		
		diffButton = new JButton("Diff selected commit with latest commit");
		mainPanel.add(diffButton);
				
	}
	
	//make message frame visible
	public void showMessageFrame() {
		fileList.setListData(rbase.getLatestFileList());
		commitFrame.setVisible(true);
	}

	//perform a message search when searchButton pressed
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		
		if (c.equals("search")) {
			String searchString = inputField.getText();
			List<String> backString = rbase.searchCommits(searchString);
			displayArea.setListData(backString.toArray());
		} 
	}
	
	//returns name of selected commit
	public String getSelectedCommit() {
		int i = displayArea.getSelectedIndex();
		return rbase.getSelectedSearch(i);
	}
	
	//returns name of selected file
	public String getFileName() {
		return fileList.getSelectedValue().toString();
	}
	 
}
