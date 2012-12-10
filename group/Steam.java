package group;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * A spanish stemming class for the spanish language.
 * Based on the paper "A word spanish stemming algorithm for the spanish language"
 * 
 * This stemmer requieres:
 * 	+ A dictionary of propper names (see discussion in checkIfPropperName())
 *  + A dictionary of stop words
 *  + A dictionary of Irregular verbs
 *  + A special dictionary of conjugations
 *  
 *  As for now. it only has the first one. The other ones are planned.
 *  
 * It has three main methods, "steamWord", which will apply all the rules to the word,
 * "removeStopWords()", which will check if the word is a Stop Word, and 
 * "changeToSingular()", which do a full removal of plurals. 
 * @author felipe
 *
 */
public class Steam {
	

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
	 * This are all the dictionary that can be used. However, maybe you
	 * are not going to use them all.
	 */
	
	private StopWords stopWords = null;
	private RiV_Dictionary rivDictionary = null;
	private I_Dictionary iDict = null;
	private SpanishDictionary spanishDictionary = null;
	private PropperNamesDictionary propperNames = null;

	/**
	 * The consrtuctor takes as argument the path to stop words, and initializes
	 * the variables that are needed.
	 * 
	 * @param pathToStopWords The path to an UTF-8 txt file with stop words
	 */
	
	public Steam(){
		
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
		
		// Spanish dictinary for debugging purposes
		//dictionary = new Dictionary();
	}

	/** 
	 * This is the main access to the steamer. It calls
	 * removeStopWords, removePlurals, removeAdverbsandReflectives
	 * and so on.
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
		if (checkIfPropperName(word))		// right now it just return false always.
			return word;
		if (isinIDict(word))
			return word;
		try {
			word = changeToSingular(word);
			word = removeAdverbsAndReflectives(word);
			word = removeRegularVerbs(word);
			word = step5(word);
			word = removeRegularVerbs(word);
			
		} catch (Exception e) {
			System.out.println("The guilty one is " + word);
			e.printStackTrace();
		}
		if(!stopWords.isInDictionary(word))
			System.out.println(word);
		return word;

	}

	/**
	 * This method will check in a Irregular verb dictionary to check for 
	 * exceptions before getting to the stemming phase.
	 * Right now it just returns false
	 * 
	 * @param word The word to check
	 * @return true if the word is in the dictionary, false otherwise
	 */
	private boolean isinIDict(String word) {
		if(iDict == null)
			iDict = new I_Dictionary();
		return iDict.isInDictionary(word);
	}

	/**
	 * This method does the following steps:
	 * 1.- Split the word
	 * 2.- Iterate each word, and if it is a
	 * 	   stop word, remove it.
	 * 3.- Return the array.
	 * 
	 * @param A string with just letters and spaces
	 * @return An empty string if the word is a stop word.
	 * the same word otherwise
	 */
	public String removeStopWords(String string){

		if (stopWords == null)
			stopWords = new StopWords();
		
		string = string.toLowerCase();
		String[] temp = string.split(" ");
		
		string = "";
		for(int i = 0; i < temp.length; i++){
			if(!stopWords.isInDictionary(temp[i]))
				string += temp[i] + " ";
		}

		return string;
	}
	
	/**
	 * TODO This method should check in a dictionary of propper names if it is contained.
	 * There are doubts about the efficiency of this, most notable in Zazo, Figuerola and
	 * Berrocal (2007), where they noted that it improved very little the indization
	 *  but nonetheless is added.
	 * 
	 * @param word The word to check if propper name
	 * @return boolean True if the word is a propper name, false otherwise
	 */
	
	/* Zazo, Figuerola and Berrocal (2007) in their paper "La detección de nombres propios
	 * en español y su aplicación en recuperación de información" developed an algorithm for 
	 * automatic detection of propper names in Spanish. However, they stated that while the 
	 * indization did got a little better, the overall impact in IR was null in all cases
	 * and even worst in some others, so automatization of this dictionary is not in future
	 * plans.
	 * 
	 */
	public boolean checkIfPropperName(String word){
		if (propperNames == null)
			propperNames = new PropperNamesDictionary();
		return propperNames.isInDictionary(word);
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
		word = word.replaceAll("\\p{P}+", "");	// This criptic command removes all punctuation

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
				if(word.endsWith("les")){
						if (!exceptionSuffixLes.contains(word.charAt(word.length()-4)))
					return word.substring(0, word.length()-2);
				}
				// señores -- > señor
				if(word.endsWith("res")){ 
					if (!exceptionSuffixRes.contains(word.charAt(word.length()-4)))
					return word.substring(0, word.length()-2);
				}
				
				/*
				 * If the word ends with "nes" or "ces", it maybe needs to be accentuated.
				 * So, that's what we do here.
				 * TODO
				 */
				if(word.endsWith("nes")){
					word = word.substring(0, word.length()-2);
					word = putOrRemoveStress(word);
					return word;
				}

				// frases --> frase

				if(word.endsWith("ses")) {
					if (vowels.contains(word.charAt(word.length()-4)))
					return word.substring(0, word.length()-1);
				}
				
				// narices --> nariz, sauces --> sauz
				if(word.endsWith("ces") && !word.endsWith("auces")){

					//like "narices"
					if(vowels.contains(word.charAt(word.length()-3))){
						word = word.substring(0, -3) + 'z';
						return word;
					}
				}
			}



			if(exceptionPlurals(word))
				return word;
		

		// else just return the string without s
		return word.substring(0,word.length()-1);

	}

	/**
	 * This method is the second step in the algorithm. It deals with
	 * most cases.
	 * 
	 * Also, if you read the implementation, be prepared for a sea of 
	 * "if" statements. 
	 * 
	 * @param String A word to remove 
	 * @return String The same word with the rules applied
	 */
	private String removeAdverbsAndReflectives(String word){

		
			if((word.endsWith("melo") 	||
				word.endsWith("mela") 	||
				word.endsWith("selo") 	||
				word.endsWith("telo") 	||
				word.endsWith("tela") 	||
				word.endsWith("noslo") 	||
				word.endsWith("nosla") 	||
				word.endsWith("oslo") 	||
				word.endsWith("osla") 
				)
				&& 
				hasVowel(word.substring(0,word.length()-4))
				)
			return removeStress(word);

			if(word.endsWith("erno"))
				return word;  

			if((word.endsWith("rlo") ||
				word.endsWith("rla") ||
				word.endsWith("rme") ||
				word.endsWith("rle") ||
				word.endsWith("rte") ||
				word.endsWith("rno") ||
				word.endsWith("rse")
				)
				&&
				endsInSpecialVowel(word)
				)
			return word.substring(0, word.length()-2);

			if (word.endsWith("rl"))
				return word.substring(0, word.length()-1);

			if ((word.endsWith("ndolo") ||
				 word.endsWith("ndola") ||
				 word.endsWith("ndole") ||
				 word.endsWith("ndome") ||
				 word.endsWith("ndote") ||
				 word.endsWith("ndono") ||
				 word.endsWith("ndose") 
				 )
				 &&
				 hasVowel(word)
				 ){
			String temp = word.substring(0, word.length()-5);
			return temp + "ndo";
			}

			// TODO find and example of word that ends like this
			if (word.endsWith("ndoo"))
				return word.substring(0, word.length()-1);

			/* TODO Rule 170 and 171. It seems that
			 * here is a typo, because the rules are
			 * 	170 "anza" Ends_in_Vowel NULL "ar"
			 *  171 "anza" NULL NULL "ar"
			 */
			
			// varianza --> variar
			if (word.endsWith("anza"))
				return word.replace("anza", "ar");

			// durante --> durar
			if (word.endsWith("ante"))
				return word.replace("ante", "ar");

			// TODO superlativo --> superlar
			// paliativo -- > paliar
			if (word.endsWith("ativo") || word.endsWith("ativa"))
				return word.replaceAll("ativ*", "ar");

			// mezcladora -- > mezclar
			if (word.endsWith("dora") && endsInVowel(word.substring(0,word.length()-4)))
				return word.replace("dora", "r");

			// bailador --> bailar
			if (word.endsWith("dor"))
				return word.replace("dor", "r");

			// TODO madura --> mar
			if (word.endsWith("dura"))
				return word.replace("dura", "r");

			// maduración --> madurar
			if (word.endsWith("ación"))
				return word.replace("ación","ar");

			// evolución --> evolucionar, but I can't think of many more examples
			if (word.endsWith("lución"))
				return word.replace("lución", "lucionar");

			// deducción --> deducir,
			if (word.endsWith("ducción"))
				return word.replace("ducción", "ducir");

			if (word.endsWith("ucción"))
				return word.replace("ucción", "uir");

			if (word.endsWith("ución"))
				return word.replace("ución", "uir");

			if (word.endsWith("abilidad"))
				return word.replace("abilidad", "able");

			// visibilidad --> visible
			if (word.endsWith("libilidad"))
				return word.replace("libilidad", "ible");

			// temeroso --> temor
			if ((word.endsWith("rosa") || word.endsWith("roso"))
					&& endsInVowel(word.substring(0, word.length()-4)))
				return word.replaceAll("ros*", "r");

			// entrenamiento --> entrenar
			if (word.endsWith("amiento"))
				return word.replace("amiento", "ar");

			// This is the first introduction of the special character 
			// "V". This will be used latter in the algorithm
			if (word.endsWith("imiento"))
				return word.replace("imiento", "V");

			//  tolerancia --> tolerar
			if (word.endsWith("ancia"))
				return word.replace("ancia", "ar");

			// superlative
			if (word.endsWith("ncísimo") || word.endsWith("ncísima"))
				return word.substring(0, word.length()-7) + 'n';

			if (word.endsWith("rcísimo") || word.endsWith("rcísima"))
				return word.substring(0, word.length()-7) + 'r';

			// grandísimo --> grande
			if (word.endsWith("ndísimo") || word.endsWith("ndísima"))
				return word.substring(0, word.length()-7) + "nde";

			// TODO violentísimo -- violente
			if (word.endsWith("ntísimo") || word.endsWith("ntísima"))
				return word.substring(0, word.length()-7) + "nte";

			if (word.endsWith("ísimo"))
				return word.replace("ísimo", "o");

			if (word.endsWith("ísima"))
				return word.replace("ísima", "a");

			// TODO aleatorio --> aleatar
			if (word.endsWith("atorio"))
				return word.replace("atorio", "ar");

			if (word.endsWith("itorio"))
				return word.replace("itorio", "") + 'V';

			// TODO centavo --> cent
			if (word.endsWith("avo"))
				return word.replace("avo", "v");
		

		// If we got here we skipped all the above rules
		return word;
	}
	
	/**
	 * This method deals with regular verbs and irregular conjugation
	 * 
	 * @param word The word to apply the algorithm
	 * @return	String The word with the applied rules. It may be that
	 * the word gets a -V termination for the next stage
	 */
	private String removeRegularVerbs(String word) {
		
		// Sequía pass untouched
		if (word.endsWith("quía"))
			return word; 
		
		// R suffixes covers common forms in verbs.
		// remember that all infinitives end in "ar","er" or "ir"
		String[] rSuffixes = { "ría",
							   "ríamo",
							   "ríais",
							   "rían",
							   "ré",
							   "rá",
							   "remo",
							   "réis",
							   "rán"				
		};
		
		for (String suffix: rSuffixes){
			if (word.endsWith(suffix))
				return word.substring(0, word.length() - suffix.length()) + 'r';
		}
		
		// This deals with the exclusive suffixes of "ar" termination
		String[] arSuffixes = { "aba",
								"ábamos",
								"abais",
								"aban",
								"ara",
								"áramo",
								"arais",
								"aran",
								"ase",
								"ásemo",
								"aseis",
								"asen",
								"aste",
								"asteis",
								"aron"					
		};
		
		for(String suffix: arSuffixes){
			if (word.endsWith(suffix))
				return word.substring(0, word.length() - suffix.length()) + "ar";
		}
		
		// Funny how both "er" and "ir" have so few 
		if (word.endsWith("ed"))
			return word.substring(0, word.length() - 2) + "er";
		
		if (word.endsWith("­ís"))
			return word.substring(0, word.length() -2) + "ir";
		
		// Here the regular verbs end and the irregular begin
		
		String[] irrSuffixes = {"ado",
								"ada",
								"íera",
								"íeramo",
								"ierais",
								"ieran",
								"iese",
								"iésemo",
								"ieseis",
								"iesen",
								"ía",
								"íamo",
								"iais",
								"ían",
								"iste",
								"ió",
								"isteis",
								"ieron",
								"ido",
								"amo",
								"emo",
								"imo",
								"áis",
								"éis",
								"an",
								"en",
								"í"				 
		};
		
		for(String suffix:irrSuffixes){
			if (word.endsWith(suffix))
				return word.replace(suffix, "V");
		}
		
		return word;
	}

	/**
	 * This method needs a better name. It is the step 5 in the algorithm
	 * 
	 * @param word The word to apply the rules to
	 * @return The word with the rules applied, never null
	 */
	private String step5(String word){
		
		// risible gets unmodiffied
		if (word.endsWith("sible"))
			return word;
		
		if (word.endsWith("able"))
			return word.substring(0, word.length() - 4) + 'V';
		
		if (word.endsWith("ible"))
			return word.substring(0, word.length() - 4) + 'V';
		
		if (word.endsWith("cecito") || word.endsWith("cecita"))
			return word.replaceAll("cecit*", "z");
		
		if (word.endsWith("cecillo") || word.endsWith("cecilla"))
			return word.replaceAll("cecill*", "z");
		
		if (word.endsWith("recito"))
			return word.replace("recito", "re");
		
		if (word.endsWith("lecito") || word.endsWith("lecita"))
			return word.replaceAll("lecit*", "le");
		
		if (word.endsWith("ecito") || word.endsWith("ecita"))
			return word.replaceAll("ecit*", "");
		
		if (word.endsWith("recillo") || word.endsWith("recilla"))
			return word.replaceAll("recill*", "re");
		
		if (word.contains("ecillo") || word.endsWith("ecilla"))
			return word.replaceAll("ecill*", "");
		
		if (word.endsWith("cillo") || word.endsWith("cilla"))
			return word.replaceAll("cill*", "");
		
		if (word.endsWith("ando"))
			return word.replace("ando", "ar");
		
		if (word.endsWith("iendo"))
			return word.replace("iendo", "V");
		
		if (word.endsWith("yendo"))
			return word.replace("yendo", "er");
		
		if (word.endsWith("ilidad"))
			return word.replace("ilidad", "ilo");
		
		if (word.endsWith("lidad"))
			return word.replace("lidad", "l");
		
		if (word.endsWith("íes"))
			return word.replace("íes", "í");

		return word;
	}

	/**
	 * Checks a list of rules based on irregular forms, i. e. those words
	 * that can't be dealt in an adequate manner in the next step.
	 * 
	 * @param word The word to apply the rules to
	 * @return The word with the rules applied, the same word if no
	 * rule matched.
	 */
	private String iregularSpelling (String word){
		
		String [][] suffixAndReplace = {
				{"respuesta", "responder"},
				{"puesto", "poner"},
				{"puesta", "porner"},
				{"srcito", "scribir"},
				{"scrita", "scribir"},
				{"uelto", "olver"},
				{"uelta", "olver"},
				{"ierto", "rir"},
				{"ierta", "rir"},
				{"hecho", "hacer"},
				{"hecha", "hacer"},
				{"mpreso", "mprimir"},
				{"mpresa", "mprimir"},
				{"muerto", "morir"},
				{"muerta", "morir"},
				{"roto", "romper"},
				{"rota", "romper"},
				{"visto", "ver"},
				{"vista", "ver"},
				{"frito", "freír"},
				{"frita", "freír"},
				{"dicho", "decir"},
				{"dicha", "decir"},
				
				{"que", "cV"},
				{"qué", "cV"},
				{"quV", "cV"},
				{"gué", "gV"},
				{"guV", "gV"},
				{"gue", "gV"},
				
				{"güe", "guV"},
				{"güV", "guV"},
				{"güé", "guV"},
				
				{"nzo", "nzV"},
				
				{"rzo", "rcV"},
				{"rzV", "rcV"},
				
				{"ingo", "inguV"},
				{"ingV", "inguV"},
				
				{"yó", "V"},
				{"yeron","V"},
				{"yera","V"},
				{"yérV","V"},
				{"yerais","V"},
				{"yerV","V"},
				{"yese","V"},
				{"yesV","V"},
				
				{"lló","llV"},
				{"lleron","llV"},
				{"llera","llV"},
				{"llérV","llV"},
				{"llése","llV"},
				{"llésV","llV"},
				{"llesV","llV"},
				
				{"ñó","ñV"},
				{"ñeron","ñV"},
				{"ñera","ñV"},
				{"ñérV","ñV"},
				{"ñese","ñV"},
				{"ñésV","ñV"},
				{"ñesV","ñV"},
				
				{"güence","gonzV"},
				{"güencV","gonzV"},
				{"güenzo","gonzV"},
				
				{"ío","iV"},
				{"íe","iV"},
				{"íV","iV"},
				
				{"úo","uV"},
				{"úa","uV"},
				{"úV","uV"},
				{"úe","uV"},
				
				{"ienzV","enzV"},
				{"encé","enzV"},
				
				{"duzco","ducV"},
				{"duzcV","ducV"},
				{"duje","ducV"},
				{"dujo","ducV"},
				{"dujV","ducV"},
				{"dujerV","ducV"},
				{"dujérV","ducV"},
				{"dujese","ducV"},
				{"dujésV","ducV"},
				{"dujesV","ducV"},
				
				{"ó","V"},
				{"duzca","V"},
				
				{"ós","ó"},
				
				{"yo","V"},
				{"ye","V"},
				{"ya","V"},
				{"yV","V"},
				
				{"rza","rcV"},
				
				{"nga","ngV"},
				
				{"íste","V"},
				{"ímo","V"},
				{"ístei","V"},
				
				{"zco","cV"},
				{"zca","cV"},
				{"zcV","cV"},
				
		};
		
		
		for (String[] suffix: suffixAndReplace){
			if (word.endsWith(suffix[0]))
				return word.substring(0, suffix[0].length()) + suffix[1];
		}
		
		if (word.endsWith("é") && !exceptFrenchWords.contains(word))
			return word.substring(0, word.length() - 1) + 'V';
		
		if (word.endsWith("és") && !exceptCountryPropperName.contains(word))
			return word.substring(0, word.length() - 1);
		
		
		return word;
	}
	
	/**
	 * TODO This method needs to call UnStem in case it isn't found.
	 * Until now, there is no way to do it.
	 * @param word
	 * @return
	 */
	private boolean isInRivDictionary(String word){
		if (rivDictionary == null)
			rivDictionary = new RiV_Dictionary();
		return rivDictionary.isInDictionary(word);
	}
	
	/**
	 * 
	 * @param failRule
	 * @param word
	 * @return
	 */
	private String unStem(int failRule, String word){
		switch(failRule){
		case 2:
			// do stuff
		}
		return word;
	}
	
	
	
	/**
	 * If it contains an accentuated value
	 * removes the accent. Otherwise place an 
	 * accent on the rightmost letter found
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
	 * 
	 */
	private boolean exceptionPlurals(String word){

		String [] exceptions = {"ríais", "ríeis", "arais","aseis","asteis","ireais",
								"ieseis","isteis","yerais","ís", "íes"};
		
		for (String exception: exceptions){
			if (word.contains(exception))
				return true;
		}
		
		return false;
	}


	/**
	 * Iterates over all the characters in the word
	 * to see if any of them is a vowel.
	 * 
	 * @param word to see if has vowel
	 * @return true if a vowel is found, false otherwise
	 */
	private boolean hasVowel(String word){
		for(int i = 0; i < word.length(); i++){
			if(vowels.contains(word.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * This method is special to the algorithm, there is nothing
	 * special about this vowels but the fact that they are 
	 * common in a series of rules.
	 *  
	 * The special vowels are a, e and i
	 * 
	 * @param word The word to check
	 * @return boolean If the words ends with a, e or i
	 */
	private boolean endsInSpecialVowel(String word){
		return specialVowels.contains(word.charAt(word.length()-1));
	}

	/**
	 * An abstraction to see whether the prefix ends with a vowel.
	 * It just considers regular vowels and not accentuated ones.
	 * 
	 * 
	 * @param String The prefix of the to check
	 * @return boolean True if the prefix ends with a vowel
	 */
	private boolean endsInVowel(String prefix){
		if (prefix.isEmpty())
			return false;

		return (vowels.contains(prefix.charAt(prefix.length()-1)));
	}

	private String removeStress(String word){
		for (Character vowel: vowels) {
			word = word.replace(vowel, normalToAccentuated.get(vowel));
		}
		return word;
	}
}
