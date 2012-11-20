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

	private static final String PATH_STOP_WORDS =  "/home/felipe/Descargas/stopWords.txt";
	private static final String PATH_TEST = "/home/felipe/Descargas/español.txt";
	private static final String ENCODING = "UTF-8";
	private static final int SIZE_OF_BUFFER = 1024;

	public static void main(String[] args) {
		BufferedReader buffReader = null;
		FileInputStream inputStream = null;
		Steam steamer = new Steam(PATH_STOP_WORDS);
		
		try {
			inputStream = new FileInputStream(PATH_TEST);
			buffReader = new BufferedReader(new InputStreamReader(inputStream,ENCODING), SIZE_OF_BUFFER);
			String line; 
			String[] lines;
			String temp;
			while((line = buffReader.readLine()) != null){
				lines = line.split(" ");
				for(String word: lines){
					word.toLowerCase();
					
					if (word.endsWith("úes")){
						System.out.println(word);
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
		
		String test = "programas";
		System.out.println(test.lastIndexOf("amas"));
	}
}
