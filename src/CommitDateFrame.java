/**
 * Frame allows user to search commits by date and diff selected commit with latest commit.
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class CommitDateFrame implements ActionListener{
	JFrame commitFrame;
	JPanel mainPanel;
	JList displayArea;
	JComboBox dateChooser;
	JComboBox monthChooser;
	JTextField yearInput;
	JButton searchButton;
	JList fileList;
	JButton diffButton;
	RepoBase rbase;
    String[] temp = {"Commits will appear here"};
    static String[] dates = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", 
    		"16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    static String[] months = {"January", "February", "March", "April", "May", 
    		"June", "July", "August", "September", "October", "November", "December"};
	
    //constructor
	public CommitDateFrame(RepoBase base) {
		rbase = base;
		commitFrame = new JFrame();
		commitFrame.setSize(800, 400);
		
		//initialize and add components
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		commitFrame.add(mainPanel);
		
		JPanel datePanel = new JPanel();
		
		dateChooser = new JComboBox(dates);
		datePanel.add(dateChooser);
		
		monthChooser = new JComboBox(months);
		datePanel.add(monthChooser);
		
		yearInput = new JTextField("yyyy");
		datePanel.add(yearInput);
		
		mainPanel.add(datePanel);
		
		displayArea = new JList(temp);
		mainPanel.add(displayArea);
		displayArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		fileList = new JList(rbase.getLatestFileList());
		mainPanel.add(fileList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		searchButton = new JButton("Search");
		mainPanel.add(searchButton);
		searchButton.addActionListener(this);
		
		diffButton = new JButton("Diff selected commit with latest commit");
		mainPanel.add(diffButton);
	}
	
	//make date frame visible
	public void showDateFrame() {
		fileList.setListData(rbase.getLatestFileList());
		commitFrame.setVisible(true);
	}

	//perform date search when searchButton pressed
	public void actionPerformed(ActionEvent e) {
		int date = dateChooser.getSelectedIndex() + 1;
		int month = monthChooser.getSelectedIndex();
		int year = Integer.parseInt(yearInput.getText());
		List<String> backString = rbase.searchDate(date, month, year);
		displayArea.setListData(backString.toArray());
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
