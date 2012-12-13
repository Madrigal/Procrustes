package group;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public abstract class TemplateVerbDictionary {

	// Used to map a word to a stem
	private HashMap<String, String> wordToStem;

	/**
	 * The param for this method is the name of the txt file without the 
	 * extension, so the txt can load regardless of the absolute path
	 * in host machine.
	 * If the package were to change name, so this method.
	 * 
	 * @param dictionaryName The name of the file without extensions, like
	 * "dict"
	 */
	public TemplateVerbDictionary(String dictionaryName){
		String separator = System.getProperty("file.separator");
		String path = separator + "docs" + separator + dictionaryName + ".txt" ;
		System.out.println(path);
		InputStream in = this.getClass().getResourceAsStream(path);

		wordToStem = new HashMap<String, String>();
		Scanner scanner;

		scanner = new Scanner(in);
		while(scanner.hasNextLine()){
			String temp = scanner.nextLine();
			String[] split = temp.split(" ");
			wordToStem.put(split[0], split[1]);
		}

		scanner.close();

		System.out.println("Dictionary set. Size: " + wordToStem.size());

	}
	
	/**
	 * Looks in the dictionary for the word. 
	 * @param word The word to lookup
	 * @return The stem associated to it, or the same word if none is found.
	 */
	public String lookup(String word){
		String candidate = wordToStem.get(word);
		if (candidate == null)
			return word;
		return candidate;
	}
}
