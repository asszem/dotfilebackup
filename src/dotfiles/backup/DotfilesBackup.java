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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
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
		try (BufferedReader bufread = new BufferedReader(Files.newBufferedReader(testRun ? testSettingsFile : settingsFile, Charset.forName(charset)));) {
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
			String[][] filesToBackup = new String[settingsRead.size() / 4][2];
			int currentFileCounter = 0;
			for (int i = 0; i < filesToBackup.length; i++) {
				filesToBackup[i][SOURCE_FILE_INDEX] = settingsRead.get(++currentFileCounter);
				filesToBackup[i][TARGET_FILE_INDEX] = settingsRead.get(++currentFileCounter);
				currentFileCounter += 2;
			}
			return filesToBackup;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String[][] getFilesToBackupHardcoded(boolean testRun) {
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
//		String[][] filesToBackup = getFilesToBackupHardcoded(testRun);
		String[][] filesToBackup = readSettingsFile(testRun);
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
				boolean isSourceNewer = false;
				Files.createDirectories(currentTargetPath.getParent()); //Create parent directory for target
				if (Files.notExists(currentTargetPath)) {
					Files.createFile(currentTargetPath); //Create the target file
					isSourceNewer = true; //when the backup file is first created, consider the source newer
				} else //If target file already exists, check the last modification date		
				// source Timestamp LATER than Target timestamp > 0
				/*0 if this FileTime is equal to other, 
					a value less than 0 if this FileTime represents a time that is before other,
					and a value greater than 0 if this FileTime represents a time that is after other*/ {
					if (getFileLastModifiedTime(currentSourcePath).compareTo(getFileLastModifiedTime(currentTargetPath)) > 0) {
						isSourceNewer = true;
					}
				}
				if (isSourceNewer) {
					//Copy source file to target file, but only if different
					Files.copy(currentSourcePath, currentTargetPath, REPLACE_EXISTING);
					//Append message to first line in target file
					appendMessageToFirstLine(currentTargetPath, "This is a line appended, just to be overwritten");
					overwriteMessageToFirstLine(currentTargetPath, "Last updated: " + getDate());
				}
				String sourceModifiedIndicator = isSourceNewer ? "*" : "";
				String targetModifiedIndicator = isSourceNewer ? "" : "*";
				/*
				System.out.printf("[%s]%n\tsource=%s%n\ttarget=%s%n", currentTargetPath.getFileName(), currentSourcePath, currentTargetPath);
				System.out.printf("\tsource modified=%s%s%n\ttarget modified=%s%s%n", convertFileModifiedTime(getFileLastModifiedTime(currentSourcePath)), sourceModifiedIndicator, convertFileModifiedTime(getFileLastModifiedTime(currentTargetPath)), targetModifiedIndicator);
				 */

				System.out.printf("[%s]%n\tSource file%n\t\t%s%n\t\tLast modified:%s%s%n", currentSourcePath.getFileName(), currentSourcePath, convertFileModifiedTime(getFileLastModifiedTime(currentSourcePath)), sourceModifiedIndicator);
				System.out.printf("\tTarget file%n\t\t%s%n\t\tLast modified:%s%s%n", currentTargetPath, convertFileModifiedTime(getFileLastModifiedTime(currentTargetPath)), targetModifiedIndicator);
				System.out.println(isSourceNewer ? "\tFile written" : "\tFile skipped (no modification)");
				System.out.println("");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("\nBackup completed. Don't forget to commit!");
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

	public static FileTime getFileLastModifiedTime(Path file) throws IOException {
		BasicFileAttributes basicFileAttrs = Files.readAttributes(file, BasicFileAttributes.class);
		return basicFileAttrs.lastModifiedTime();
//		System.out.println(lastModified.toString());
	}

	public static String convertFileModifiedTime(FileTime fileTime) {
		StringBuilder strB = new StringBuilder();
		strB.append(fileTime.toString().substring(0, 10)).append(" ").append(fileTime.toString().substring(11, 19));
		return strB.toString();
	}

	public static void main(String[] args) {
		System.out.println("Dot Files Backup - Version 2.0");
		boolean keepRunning = true;
		while (keepRunning) {
			Scanner in = new Scanner(System.in);
			System.out.println("[rp]\t- run production backup\n[rt]\t- run test backup\n[lps]\t- list production settings\n[lts]\t- list test settings\n[q]\t- quit");
			System.out.print("Enter your command: ");
			boolean testRun = false;
			switch (in.next().toLowerCase()) {
				case "q":
					keepRunning = false;
					break;
				case "rt":
					testRun = true;
				case "rp":
				case "r":
					runBackup(testRun);
					keepRunning=false;
					break;
				case "lts": //read the settings from TestRun
					testRun = true;
				case "lps": //read the settings from ProductionRun
					System.out.println("Dotfiles to backup:");
					String[][] settingsRead = readSettingsFile(testRun);
					if (settingsRead == null) {
						System.out.println("Error with settings file");
					} else {
						for (String[] currentFile : settingsRead) {
							System.out.printf("%s >>>>>%n", currentFile[SOURCE_FILE_INDEX].substring(currentFile[SOURCE_FILE_INDEX].lastIndexOf("\\") + 1));
							System.out.printf(" Source file=%s%n Target file=%s%n", currentFile[SOURCE_FILE_INDEX], currentFile[TARGET_FILE_INDEX]);

						}
						System.out.println("");
					}
					break;
			}
		}
	}

}
