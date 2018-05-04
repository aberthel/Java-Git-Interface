/**
 * This class handles the repository: accessing and listing commits/files as requested.
 * Methods so marked include code modified from JGitCookbook
 * Found at: https://github.com/centic9/jgit-cookbook
 * 
 * Author: Ana Berthel
 * Date: May 3, 2018
 */

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JFileChooser;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class RepoBase {

	private Repository repository;
	private Git git;
	private Date[] time;
    private String[] name;
    private String[] commits;
    private Object[] filesOfLatestCommit;
    ArrayList<String> searchNameList;
    
	//constructor if repository is already initialized
	public RepoBase(Repository repo) {
		repository = repo;
		git = new Git(repository);
		try {
			getCommits();
		} catch (IOException | GitAPIException e) {
			System.out.println("Could not get comments from repository.");
			e.printStackTrace();
		}
		
		try {
			filesOfLatestCommit = (String[]) readElementsAt(getLatestCommit()).toArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//constructor if repository is not yet initialized
	public RepoBase() {
		repository = null;
		git = null;
		time = null;
		name = null;
		commits = null;
		filesOfLatestCommit = null;
	}
	
	//sets a new repository and updates all related files
	public void setNewRepo(Repository repo) {
		repository = repo;
		git = new Git(repository);
		try {
			getCommits();
		} catch (IOException | GitAPIException e) {
			System.out.println("Could not find any commits");
		}
		
		try {
			filesOfLatestCommit = readElementsAt(getLatestCommit()).toArray();
		} catch (IOException | NullPointerException e) {
			System.out.println("Could not find files of any commits");
		}
	}
	
	//returns name of commit at index
	public String getName(int index) {
		if (name != null) {
			return name[index];
		} else {
			return null;
		}
	}
	
	//returns name of latest commit (at index 0)
	public String getLatestCommit() {
		if (name != null) {
			return name[0];
		} else {
			return null;
		}
	}
	
	//returns name of commit that has been selected by some selector
	public String getSelectedSearch(int i) {
		return searchNameList.get(i);
	}
	
	//returns array of the files active in the latest commit
	public Object[] getLatestFileList() {
		if (filesOfLatestCommit != null) {
			return filesOfLatestCommit;
		} else {
			String[] nullString = {"No files found"};
			return nullString;
		}
	}
	
	//returns array of the dates associated with each commit (latest to earliest)
	public Date[] getDateList() {
		if (time != null) {
			return time;
		} else {
			return null;
		}
	}
	
	//sets contents of arrays of commit names, dates, and messages
	//modified from JGitCookbook
	public void getCommits() throws IOException, GitAPIException {
	    	Iterable<RevCommit> logs = git.log().call();
	    int count = 0;	               
	    logs = git.log().all().call();
	    
	    //gets the number of commits present
	    for (RevCommit rev : logs) {
	    		count++;
	    }
	            
	    time = new Date[count];
	    name = new String[count];
	    commits = new String[count];
	    int count2 = 0;
	    logs = git.log().all().call();
	    
	    //stores name, time, and message for each commit into arrays
	    for (RevCommit rev : logs) {
	    		PersonIdent authorIdent = rev.getAuthorIdent();
	    		Date authorDate = authorIdent.getWhen();
	    		time[count2] = authorDate;
	    		name[count2] = rev.getName();
	    		commits[count2] = rev.getFullMessage();
	    		count2 ++;
	    }   
	}
	
	//returns a list of files in a particular commit
	//modified from JGitCookbook
	public List<String> readElementsAt(String commit) throws IOException {
		List<String> items = new ArrayList<>();
		RevCommit revCommit = buildRevCommit(commit);

		// and using commit's tree find the path
		RevTree tree = revCommit.getTree();

		try (TreeWalk treeWalk = new TreeWalk(repository)) {
			treeWalk.addTree(tree);
			treeWalk.setRecursive(false);
			treeWalk.setPostOrderTraversal(false);

			while(treeWalk.next()) {
				items.add(treeWalk.getPathString());
			}
		}
		return items;
	}
	
	//helper function for readElementsAt()
	//modified from JGitCookbook
    private RevCommit buildRevCommit(String commit) throws IOException {
        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
            return revWalk.parseCommit(ObjectId.fromString(commit));
        }
    }	 
    
    //makes a temp file of an older commit version
    //modified from JGitCookbook
    public File readFileFromCommit(String commitName, String fileName, String tempName) throws IOException {
    		File tempFile = new File(tempName);
    		tempFile.createNewFile();
    		FileOutputStream fileop = new FileOutputStream(tempFile);
    		ObjectId lastCommitId = repository.resolve(commitName);

        // a RevWalk allows to walk over commits based on some filtering that is defined
        try (RevWalk revWalk = new RevWalk(repository)) {
        		RevCommit commit = revWalk.parseCommit(lastCommitId);
        		// and using commit's tree find the path
        		RevTree tree = commit.getTree();

        		// now try to find a specific file
        		try (TreeWalk treeWalk = new TreeWalk(repository)) {
        			treeWalk.addTree(tree);
        			treeWalk.setRecursive(true);
        			treeWalk.setFilter(PathFilter.create(fileName));
               	if (!treeWalk.next()) {
                  	System.out.println("File is not in the older version");
                    	throw new IllegalStateException("Did not find expected file");
               	}

                	ObjectId objectId = treeWalk.getObjectId(0);
                	ObjectLoader loader = repository.open(objectId);

                	// and then one can the loader to read the file
                	loader.copyTo(fileop); // make that the file output stream
        		}
        		revWalk.dispose();
        }
        fileop.flush();
        fileop.close();
        return tempFile;
    }
	
    //returns a list of strings describing each commit
    //modified from JGitCookbook
    public  List<String> showLog() throws IOException, GitAPIException {
        List<String> str = new ArrayList<>();
		if (git != null) { 
			int count = 0;
			Iterable<RevCommit> logs = git.log().call();
			logs = git.log().all().call();
			for (RevCommit rev : logs) {
            		PersonIdent authorIdent = rev.getAuthorIdent();
            		Date authorDate = authorIdent.getWhen();
            		str.add("Date: " + authorDate.toString() + "\n message: " + rev.getShortMessage());
            		count++;
			}
			System.out.println("Had " + count + " commits overall in repository");     
		} else {
			str.add("No repository opened for viewing");
		}
        return str;
    } 
    
    
    //adds file(s) to be committed
    //modified from JGitCookbook
    public void addFile(File[] files) throws IOException, GitAPIException {              
        	File tempfile;
        	for (int i=0; i<files.length; i++) {
        		tempfile = files[i];
        		git.add().addFilepattern(tempfile.getName()).call();
        	}
    }
    
    //returns a string describing files that are to be committed, and files that are to be ignored.
    //modified from JGitCookbook
    public String showStatus() throws IOException, GitAPIException {
    		String str = "Files to be saved: \n"; 
    		if (git != null) {
             Status status = git.status().call();
             str = str + "Newly added:" + status.getAdded() + "\n";
             str = str + "Changed: " + status.getChanged() + "\n \n";
             str = str + "Files not to be saved: \n";
             str = str + "Untracked:" + status.getUntracked() + "\n";
             str = str + "Ignored:" + status.getIgnoredNotInIndex() + "\n";
             str = str + "Modified: " + status.getModified();
         } else {
        	 	str = "No repository to show status\n";
         }
         return str;
    }
    
	//commits file(s) to the repository
	public void commitFile(String message) throws IOException, GitAPIException {
		git.commit().setMessage(message).call();
	}
	
	//searches for the commits relevant to a certain day
	public List<String> searchDate(int day, int month, int year) {		 
		//searches for any timestamp within 24 hours of selected date
		Calendar newdate = GregorianCalendar.getInstance();
		newdate.set(year, month, day, 0, 0, 0);
		 
		Calendar enddate = GregorianCalendar.getInstance();
		enddate.set(year, month, day, 0, 0, 0);
		enddate.add(Calendar.DAY_OF_MONTH, 1);
		 
		String rstring = "";
		boolean ct = true;
		int x = -1;
		List<String> items = new ArrayList<>();
		searchNameList = new ArrayList<String>();
		//iterate through all commit dates
		for(int i=0; i<time.length; i++) {
			Calendar testtime = GregorianCalendar.getInstance();
			testtime.setTime(time[i]);
			 if(testtime.before(enddate) && testtime.after(newdate)) {
				 rstring = "Time: " + time[i].toString() + ", message: " + commits[i] + "\n";
				 searchNameList.add(name[i]);
				 items.add(rstring);
			 } else if (testtime.before(newdate) && ct) { //also include last date before the selected time
				 x = i;
				 ct = false;
			 }
		 }
		 
		 if (items.isEmpty()) {
			 if (!ct) {
				 rstring = "Name: " + name[x] + ", time: " + time[x].toString() + ", message: " + commits[x] + "\n";
				 items.add(rstring);
			 } else {
				 items.add("No commits found for this date");
			 }
		 } 
		 
		 return items;
	 }

	//returns a list of all commits whose messages contain the provided string
	 public List<String> searchCommits(String sstring) {
		 List<String> items = new ArrayList<>();
		 searchNameList = new ArrayList<String>();
		 String rstring = "";
		 //iterate through all commit messages
		 for(int i=0; i<commits.length; i++) {
			 if(commits[i].toLowerCase().contains(sstring.toLowerCase())) {
				 rstring = "Time: " + time[i].toString() + ", message: " + commits[i] + "\n";
				 items.add(rstring);
				 searchNameList.add(name[i]);
			 }
		 }
		 if(rstring.length() == 0) {
			 rstring = "No messages found";
			 items.add(rstring);
		 }
		 return items;
	 }
}
