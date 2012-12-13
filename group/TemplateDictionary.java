package group;


import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
/**
 * This class is meant to be a template for the I-Dictonary,
 * the Ri-V dictionary, the stop words and ocassionaly for 
 * a Spanish dictionary.  
 * 
 * @author Madrigal
 *
 */
public abstract class TemplateDictionary {

	private HashSet<String> dictionaryHash;

	/**
	 * The param for this method is the name of the txt file without the 
	 * extension, so the txt can load regardless of the absolute path
	 * in host machine.
	 * If the package were to change name, so this method.
	 * 
	 * @param dictionaryName The name of the file without extensions, like
	 * "dict"
	 */
	public TemplateDictionary(String dictionaryName){
		String separator = System.getProperty("file.separator");
		String path = separator + "docs" + separator + dictionaryName + ".txt" ;
		System.out.println(path);
		InputStream in = null;
		dictionaryHash = new HashSet<String>();
		Scanner scanner = null;
		
		
		// TODO fix, because it doesn't work in a JAR
		in = this.getClass().getResourceAsStream(path);
		System.out.println(in);
		scanner = new Scanner(in);
		
		while(scanner.hasNextLine()){
			dictionaryHash.add(scanner.nextLine());
		}

		scanner.close();

		System.out.println("Dictionary set. Size: " + dictionaryHash.size());
	
}

	/**
	 * Trims the word and calls the contains method of the
	 * private HashSet
	 * @param word A word to check if it's in the dictionary
	 * @return True if the word is in the hash set, false otherwise
	 */
	public boolean isInDictionary(String word){
		word = word.trim();
		return dictionaryHash.contains(word);
	}

}
