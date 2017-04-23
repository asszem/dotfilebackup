package dotfiles.backup;

import java.nio.file.Files;
import java.nio.file.Paths;
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

	public DotfilesBackupTest() {
	}

	@BeforeClass
	public static void setUpClass() {
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
	 * Test of listTargets method, of class DotfilesBackup.
	 */
	@Ignore
	@Test
	public void testListTargets() {
		System.out.println("listTargets");
		DotfilesBackup.listTargets();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of listSources method, of class DotfilesBackup.
	 */
	@Ignore
	@Test
	public void testListSources() {
		System.out.println("listSources");
		DotfilesBackup.listSources();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getFilesToBackup method, of class DotfilesBackup.
	 */
	@Ignore
	@Test
	public void testGetFilesToBackup() {
		System.out.println("getFilesToBackup");
		String[][] expResult = null;
		String[][] result = DotfilesBackup.getFilesToBackup();
		assertArrayEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of runBackup method, of class DotfilesBackup.
	 */
	@Test
	public void testRunBackup() {
		System.out.println("runBackup");
		String[][] filesToBackup = DotfilesBackup.getFilesToBackup();
		DotfilesBackup.runBackup();
		for (int i = 0; i < filesToBackup.length; i++) {
			System.out.printf("[%s] test...", filesToBackup[i][1]);
			assertTrue(Files.exists(Paths.get(filesToBackup[i][1])));
			System.out.println("OK");
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
