package dotfiles.backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import static java.nio.file.StandardCopyOption.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DotfilesBackup {

	public static final int SOURCE_FILE_INDEX = 0;
	public static final int TARGET_FILE_INDEX = 1;
	public static String charset = "UTF-8";
	public static Path settingsFile = Paths.get(System.getProperty("user.home")).resolve("dotfilebackupSettings.txt");
	public static Path testSettingsFile = Paths.get("J:/dotfiles/dotfilebackupSettings-test.txt");

	public static String[][] readSettingsFile(boolean testRun) {
		//Settings file structure
		//**Start of <filename>
		//source path
		//target path
		//**End of <filename>
		//Validate if Settings file has even lines
		if (Files.notExists(testRun ? testSettingsFile : settingsFile)) {
			System.out.println("Settings file not found.");
			return null;
		}
		ArrayList<String> settingsRead = new ArrayList<>();
		try {
			BufferedReader bufread = new BufferedReader(Files.newBufferedReader(testRun ? testSettingsFile : settingsFile, Charset.forName(charset)));
			String currentLine;
			//Read all content to the arraylist of settngsRead
			while ((currentLine = bufread.readLine()) != null) {
				settingsRead.add(currentLine);
			}
			if (settingsRead.size() % 4 != 0) {
				System.out.println("Settings file corrupted!");
				return null;
			}
			//Create new array based on the settingsRead array. Each file has 4 rows in the settings file
			String[][] filesToBackup =new String[settingsRead.size()/4][2];	
			int currentFileCounter=0;
			for (int i=0;i<filesToBackup.length;i++){
			filesToBackup[i][SOURCE_FILE_INDEX]=settingsRead.get(++currentFileCounter);
			filesToBackup[i][TARGET_FILE_INDEX]=settingsRead.get(++currentFileCounter);
			currentFileCounter+=2;
			}
			return filesToBackup;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void addToSettingsFile(boolean testRun) {

	}

	public static String[][] getFilesToBackup(boolean testRun) {
//[source]=J:\dotfiles\test\sourcefile.txt[target]=J:\dotfiles\test\target file.txt
		if (testRun) { //Hardcoded test files only on drive J
			String[][] filesToBackup = new String[2][2];
			filesToBackup[0][SOURCE_FILE_INDEX] = "J:/dotfiles/test/sourcefile.txt";
			filesToBackup[0][TARGET_FILE_INDEX] = "J:/dotfiles/test/targetfile.txt";
			filesToBackup[1][SOURCE_FILE_INDEX] = "J:/dotfiles/test/sourcefile.txt";
			filesToBackup[1][TARGET_FILE_INDEX] = "J:/dotfiles/test/targetfileSecond.txt";
			return filesToBackup;
		} else { //hardcoded production files on Desktop
			String[][] filesToBackup = new String[3][2];
			String homeDir = Paths.get(System.getProperty("user.home")).toString();
			filesToBackup[0][SOURCE_FILE_INDEX] = homeDir + "\\_vimrc";
			filesToBackup[0][TARGET_FILE_INDEX] = "J:/dotfiles/test/vimrc";
			filesToBackup[1][SOURCE_FILE_INDEX] = homeDir + "\\.gitconfig";
			filesToBackup[1][TARGET_FILE_INDEX] = "J:/dotfiles/test/.gitconfig";
			filesToBackup[2][SOURCE_FILE_INDEX] = homeDir + "\\Documents\\WindowsPowerShell\\Microsoft.PowerShell_profile.ps1";
			filesToBackup[2][TARGET_FILE_INDEX] = "J:/dotfiles/test/Microsoft.PowerShell_profile.ps1";
			return filesToBackup;
		}
	}

	public static void runBackup(boolean testRun) {
		String[][] filesToBackup = getFilesToBackup(testRun);
		for (String[] currentFile : filesToBackup) {
			Path currentSourcePath = Paths.get(currentFile[SOURCE_FILE_INDEX]);
			Path currentTargetPath = Paths.get(currentFile[TARGET_FILE_INDEX]);
			//Verify whether the source file exists. 
			if (Files.notExists(currentSourcePath)) {
				System.out.println(currentSourcePath);
				System.out.println("Source file does not exists.");
				continue;
			}
			try {
				//Create the parent() directory for the target file
				Files.createDirectories(currentTargetPath.getParent());
				//Create the file if doesn't exist
				if (Files.notExists(currentTargetPath)) {
					Files.createFile(currentTargetPath);
				}
				//Copy source file to target file
				Files.copy(currentSourcePath, currentTargetPath, REPLACE_EXISTING);
				//Append message to first line in target file
				appendMessageToFirstLine(currentTargetPath, "This is a line appended, just to be overwritten");
				overwriteMessageToFirstLine(currentTargetPath, "Last updated: " + getDate());
				System.out.printf("file=%s%n\tsource=%s%n\ttarget=%s%n\t...OK%n", currentTargetPath.getFileName(), currentSourcePath, currentTargetPath);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Backup completed.");
	}

	public static void appendMessageToFirstLine(Path targetFile, String message) {
		try {
			//read the content of the current file
			BufferedReader bufreader = new BufferedReader(Files.newBufferedReader(targetFile, Charset.forName(charset)));
			StringBuilder oldFileContent = new StringBuilder();
			String currentLine;
			while ((currentLine = bufreader.readLine()) != null) {
				oldFileContent.append(currentLine).append(System.lineSeparator());
			}
			bufreader.close();
			String newFileContent = message + System.lineSeparator() + oldFileContent.toString();
			//Open the target file to append the message
			BufferedWriter bufwriter = new BufferedWriter(Files.newBufferedWriter(targetFile, WRITE));
			bufwriter.write(newFileContent);
			bufwriter.flush();
			bufwriter.close();
			//append the message to the first position
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void overwriteMessageToFirstLine(Path targetFile, String message) {
		try {
			//read the content of the current file
			BufferedReader bufreader = new BufferedReader(Files.newBufferedReader(targetFile, Charset.forName(charset)));
			StringBuilder newFileContent = new StringBuilder();
			String currentLine;
			boolean firstLine = true;
			while ((currentLine = bufreader.readLine()) != null) {
				//Do not add the first line to the file to the newFileCounter. So it will be overwritten
				if (firstLine == true) {
					newFileContent.append(message).append(System.lineSeparator());
					firstLine = false;
				} else {
					newFileContent.append(currentLine).append(System.lineSeparator());
				}
			}
			bufreader.close();
			//Open the target file to append the message

			BufferedWriter bufwriter = new BufferedWriter(Files.newBufferedWriter(targetFile, WRITE, TRUNCATE_EXISTING));
			bufwriter.write(newFileContent.toString());
			bufwriter.flush();
			bufwriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
//		System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		return dateFormat.format(date);
	}

	public static void addDotfile() {

	}

	public static void main(String[] args) {
		boolean keepRunning = true;
		while (keepRunning) {
			Scanner in = new Scanner(System.in);
			System.out.println("Version 1.0");
			System.out.println("[rp] - run production backup\n[rt] - run test backup\n[lt] - list test files\n[lp] - list production files\n[a] - add files\n[st] - list test settings\n[sp] - list production settings\n[q] - quit");
			System.out.print("Enter your command: ");
			boolean testRun = false;
			switch (in.next().toLowerCase()) {
				case "q":
					keepRunning = false;
					break;
				case "lt": //list test run files
					testRun = true; //there is no break, continue with LP to print
				case "lp":
					String[][] filesToBackup = getFilesToBackup(testRun);
					for (String[] currentFile : filesToBackup) {
						System.out.printf("Source file=%n%s%nTarget file=%n%s%n", currentFile[SOURCE_FILE_INDEX], currentFile[TARGET_FILE_INDEX]);
					}
					break;
				case "rt":
					testRun = true;
				case "rp":
				case "r":
					runBackup(testRun);
					break;
				case "st": //read the settings from TestRun
					testRun = true;
				case "sp": //read the settings from ProductionRun
					System.out.println("Settings file content. Test run:" + testRun);
					String[][] settingsRead = readSettingsFile(testRun);
					if (settingsRead == null) {
						System.out.println("Error with settings file");
					} else {
						for (String[] currentFile : settingsRead) {
							System.out.printf("Source file=%n%s%nTarget file=%n%s%n", currentFile[SOURCE_FILE_INDEX], currentFile[TARGET_FILE_INDEX]);
						}
					}
					break;
			}
		}
	}

}
