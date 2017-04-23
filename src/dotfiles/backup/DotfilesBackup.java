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

	public static void listTargets() {

	}

	public static void listSources() {

	}

	public static String[][] getFilesToBackup() {
//[source]=J:\dotfiles\test\sourcefile.txt[target]=J:\dotfiles\test\target file.txt
		String[][] filesToBackup = new String[1][2];
		filesToBackup[0][SOURCE_FILE_INDEX] = "J:/dotfiles/test/sourcefile.txt";
		filesToBackup[0][TARGET_FILE_INDEX] = "J:/dotfiles/test/targetfile.txt";
		return filesToBackup;
	}

	public static void runBackup() {
		String[][] filesToBackup = getFilesToBackup();
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
				overwriteMessageToFirstLine(currentTargetPath, "Last updated: "+ getDate());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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
			System.out.println("(R)un backup (L)ist sources (T)argets (A)dd (Q)uit");
			switch (in.next().toLowerCase()) {
				case "q":
					keepRunning = false;
					break;
				case "l":
					String[][] filesToBackup = getFilesToBackup();
					for (String[] currentFile : filesToBackup) {
						System.out.printf("Source file=%n%s%nTarget file=%n%s%n", currentFile[SOURCE_FILE_INDEX], currentFile[TARGET_FILE_INDEX]);
					}
				case "r":
					runBackup();
					break;
			}
		}
	}

}
