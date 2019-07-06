package it.polito.dp2.RNS.sol2;
/**
 * This class is used for testing the lab2 solution library
 */

import java.util.List;
import java.util.Set;

import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.ModelException;
import it.polito.dp2.RNS.lab2.PathFinderException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;

public class TestLibrary {
	private final static String BASE_URL = "it.polito.dp2.RNS.lab2.URL";
	private final static String READER_FACTORY_NAME = "it.polito.dp2.RNS.RnsReaderFactory";
	private final static String READER_FACTORY_VALUE = "it.polito.dp2.RNS.Random.RnsReaderFactoryImpl";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {			
			System.setProperty(BASE_URL, "http://localhost:7474/db");
	        // Create implementation under test       
	        System.setProperty(READER_FACTORY_NAME, READER_FACTORY_VALUE);
	        System.setProperty("it.polito.dp2.RNS.Random.seed", "77777");
	        System.setProperty("it.polito.dp2.RNS.Random.testcase", "2");
	        
			PathFinderImpl pathFinder = new PathFinderImpl();
			if(!pathFinder.isModelLoaded())
				pathFinder.reloadModel();

			String source = "Gate4";
			String destination = "Gate8";
			int maxdepth = -5;
			
			Set<List<String>> paths = pathFinder.findShortestPaths(source, destination, maxdepth);

			for(List<String> p: paths) {
				System.out.print("Path from "+source+" to "+destination+": ");
				for(String s: p)
					System.out.print(s + " ");
				System.out.println();
			}
			System.out.println("---");
			
			System.setProperty("it.polito.dp2.RNS.Random.seed", "2167182");
			pathFinder.reloadModel();
						
			source = "Gate0";
			destination = "Gate5";	
			maxdepth = 11;

			paths.clear();
			paths = pathFinder.findShortestPaths(source, destination, maxdepth);
			
			for(List<String> p: paths) {
				System.out.print("Path from "+source+" to "+destination+": ");
				for(String s: p)
					System.out.print(s + " ");
				System.out.println();
			}
			
			// look at graph in:
			// http://localhost:7474/browser/
			// query:
			// MATCH p=()-->() RETURN p LIMIT 299

			System.out.println("ok");
		} catch (ServiceException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ModelException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (PathFinderException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (UnknownIdException e) {
			e.printStackTrace();
			System.exit(4);			
		} catch (BadStateException e) {
			e.printStackTrace();
			System.exit(5);
		}
	}

}
