package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		HashMap<String, Occurrence> keyWords = new HashMap<String, Occurrence>();
		
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()){
			String word = sc.next();
			word = getKeyWord(word);
			if(word != null){
				if(keyWords.containsKey(word)){
					keyWords.get(word).frequency++;
				}
				else{
					Occurrence visited = new Occurrence(docFile, 1);
					keyWords.put(word, visited);
				}
			}
		}
		return keyWords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		ArrayList<Occurrence> iloList = new ArrayList<Occurrence>();
		
		for(String ck: kws.keySet()){
			Occurrence now = kws.get(ck);
			
			if(keywordsIndex.containsKey(ck)){
				iloList = keywordsIndex.get(ck);
				iloList.add(now);
				insertLastOccurrence(iloList);
				keywordsIndex.put(ck, iloList);
			}
			else{
				ArrayList<Occurrence> oList = new ArrayList<Occurrence>();
				oList.add(now);
				keywordsIndex.put(ck, oList);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		word = word.trim();
		word = word.toLowerCase();
		char lc = word.charAt(word.length()-1);
		
		while ((lc == '.') || (lc == ',') || (lc == '?') || (lc == ':') || (lc == ';') || (lc == '!')){
			word = word.substring(0, word.length()-1);
			if (word.length() > 1){
				lc = word.charAt(word.length()-1);
			}
			else{
				break;
			}
		}
		
		for (int x = 0; x < word.length(); x++){
			if (!Character.isLetter(word.charAt(x))){
				return null;
			}
		}
		
		for (String noise: noiseWords.keySet()){
			if (word.equalsIgnoreCase(noise)){
				return null;
			}
		}

		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		if (occs.size() == 1){
			return null;
		}
		int lF = occs.get(occs.size()-1).frequency;
		Occurrence hold = occs.get(occs.size()-1);
		int low = 0;
		int high = occs.size()-1;
		int mid;
		ArrayList<Integer> mids = new ArrayList<Integer>();
		
		while(low <= high){
			mid = (low + high) / 2;
			mids.add(mid);
			
			if(lF > occs.get(mid).frequency){
				high = mid-1;
			}
			else if(lF < occs.get(mid).frequency){
				low = mid+1;
			}
			else{
				break;
			}
		}
		
		if(mids.get(mids.size() - 1) == 0){
			if(hold.frequency < occs.get(0).frequency){
				occs.add(1, hold);
				occs.remove(occs.size() - 1);
				return mids;
			}
		}
		
		occs.add(mids.get(mids.size()-1), hold);
		occs.remove(occs.size()-1);
		
		return mids;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		ArrayList<String> finalList = new ArrayList<String>();
		ArrayList<Occurrence> list1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> list2 = new ArrayList<Occurrence>();
		int tracker = 0;
		
		if (keywordsIndex.get(kw1) != null){
			list1 = keywordsIndex.get(kw1);
		}
		if (keywordsIndex.get(kw2) != null){
			list2 = keywordsIndex.get(kw2);
		}
		
		if ((list1 == null) && (list2 == null)){
			return null;
		}
		else if((list1 != null) && (list2 == null)){
			int a = 0;
			while ((a < list1.size()) && (tracker < 5)){
				finalList.add(list1.get(a).document);
				a++;
				tracker++;
			}
		}
		else if((list1 == null) && (list2 != null)){
			int b = 0;
			while ((b < list2.size()) && (tracker < 5)){
				finalList.add(list2.get(b).document);
				b++;
				tracker++;
			}
		}
		else{
			int c = 0;
			int d = 0;
			while ((c < list1.size()) && (d < list2.size()) && (tracker < 5)){
				if (list1.get(c).frequency > list2.get(d).frequency){
					if (!finalList.contains(list1.get(c).document)){
						finalList.add(list1.get(c).document);
					}
					c++;
				}
				else if (list1.get(c).frequency < list2.get(d).frequency){
					if (!finalList.contains(list2.get(d).document)){
						finalList.add(list1.get(d).document);
					}
					d++;
				}
				else{
					if (!finalList.contains(list1.get(c).document)){
						finalList.add(list1.get(c).document);
					}
					if ((finalList.size() < 5) && (!finalList.contains(list2.get(d).document))){
						finalList.add(list2.get(d).document);
					}
					c++;
					d++;
				}
			}
			if (c == list1.size()){
				while ((d<list2.size()) && (finalList.size() < 5)){
					if (!finalList.contains(list2.get(d).document)){
						finalList.add(list2.get(d).document);
					}
					d++;
				}
			}
			if (d == list2.size()){
				while ((c<list1.size()) && (finalList.size() < 5)){
					if (!finalList.contains(list1.get(c).document)){
						finalList.add(list1.get(c).document);
					}
					c++;
				}
			}
		}
		
		/*
		for (int x = 0; x < list1.size(); x++){
			if (finalList.size() <= 4){
				int list1_freq = list1.get(x).frequency;
				String doc1 = list1.get(x).document;
				
				for (int y = 0; y < list2.size(); y++){
					int list2_freq = list2.get(y).frequency;
					String doc2 = list2.get(y).document;
					
					if (list2_freq <= list1_freq){
						if ((!finalList.contains(doc1)) && (finalList.size() <= 4)){
							finalList.add(doc1);
						}
					}
					
					else if (list2_freq > list1_freq){
						if ((!finalList.contains(doc2)) && (finalList.size() <= 4)){
							finalList.add(doc2);
						}
					}
				}
			}
		}
		*/
		
		if (finalList.size() == 0){
			return null;
		}
		//print5(finalList);
		return finalList;
	}
	
	/*
	public void printList(){
		for(String key : keywordsIndex.keySet())
		{
		     System.out.println(key + " : " + keywordsIndex.get(key));
		}
	}
	*/
	
	/*
	public void print5(ArrayList<String> five){
		for (String fives: five){
			System.out.println(fives);
		}
	}
	*/
}
