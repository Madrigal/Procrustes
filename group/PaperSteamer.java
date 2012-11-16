package group;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class PaperSteamer {

	private HashSet<Character> vowels = new HashSet<Character>();
	private HashSet<Character> specialVowels = new HashSet<Character>();
	private HashSet<Character> consonants = new HashSet<Character>();
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
	public PaperSteamer(){
		// Dont know if I should add accentuated vowels
		vowels.add('a');
		vowels.add('e');
		vowels.add('i');
		vowels.add('o');
		vowels.add('u');
		
		specialVowels.add('a');
		specialVowels.add('e');
		specialVowels.add('i');
		
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
	
	private boolean checkExceptions(String word){
		
		return false;
	}
}
