/**
 * Frame allows user to select and diff files from two commits
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

public class DiffFrame implements ActionListener{
	JFrame diffFrame;
	JPanel mainPanel;
	JComboBox<String> commitSelector1;
	JComboBox<String> commitSelector2;
	JButton searchButton;
	RepoBase rbase;
    String filename;
    JList<Object> fileList;
    String[] nullstring = {"nothing"};
	
    //constructor
	public DiffFrame(RepoBase base) {
		rbase = base;
		diffFrame = new JFrame();
		diffFrame.setSize(800, 200);
		
		//initialize and add components
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		diffFrame.add(mainPanel);
		
		commitSelector1 = new JComboBox<String>(nullstring);
		mainPanel.add(commitSelector1);
		commitSelector1.addActionListener(this);

		fileList = new JList<Object>(rbase.getLatestFileList());
		mainPanel.add(fileList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		commitSelector2 = new JComboBox<String>(nullstring);
		mainPanel.add(commitSelector2);
		
		searchButton = new JButton("Diff");
		mainPanel.add(searchButton);
	}
	
	//make diff frame visible
	public void showDiffFrame() {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel(rbase.getDateList());
		DefaultComboBoxModel<String> model2 = new DefaultComboBoxModel(rbase.getDateList());
		commitSelector1.setModel(model);
		commitSelector2.setModel(model2);
		fileList.setListData(rbase.getLatestFileList());
		diffFrame.setVisible(true);
	}
	
	//get name of commit selected in first combobox
	public String getFirstName() {
		return rbase.getName(commitSelector1.getSelectedIndex());
	}
	
	//get name of commit selected in second combobox
	public String getSecondName() {
		return rbase.getName(commitSelector2.getSelectedIndex());
	}   	    
	
	//when a new commit is selected in the first combobox, set list of files to include all files from selected commit
	public void actionPerformed(ActionEvent e) {
		String commitname = getFirstName();
		try {
			fileList.setListData(rbase.readElementsAt(commitname).toArray());
		} catch (IOException e1) {
			System.out.println("Cannot read files from commit");
			e1.printStackTrace();
		}
	}    
}
