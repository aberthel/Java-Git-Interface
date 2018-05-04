/**
 * Main button interface. Buttons navigate to frames which have different features.
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.qarks.util.files.diff.ui.DiffPanel;

public class ButtonPanel extends JPanel implements ActionListener{
	JButton openButton; //button sets repository to repository at selected location
	JButton newButton; //button creates new repository at selected location
	JButton saveButton; //button opens a GUI for making a commit to repo
	JButton commitSearchButton; //button opens commit message frame
	JButton dateSearchButton; //button opens commit date frame
	JButton diffButton; //button opens diffPanel
	
	JFileChooser chooser; //choosing folders containing repositories
	JFileChooser filechooser; //specifically for choosing files to add for committing
	
	//used for the majority of commands accessing the repository
	RepoBase rbase;
	
	//search and diff frames
	RepoDisplayFrame rdf;
	CommitMessageFrame cmf;
	CommitDateFrame cdf;
	CommitBox box;
	JFrame diffFrame;
	DiffPanel diffPanel;
	
	File file; //file currently being viewed
	File oldFile; //files for diff viewing
	File newFile;
	Repository repository1;
	
	static String saveFileName = "lastFilePath.txt";
	
	DiffFrame diffs;
	
	//constructor
	public ButtonPanel() {
		
		//initialize and add buttons
		openButton = new JButton("Open Existing Repository");
		this.add(openButton);
		openButton.addActionListener(this);
		openButton.setActionCommand("open");
		
		newButton = new JButton("Create New Repository");
		this.add(newButton);
		newButton.addActionListener(this);
		newButton.setActionCommand("new");
		
		saveButton = new JButton("Commit Changes");
		this.add(saveButton);
		saveButton.addActionListener(this);
		saveButton.setActionCommand("commit");
		
		commitSearchButton = new JButton("Search Commit Messages");
		this.add(commitSearchButton);
		commitSearchButton.addActionListener(this);
		commitSearchButton.setActionCommand("commit search");
		
		dateSearchButton = new JButton("Search Commits by Date");
		this.add(dateSearchButton);
		dateSearchButton.addActionListener(this);
		dateSearchButton.setActionCommand("date search");
		
		diffButton = new JButton("Diff Files");
		this.add(diffButton);
		diffButton.addActionListener(this);
		diffButton.setActionCommand("diff");
		
		//initialize all search and diff frames
		rbase = new RepoBase();
		try {
			rdf = new RepoDisplayFrame(rbase);
			rdf.diffButton.addActionListener(this);
			rdf.diffButton.setActionCommand("diff from main");
			
			box = new CommitBox(rbase);
			box.commitButton.addActionListener(this);
			box.commitButton.setActionCommand("commit2");
			
			cdf = new CommitDateFrame(rbase);
			cdf.diffButton.addActionListener(this);
			cdf.diffButton.setActionCommand("diff date search");
			
			cmf = new CommitMessageFrame(rbase);
			cmf.diffButton.addActionListener(this);
			cmf.diffButton.setActionCommand("diff commit search");
			
			diffs = new DiffFrame(rbase);
			diffs.searchButton.addActionListener(this);
			diffs.searchButton.setActionCommand("start diff");
			
			diffFrame = new JFrame();
			diffFrame.setSize(300, 400);
			diffPanel = new DiffPanel("");
			diffFrame.add(diffPanel);
		} catch (HeadlessException | IOException | GitAPIException e1) {
			System.out.println("Could not initialize all search frames");
			e1.printStackTrace();
		} 

		//open the last opened repository if possible
		try {
			FileReader reader = new FileReader(saveFileName);
			String fn = "";
			int c = reader.read();
			while(c > 0) {
				fn += (char) c;
				c = reader.read();
			}
			System.out.println(fn);
			System.out.println(fn.length());
			reader.close();
			
			File file2 = new File(fn);
			openRepo(file2);
		} catch (IOException | HeadlessException | NullPointerException e) {
			//do nothing
			System.out.println("No repo saved");
		} 
		
		//initialize file chooser for open and new commands
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setFileHidingEnabled(false);
		
	}

	//processes button clicks from ButtonPanel and from the search/diff frames
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		
		if(c.equals("open")) { //open an already existing repository
			chooseRepo();
		} else if(c.equals("new")) { //create and open a new repository
	        newRepo();
		} else if (c.equals("commit")) { //open up the UI to make a new commit
			commitFiles();
		} else if (c.equals("commit2")) { //make the new commit and update the main frame accordingly
			box.commit();
			rdf.updateText();
			rdf.updateFiles();
		} else if (c.equals("commit search")) { //open UI to search commit messages
			cmf.showMessageFrame();
		} else if (c.equals("date search")) { //open UI to search commit dates
			cdf.showDateFrame();
		} else if (c.equals("diff")) { //open UI to diff two commits
			diffs.showDiffFrame();
		} else if (c.equals("start diff")) { //show diff of two commits
			String diffName1 = (String) diffs.getFirstName();
			String diffName2 = (String) diffs.getSecondName();
			String fileName = (String) diffs.fileList.getSelectedValue();
			
			diffFiles(diffName1, diffName2, fileName);
		} else if (c.equals("diff commit search")) { //show diff of message search result and current commit
			String diffName1 = cmf.getSelectedCommit();
			String diffName2 = rbase.getLatestCommit();
			String fileName = cmf.getFileName();
			
			diffFiles(diffName1, diffName2, fileName);
		} else if (c.equals("diff date search")) { //show diff of date search result and current commit
			String diffName1 = cdf.getSelectedCommit();
			String diffName2 = rbase.getLatestCommit();
			String fileName = cdf.getFileName();
			
			diffFiles(diffName1, diffName2, fileName);
		} else if (c.equals("diff from main")) { //show diff of selected commit and current commit
			String diffName1 = rdf.getCommitName();
			String diffName2 = rbase.getLatestCommit();
			String fileName = rdf.getFileName();
			
			diffFiles(diffName1, diffName2, fileName);
		} 
	}
	
	//opens the repository at the given file path
	public void openRepo(File file) {
		//create repository
		try {
			repository1 = FileRepositoryBuilder.create(file);
		} catch (IOException e2) {
			System.out.println("Could not open the repository");
			e2.printStackTrace();
		}        

		System.out.println("Having repository: " + repository1.getDirectory());
		rbase.setNewRepo(repository1);
			
		rdf.updateText();
		rdf.updateFiles();
		
		filechooser = new JFileChooser(repository1.getDirectory().getParentFile());
		filechooser.setMultiSelectionEnabled(true);
	}
	
	//save repo file location to open automatically at next launch
	public void saveRepoPath(File file) {
		try {
			PrintWriter writer = new PrintWriter(saveFileName);
			writer.print(file.getPath());
			writer.close();	
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file path");
			e.printStackTrace();
		}
	}
	
	//opens filechooser to allow selection of a repository, and opens repository
	public void chooseRepo() {
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			openRepo(file);
			saveRepoPath(file);
		}
	}
	
	//prompts for a file path for a new repository, and creates and opens that repository
	public void newRepo() {
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {			
			File path = chooser.getSelectedFile();
			try {
				Git.init().setDirectory(path).call();
			} catch (IllegalStateException | GitAPIException e) {
				System.out.println("Could not create a new repository at the selected path");
				e.printStackTrace();
			}
			path = new File(path, ".git");
			openRepo(path);
			saveRepoPath(path);
		}    
	}

	//opens UI for making a new commit
	public void commitFiles() {
		int returnVal = filechooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = filechooser.getSelectedFiles();
			try {
				rbase.addFile(files);
			} catch (IOException | GitAPIException e1) {
				System.out.println("Could not add files");
				e1.printStackTrace();
			}
			box.showBox();
		}	
	}
	
	//reads files to diff into temporary files
	public void diffFiles(String diffName1, String diffName2, String fileName) {
		try {
			oldFile = rbase.readFileFromCommit(diffName1, fileName, "temp1.txt");
			newFile = rbase.readFileFromCommit(diffName2, fileName, "temp2.txt");
		} catch (IOException e1) {
			System.out.println("Could not diff files");
			e1.printStackTrace();
		}
		showDiff(oldFile, newFile);
	}
	
	//sets diff panel files and makes diff frame visible
	public void showDiff(File oldFile, File newFile) {
		if (oldFile != null && newFile != null) {
			diffPanel.launchDiff(oldFile, newFile);
			if (oldFile.equals(newFile)) {
				JOptionPane.showMessageDialog(diffPanel, "These are the same file.");
			}
		}
		diffFrame.setVisible(true);
	}
}

