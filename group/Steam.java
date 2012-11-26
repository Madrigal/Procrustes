package group;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Steam {

	private File pathToStopWords;

	private HashSet<String> stopWordsHash;
	private HashSet<Character> vowels = new HashSet<Character>(5);
	private HashSet<Character> specialVowels = new HashSet<Character>(3);
	private HashSet<Character> accentuatedVowels = new HashSet<Character>(5);
	private HashSet<Character> consonants = new HashSet<Character>(23);
	private HashSet<Character> exceptionSuffixRes;
	private HashSet<Character> exceptionSuffixLes;
	private static HashSet<String> exceptCountryPropperName;
	private static HashSet<String> exceptFrenchWords;
	private HashMap<Character, Character> accentuatedToNormal = new HashMap<Character, Character>(5);
	private HashMap<Character, Character> normalToAccentuated = new HashMap<Character, Character>(5);

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
	RemoveStress: remove the accentuated vowel found in the root.
	 */
	public Steam(String pathToStopWords){
		setStopWordsList(pathToStopWords);
		String[] tempCountryPropperName = {"cort",
				"ingl", "franc", "irland", "dublin", "portugu",
				"luxemburgu", "holand", "dan", "finland", "fin", "taiwan",
				"japon", "sudan", "leon", "vien", "cordob", "malt",
				"gabon", "ghan", "ugand", "ruand", "ceiland",};
		String[] tempFrenchWords = {"carn", "ball", "t", "caf", "chal", "beb", "bid", 
				"macram","carn", "ball" , "t" , "caf",
				"chal" , "beb", "bid","macram"};
		Character[] tempVowels = {'a', 'e','i','o','u'};	
		Character[] tempAccentuated = {'á','é','í','ó','ú'};
		Character[] tempConsonants = {'b','c','d','f','g','h','j','k','l','m','n','ñ',
				'p','q','r','s','t','v','w','x','y','z'};

		Character[] tempSpecialVowels = {'a','e','i'};		// This are not special per se, 
		// but common in special cases
		// for this algorithm

		exceptCountryPropperName = new HashSet<String>(Arrays.asList(tempCountryPropperName));
		exceptFrenchWords = new HashSet<String>(Arrays.asList(tempFrenchWords));

		exceptionSuffixRes = new HashSet<Character>(7);
		exceptionSuffixRes.add('t');	// ends in tres, like rupestres
		exceptionSuffixRes.add('p');	// ends in pres, like compres 
		exceptionSuffixRes.add('c');	// ends in cres, like mediocres
		exceptionSuffixRes.add('b');	// ends in bres, like hombres
		exceptionSuffixRes.add('g');	// ends in gres, like alegres
		exceptionSuffixRes.add('d');	// ends in dres, like padres
		exceptionSuffixRes.add('r');	// ends in rres, like torres

		exceptionSuffixLes = new HashSet<Character>(6);
		exceptionSuffixLes.add('l');	// ends in lles, like detalles
		exceptionSuffixLes.add('p');	// ends in ples, like simples
		exceptionSuffixLes.add('c');	// ends in cles, like bucles
		exceptionSuffixLes.add('b');	// ends in bles, like posibles
		exceptionSuffixLes.add('g');	// ends in gles, can't think in an example
		exceptionSuffixLes.add('f');	// ends in fles, like rifles

		// Dont know if I should add accentuated vowels
		for(Character vowel: tempVowels){
			vowels.add(vowel);
		}

		for(Character specialVowel: tempSpecialVowels){
			specialVowels.add(specialVowel);
		}

		for(Character accentuatedVowel: tempAccentuated){
			accentuatedVowels.add(accentuatedVowel);
		}

		for(Character consonant: tempConsonants){
			consonants.add(consonant);
		}

		accentuatedToNormal.put('á', 'a');
		accentuatedToNormal.put('é', 'e');
		accentuatedToNormal.put('í', 'i');
		accentuatedToNormal.put('ó', 'o');
		accentuatedToNormal.put('ú', 'u');

		normalToAccentuated.put('a', 'á');
		normalToAccentuated.put('e', 'é');
		normalToAccentuated.put('i', 'í');
		normalToAccentuated.put('o', 'ó');
		normalToAccentuated.put('u', 'ú');
	}

	/**
	 * Sets the private path to the stop words
	 * 
	 * @param The path to the file with stop words
	 * @return True if the path was set, false otherwise
	 */
	public boolean setStopWordsList(String pathToFile){

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

		System.out.println("Stop words loaded. Size: " + stopWordsHash.size());

		return true;
	}

	/** 
	 * This is the main access to the steamer. It calls
	 * removeStopWords and removePlurals
	 * 
	 * @param The word to stem
	 * @return The stem associated with the word, and an empty string if it
	 * is a stop word, like prepositions.
	 */
	public String steamWord(String word){
		
		word = word.trim();
		word = word.replaceAll("[^\\p{L}^\\p{M}]", "");
		if(word.isEmpty())
			return word;
		word = removeStopWords(word);
		if(word.isEmpty())
			return word;
		word = changeToSingular(word);
		return word;

	}
	/**
	 * This method does the following steps:
	 * 1.- Split the word
	 * 2.- Iterate each word, and if it is a
	 * 	   stop word, remove it.
	 * 3.- Return the array.
	 * 
	 * @param The pure string
	 * @return An array with the stop words removed 
	 */
	public String removeStopWords(String string){
	
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
	
	/**
	 * This removes plurals based on a set of rules. Note that while most of the
	 * plurals in Spanish end with 's' and are easy to deal with, not all the words
	 * than end with 's' are plurals, and not all are so easy to reduce to the
	 * singular form. So, this method is not guaranteed to remove all plurals 
	 * 
	 * @param  String A string in lower case, UTF-8 coded
	 * @return String The string in singular
	 */
	public String changeToSingular(String word){
		
		//TODO Check all exceptions first.
		word = word.trim();
		word = word.replaceAll("\\p{P}+", "");	// This criptic command removes all punctiation

		if(word.isEmpty() || word.length() < 2)
			return "";
		if (!word.endsWith("s") && !word.endsWith("z"))
			return word;

		if(word.endsWith("es")){
			// Only the last two conditions have exceptions

			// bambúes --> bambú
			if (word.endsWith("úes"))
				return word.substring(0, word.length()-2);

			// actividades --> actividad
			if(word.endsWith("des"))
				return word.substring(0, word.length()-2);

			// autobúses --> autobús
			if(word.endsWith("uses"))
				return word.substring(0,word.length()-4) + "ús";

			// intereses --> interés
			if(word.endsWith("eses"))
				return word.substring(0, word.length()-4) + "és";

			// digitales --> digital
			if(word.endsWith("les") && !exceptionSuffixLes.contains(word.charAt(word.length()-4)))
				return word.substring(0, word.length()-2);

			// señores -- > señor
			if(word.endsWith("res") && !exceptionSuffixRes.contains(word.charAt(word.length()-4)))
				return word.substring(0, word.length()-2);

			/*
			 * If the word ends with "nes" or "ces", it maybe needs to be accentuated.
			 * So, that's what we do here.
			 */

			if(word.endsWith("nes")){
				word = word.substring(0, word.length()-2);
				word = putOrRemoveStress(word);
				return word;
			}
		
			// frases --> frase
			if(word.endsWith("ses") && vowels.contains(word.charAt(word.length()-4)))
				return word.substring(0, word.length()-1);

			// narices --> nariz, sauces --> sauz
			if(word.endsWith("ces") && !word.endsWith("auces"))
				return word.substring(0, word.length()-2);
		}

		if(word.endsWith("z")){

		}

		if(exceptionPlurals(word))
			return word;

		// else just return the string without s
		return word.substring(0,word.length()-1);

	}
	/**
	 * If it contains an accentuated value
	 * removes the accent. Otherwise place an 
	 * accent on the rightmost word found
	 * 
	 *@param A word to modify the stress. Note that
	 *		 it doesn't validate if it needs this change.
	 *
	 *@return The string modified.
	 */
	private String putOrRemoveStress(String word){
	
		int accentuatedPosition = -1;
		int lastVowel = -1;
		for(int i = 0; i < word.length(); i++){
			if( accentuatedVowels.contains( word.charAt(i) ) ){
				// In Spanish there is only one accent in any given word
				accentuatedPosition = i;
				break;
			}
			else{
				if (vowels.contains(word.charAt(i)))
					lastVowel = i;
			}
		}

		if(accentuatedPosition > 0){
			word = word.replace(word.charAt(accentuatedPosition), 
					accentuatedToNormal.get(word.charAt(accentuatedPosition)));
		}
		else{

			if(lastVowel < 0)			// Which means the word doesn't have any vowel, and is invalid
				return "";
			else{
				// TODO Efficientate this
				char[] temp = word.toCharArray();
				temp[lastVowel] = normalToAccentuated.get(temp[lastVowel]);
				word = "";
				for(int i = 0; i < temp.length; i++){
					word += temp[i];
				}
			}
		}
		return word;
	}

	/**
	 * TODO
	 * This is a transitory state. 
	 * To remove plurals, we need to check if the word ends with 's'. 
	 * However, this deals with words that although are not plural, need
	 * to be changed by the algorithm. Most of the exceptions are 
	 * "españolismos", words that are only used in the spanish from Spain,
	 * as contrast with latin america.
	 * 
	 * As time runs low to deliver, this just checks the exceptions.
	 */
	private boolean exceptionPlurals(String word){

		if(word.endsWith("ríais"))
			return true;

		if(word.endsWith("ríeis"))
			return true;

		if(word.endsWith("aseis"))
			return true;

		if(word.endsWith("asteis"))
			return true;

		if(word.endsWith("ierais"))
			return true;

		if(word.endsWith("ieseis"))
			return true;

		if(word.endsWith("isteis"))
			return true;

		if(word.endsWith("yerais"))
			return true;

		if(word.endsWith("yeseis"))
			return true;

		return false;
	}
	private boolean exceptGen(String word){
		if(exceptCountryPropperName.contains(word))
			return true;
		return false;
	}

	private String removeStress(String word){
		for (Character vowel: vowels) {
			word = word.replace(vowel, normalToAccentuated.get(vowel));
		}
		return word;
	}

}
