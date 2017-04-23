package dotfiles.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Andras Olah (olahandras78@gmail.com)
 */
public class DotfilesBackupTest {
	
	public static String[][] testFiles =new String[1][2];
	public static String charset ="UTF-8";
	public final boolean TESTRUN=true;
	
	public DotfilesBackupTest() {
	}

	@BeforeClass
	public static void setUpClass() {
		testFiles[0][0] = "J:/dotfites/test/sourcefile.txt";
		testFiles[0][1] = "J:/dotfiles/test/targetfile.txt";
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}



	/**
	 * Test of runBackup method, of class DotfilesBackup.
	 */
	@Test
	public void testRunBackup() {
		System.out.println("runBackup");
		String[][] filesToBackup = DotfilesBackup.getFilesToBackup(TESTRUN);
		for (int i = 0; i < filesToBackup.length; i++) {
			DotfilesBackup.runBackup(TESTRUN);
			System.out.printf("[%s] test...", filesToBackup[i][1]);
			assertTrue(Files.exists(Paths.get(filesToBackup[i][1])));
			System.out.println("OK");
		}
	}

	@Test
	public void testAppendMessage(){
		System.out.println("appendMessage");
		DotfilesBackup.runBackup(TESTRUN);
		Path targetFileToAppend=Paths.get(testFiles[0][1]);
		String expMessageToFirstLine="Árvíztűrú tükörfúrógép " + ((int)(Math.random()*100));
		DotfilesBackup.appendMessageToFirstLine(targetFileToAppend, expMessageToFirstLine);
		try {
			//read the file and verify if it has the message
			BufferedReader bufread =new BufferedReader(Files.newBufferedReader(targetFileToAppend, Charset.forName(charset)));
			String actualFirstLine =bufread.readLine();
			assertEquals(expMessageToFirstLine, actualFirstLine);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Test of addDotfile method, of class DotfilesBackup.
	 */
	@Ignore
	@Test
	public void testAddDotfile() {
		System.out.println("addDotfile");
		DotfilesBackup.addDotfile();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of main method, of class DotfilesBackup.
	 */
	@Ignore
	@Test
	public void testMain() {
		System.out.println("main");
		String[] args = null;
		DotfilesBackup.main(args);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}
