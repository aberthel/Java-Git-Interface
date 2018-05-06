# Java-Git-Interface

Author: Ana Berthel

Last Updated: May 5, 2018

Java-based graphical interface for interacting with a Git repository. 
Functionality includes initializing and opening a repository, adding and committing files, 
and searching and diffing files in two commits.

Required packages:

jdiff

org.eclipse.jgit-4.9.0.201710071750-r

slf4j-api-1.7.25

slf4j-simple-1.7.25

Files:

BaseMain.java:
  Main file for interface.
  
ButtonPanel.java:
  Main button interface. Buttons navigate to frames which have different features.
  
CommitBox.java:
  Frame confirms files to be committed and prompts user for commit message.
  
CommitDateFrame.java:
  Frame allows user to search commits by date and diff selected commit with latest commit.
  
CommitMessageFrame.java:
  Frame allows user to search commits by commit message and diff selected commit with latest commit.
  
DiffFrame.java:
  Frame allows user to select and diff files from two commits.
  
RepoBase.java:
  This class handles the repository: accessing and listing commits/files as requested.
  Methods so marked include code modified from JGitCookbook.
  Found at: https://github.com/centic9/jgit-cookbook
  
RepoDisplayFrame.java:
  Frame allows user to view all commits in repository and diff selected commit with latest commit.
