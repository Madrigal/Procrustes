package group;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	/**
	 *  Analyzes text and groups it into categories.
	 */

	private static final String PATH_STOP_WORDS =  "/home/felipe/Codigo/Procrustes/docs/sortedWords.txt";
	private static final String PATH_TEST = "/home/felipe/Descargas/español.txt";
	private static final String ENCODING = "UTF-8";
	private static final int SIZE_OF_BUFFER = 1024;
	
	private static BufferedReader buffReader;
	private static FileInputStream inputStream;
	private static Steam steamer;
	private static Dictionary dictionary;
	
	// For debugging purposes, a Spanish dictionary was added
	private static final String PATH_DICTIONARY = "/home/felipe/Codigo/Procrustes/docs/newDict.txt";

	public static void main(String[] args) {
		buffReader = null;
		inputStream = null;
		steamer = new Steam(PATH_STOP_WORDS);
		dictionary = new Dictionary(PATH_DICTIONARY);
		
		
		try {
			inputStream = new FileInputStream(PATH_TEST);
			buffReader = new BufferedReader(new InputStreamReader(inputStream,ENCODING), SIZE_OF_BUFFER);
			String line; 
			String[] lines;
			while((line = buffReader.readLine()) != null){
				lines = line.split(" ");
				String temp;
				for(String word: lines){
					temp = steamer.steamWord(word);
					if(!temp.isEmpty()){
						if(!dictionary.isInDictionary(temp)){
							if(word.endsWith("es"))
								System.out.println(word + " changed to " + temp);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		String test = "índices";
		System.out.println(steamer.steamWord(test));
	}
	
}
