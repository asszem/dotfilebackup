package dotfiles.backup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import static java.nio.file.StandardCopyOption.*;
import java.util.Scanner;

public class DotfilesBackup {

	public static final int SOURCE_FILE_INDEX = 0;
	public static final int TARGET_FILE_INDEX = 1;

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
				//Append message to first line in target file
				//Copy source file to target file
				Files.copy(currentSourcePath, currentTargetPath, REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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
