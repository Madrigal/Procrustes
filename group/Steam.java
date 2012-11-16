package group;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Steam {

	
	// TODO public removeInvalidCharAndSpaces
	
	private File pathToStopWords;
	private HashSet<String> stopWordsHash;
	private HashSet<Character> exceptionSuffixRes;
	
	public boolean setStopWords(String pathToFile){
		/**
		 * Sets the private path to the stop words
		 * 
		 * @param The path to the file with stop words
		 * @return True if the path was set, false otherwise
		 */
		
		File file = new File(pathToFile);
		if(!file.canRead())
			return false;
		pathToStopWords = file;
		stopWordsHash = new HashSet<String>();
		Scanner scanner;
		
		try {
			scanner = new Scanner(pathToStopWords);
			while(scanner.hasNextLine()){
				stopWordsHash.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			
			System.err.println("The specified path " + pathToStopWords + " doesn't exist");
			System.err.println("Maybe you can reset it with setStopWords");
			e.printStackTrace();
			return false;
		}
		
		scanner.close();
		return true;
	}
	
	public String removeStopWords(String string){
		/**
		 * This method does the following steps:
		 * 1.- Split the word
		 * 2.- Iterate each word, and if it has a
		 * 	   stop word, remove it in-place
		 * 3.- Return the array.
		 * 
		 * @param The pure string
		 * @return An array with the stop words removed 
		 */
		string = string.toLowerCase();
		
		// TODO remove invalid characters in UTF-8. Maybe try to fix them.
		
		String[] temp = string.split(" ");
		string = "";
		for(int i = 0; i < temp.length; i++){
			if(!stopWordsHash.contains(temp[i]))
				string += temp[i] + " ";
		}
		
		return string;
	}
	
	public Steam(String pathToStopWords){
		setStopWords(pathToStopWords);
		exceptionSuffixRes = new HashSet<Character>();
		exceptionSuffixRes.add('t');	// ends in tres
		exceptionSuffixRes.add('p');	// ends in pres
		exceptionSuffixRes.add('c');
		exceptionSuffixRes.add('b');
		exceptionSuffixRes.add('g');
		exceptionSuffixRes.add('d');
		exceptionSuffixRes.add('g');
		exceptionSuffixRes.add('r');
	}
	
	public String removeInvalidCharacters(String string){
		/**
		 * TODO
		 * Removes all the characters that are invalid in the
		 * UTF-8 character set. Maybe later it could try to
		 * fix them, like ű
		 * 
		 * @param A string to remove the invalid characters
		 * @return The string with the invalid characters removed
		 */
		
		String a = "";
		return a;
	}
	
	public String removePlurals(String string){
		/**
		 * TODO
		 * 
		 * This removes plurals based on a set of rules.
		 * 
		 * @param A string to remove plurals
		 * @return The string in singular
		 */
		
		//TODO Check all exceptions first.
		string = string.trim();
		
		if (!string.endsWith("s"))
			return string;
		
		if(string.endsWith("es")){
			
			if (string.endsWith("úes"))
				return string.substring(0, string.length()-2);
			
			// Example
			if(string.endsWith("uses"))
				return string.substring(0,string.length()-4) + "ús";
			
			// intereses --> interés
			if(string.endsWith("eses"))
				return string.substring(0, string.length()-4) + "és";
			
			// TODO needs to check the condition "root ends with vowel"
			// clases --> clase
			if(string.endsWith("ses"))
				return string.substring(0, string.length()-1);
			
			// actividades --> actividad
			if(string.endsWith("des"))
				return string.substring(0, string.length()-2);
			
			if(string.endsWith("res") && !exceptionSuffixRes.contains(string.charAt(string.length()-4)))
				return string.substring(0, string.length()-2);
			
			if(string.endsWith("ces") && !string.endsWith("auces"))
				return string.substring(0, string.length()-2);
			
		}
				
		return string.substring(0,string.length()-1);
		
	}
	
	private boolean endsOnlyWith(String word, String suffix){
		if(word.length() == 0 || suffix.length() == 0)
			return false;
		
		
		return false;
	}
	
	
	

}
