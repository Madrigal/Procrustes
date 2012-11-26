package group;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class Document {
	
	private PDDocument document;
	private String representationInText;
	
	/**
	 * Creates a new document to strip the text 
	 * from it.
	 * 
	 * @param String The path to a pdf file in Spanish
	 */
	public Document(String path){
		File file = new File(path);
		try {
			document = PDDocument.load(file);
			PDFTextStripper stripper = new PDFTextStripper();
			representationInText = stripper.getText(document);
		} catch (IOException e) {
			System.out.println("Document couldn't be loaded. Try changing the path");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return String Returns a text representation of the 
	 * document loaded, and an empty string if it couldn't be loaded.
	 */
	public String getText(){
		return representationInText;		
	}
}
