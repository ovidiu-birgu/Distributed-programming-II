package it.polito.dp2.RNS.debug;

import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;

public class TestMain {
	private final static String INPUT_XML_FILE = "it.polito.dp2.RNS.sol1.RnsInfo.file";
	private final static String READER_FACTORY_NAME = "it.polito.dp2.RNS.RnsReaderFactory";
	private final static String READER_FACTORY_VALUE = "it.polito.dp2.RNS.sol1.RnsReaderFactory";

	
	public static void main(String[] args) {
        // Create implementation under test       
        System.setProperty(READER_FACTORY_NAME, READER_FACTORY_VALUE);
        System.setProperty(INPUT_XML_FILE, "out100.xml");

        try {
			RnsReader testRnsReader = RnsReaderFactory.newInstance().newRnsReader();
			
			testRnsReader.getPlaces(null);
			
		} catch (RnsReaderException | FactoryConfigurationError e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        System.out.println("ok");
	}
}
