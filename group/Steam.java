package group;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Steam {

	
	// TODO public removeInvalidCharAndSpaces
	
	private File pathToStopWords;
	private HashSet<String> stopWordsHash;
	private HashSet<Character> exceptionSuffixRes;
	private HashSet<Character> vowels = new HashSet<Character>(5);
	private HashSet<Character> specialVowels = new HashSet<Character>(3);
	private HashSet<Character> accentuatedVowels = new HashSet<Character>(5);
	private HashSet<Character> consonants = new HashSet<Character>(23);
	
	
	private static final String[] tempCountryPropperName = {"cort",
		"ingl", "franc", "irland", "dublin", "portugu",
		"luxemburgu", "holand", "dan", "finland", "fin", "taiwan",
		"japon", "sudan", "leon", "vien", "cordob", "malt",
		"gabon", "ghan", "ugand", "ruand", "ceiland",};
	private static final HashSet<String> exceptCountryPropperName = new HashSet<String>(Arrays.asList(tempCountryPropperName));
	private static final String[] tempFrenchWords = {"carn", "ball", "t", "caf", "chal", "beb", "bid", 
		"macram","carn", "ball" , "t" , "caf",
				"chal" , "beb", "bid","macram"};
	private static final HashSet<String> exceptFrenchWords = new HashSet<String>(Arrays.asList(tempFrenchWords));
	
	
	/*
	Root Conditions
	Ends_in_Vowel : the root ends in a vowel
	Ends_in_Consonant: the root ends in a consonant
	Ends_in_specVowel: he root ends in ‘a’ ‘e’ or ‘i’
	hasVowel: the root has more than one letter AND contains at least one vowel
	hasVowel: word has a vowel
	exceptGen: exceptions for suffices matching “es” that cover proper names of
	              countries. The root of the word must not match
	"cort",
	"ingl", "franc", "irland", "dublin", "portugu",
	- 21 -"luxemburgu", "holand", "dan", "finland", "fin", "taiwan",
	"japon", "sudan", "leon", "vien", "cordob", "malt",
	"gabon", "ghan", "ugand", "ruand", "ceiland",
	exceptFran:
	exceptions for certain words whose suffices end in “é” (and which for
	the most part derive from French words). The root of such words must
	not match any of the following:
	"carn", "ball", "t", "caf", "chal", "beb", "bid", "macram",
	“carn” (for carné from the French carnet), “ball” (for ballé
	from the French ballet), “t” (for Vermut), “caf” (for café),
	“chal” (for chalé), “beb” (for bebé), “bid” (for bidé from the
	French bidet), and “macram” (for “macramé”).
	Root Modifier Rules
	putOrRemoveStress: if the root contains an accentuated vowel then remove the
	                  accent, otherwise place an accent on the rightmost vowel found
	RemoveStress: removethe accentuated vowel found in the root.
*/
	
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
		exceptionSuffixRes.add('t');	// ends in tres; rupestres
		exceptionSuffixRes.add('p');	// ends in pres; compres 
		exceptionSuffixRes.add('c');	// ends in cres; mediocres
		exceptionSuffixRes.add('b');	// ends in bres; hombres
		exceptionSuffixRes.add('g');	// ends in gres; alegres
		exceptionSuffixRes.add('d');	// ends in dres; padres
		exceptionSuffixRes.add('r');	// ends in rres; torres
		
		// Dont know if I should add accentuated vowels
		vowels.add('a');
		vowels.add('e');
		vowels.add('i');
		vowels.add('o');
		vowels.add('u');
		
		/*vowels.add('á');
		vowels.add('é');
		vowels.add('í');
		vowels.add('ó');
		vowels.add('ú');*/
		
		specialVowels.add('a');
		specialVowels.add('e');
		specialVowels.add('i');
		
		/*specialVowels.add('á');
		specialVowels.add('é');
		specialVowels.add('í');*/
		
		accentuatedVowels.add('á');
		accentuatedVowels.add('é');
		accentuatedVowels.add('í');
		accentuatedVowels.add('ó');
		accentuatedVowels.add('ú');		
		
		consonants.add('b');
		consonants.add('c');
		consonants.add('d');
		consonants.add('f');
		consonants.add('g');
		consonants.add('h');
		consonants.add('j');
		consonants.add('k');
		consonants.add('l');
		consonants.add('m');
		consonants.add('n');
		consonants.add('ñ');
		consonants.add('p');
		consonants.add('q');
		consonants.add('r');
		consonants.add('s');
		consonants.add('t');
		consonants.add('u');
		consonants.add('v');
		consonants.add('w');
		consonants.add('x');
		consonants.add('y');
		consonants.add('z');
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
		return string;
	}
	
	public String removePlurals(String string){
		/**
		 * TODO
		 * 
		 * This removes plurals based on a set of rules.
		 * 
		 * @param A string in lower case
		 * @return The string in singular
		 */
		
		//TODO Check all exceptions first.
		string = string.trim();
		
		if (!string.endsWith("s"))
			return string;
		
		if(string.endsWith("es")){
			// Only the last two conditions have exceptions
			
			// bambúes --> bambú
			if (string.endsWith("úes"))
				return string.substring(0, string.length()-2);
			
			// autobúses --> autobús
			if(string.endsWith("uses"))
				return string.substring(0,string.length()-4) + "ús";
			
			// intereses --> interés
			if(string.endsWith("eses"))
				return string.substring(0, string.length()-4) + "és";
			
			// actividades --> actividad
			if(string.endsWith("des"))
				return string.substring(0, string.length()-2);
			
			// señores -- > señor
			if(string.endsWith("res") && !exceptionSuffixRes.contains(string.charAt(string.length()-4)))
				return string.substring(0, string.length()-2);
			
			// TODO needs to check the condition "root ends with vowel"
			// frases --> f
			if(string.endsWith("ses") || string.endsWith("nes"))
				return string.substring(0, string.length()-1);
			
			// TODO match suffix n/s and root has vowels
			// narices --> nariz, sauces --> sauz
			if(string.endsWith("ces") && !string.endsWith("auces"))
				return string.substring(0, string.length()-2);
			
		}
				
		// else just return the string without s
		return string.substring(0,string.length()-1);
		
	}

}
