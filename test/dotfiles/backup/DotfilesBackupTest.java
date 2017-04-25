package dotfiles.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andras Olah (olahandras78@gmail.com)
 */
public class DotfilesBackupTest {
	
//	public static String[][] testFiles =new String[1][2];
	public static String charset ="UTF-8";
	public final boolean TESTRUN=true;
	public static Path testSettingsFile = Paths.get("J:/dotfiles/dotfilebackupSettings-test.txt");

	public DotfilesBackupTest() {
	}

	@BeforeClass
	public static void setUpClass() {
//		testFiles[0][0] = "J:/dotfites/test/sourcefile.txt";
//		testFiles[0][1] = "J:/dotfiles/test/targetfile.txt";
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
		String[][] filesToBackup = DotfilesBackup.readSettingsFile(TESTRUN);
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
		String[][] filesToBackup = DotfilesBackup.readSettingsFile(TESTRUN);
		Path targetFileToAppend=Paths.get(filesToBackup[0][1]);
		String expMessageToFirstLine="Árvíztűrú tükörfúrógép " + ((int)(Math.random()*100));
		DotfilesBackup.appendMessageToFirstLine(targetFileToAppend, expMessageToFirstLine);
		try (BufferedReader bufread =new BufferedReader(Files.newBufferedReader(targetFileToAppend, Charset.forName(charset)));){
			//read the file and verify if it has the message
			String actualFirstLine =bufread.readLine();
			assertEquals(expMessageToFirstLine, actualFirstLine);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}


}
