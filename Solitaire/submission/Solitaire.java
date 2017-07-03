package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		CardNode traverse = deckRear.next;
		int ctra = 0;
		if (deckRear.cardValue == 27){
			int swapTemp = deckRear.cardValue;
			deckRear.cardValue = deckRear.next.cardValue;
			deckRear.next.cardValue = swapTemp;
		}
		else{
			do {
				ctra++;
				if (traverse.cardValue == 27){
					int swapTemp = traverse.cardValue;
					traverse.cardValue = traverse.next.cardValue;
					traverse.next.cardValue = swapTemp;
					break;
				}
				traverse = traverse.next;
			}while(traverse != deckRear);
		}
		System.out.print("jokerA:     ");
		printList(deckRear);
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
	    CardNode traverse = deckRear.next;
	    int ctrb = 0;
	    if (deckRear.cardValue == 28){
	    	int swapTemp = deckRear.cardValue;
	    	deckRear.cardValue = deckRear.next.cardValue;
	    	deckRear.next.cardValue = deckRear.next.next.cardValue;
	    	deckRear.next.next.cardValue = swapTemp;
			deckRear = deckRear.next;
	    }
	    else{
	    do{
	    	ctrb++;
	    	if (traverse.cardValue == 28){
	    		if (ctrb == 26){
	    			int swapTemp = traverse.cardValue;
	    			traverse.cardValue = traverse.next.cardValue;
	    			traverse.next.cardValue = traverse.next.next.cardValue;
	    			traverse.next.next.cardValue = swapTemp;
	    			break;
	    		}
	    		else if(ctrb == 27){
	    			int swapTemp = traverse.cardValue;
	    			traverse.cardValue = traverse.next.cardValue;
	    			traverse.next.cardValue = traverse.next.next.cardValue;
	    			traverse.next.next.cardValue = swapTemp;
	    			break;
	    		}
	    		else{
	    			int swapTemp = traverse.cardValue;
	    			traverse.cardValue = traverse.next.cardValue;
	    			traverse.next.cardValue = traverse.next.next.cardValue;
	    			traverse.next.next.cardValue = swapTemp;
	    			break;
	    		}
	    	}
	    	traverse = traverse.next;
	    }while(traverse != deckRear);
	    }
	    System.out.print("jokerB:     ");
	    printList(deckRear);
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode traverse = deckRear.next;
		int j1value = 0;
		CardNode j1 = traverse;
		CardNode b4j1 = traverse;
		int ctr1 = 0;
		do {
			ctr1++;
			if (traverse.cardValue == 27){
				j1 = traverse;
				j1value = 27;
				break;
			}
			if (traverse.cardValue == 28){
				j1 = traverse;
				j1value = 28;
				break;
			}
			b4j1 = traverse; 
			traverse = traverse.next;
		}while(traverse != deckRear);
		
		int j2value = 0;
		if (j1value == 27){
			j2value = 28;
		}
		else{
			j2value = 27;
		}
		
		traverse = deckRear.next;
		CardNode j2 = traverse;
		CardNode aj2 = traverse;
		CardNode b4j2 = traverse;
		int ctr2 = 0;
		if (deckRear.cardValue == j2value){
			j2 = deckRear;
			aj2 = deckRear.next;
			ctr2 = 28;
		}
		else{
		do {
			ctr2++;
			if (traverse.cardValue == j2value){
				j2 = traverse;
				aj2 = traverse.next;
				break;
			}
			b4j2 = traverse;
			traverse = traverse.next;
		}while(traverse != deckRear);
		}
		
		if ((ctr1 == 1) && (ctr2 != 28)){
			deckRear = j2;
			System.out.print("tripleCut:     ");
			printList(deckRear);
		}
		else if ((ctr1 != 1) && (ctr2 == 28)){
			deckRear = b4j1;
			System.out.print("tripleCut:     ");
			printList(deckRear);
		}
		else if ((ctr1 == 1) && (ctr2 == 28)){
			deckRear = j2;
			System.out.print("tripleCut:     ");
			printList(deckRear);
		}
		else{
			b4j1.next = aj2;
			j2.next = deckRear.next;
			deckRear.next = j1;
			deckRear = b4j1;
			System.out.print("tripleCut:     ");
			printList(deckRear);
		}
	}
	
	/**v
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		int endData = 0;
		if (deckRear.cardValue == 28){
			endData = 27;
		}
		else{
			endData = deckRear.cardValue;
		}
		if (endData == 27){
			
		}
		else{
		CardNode front = deckRear.next;
		CardNode countEnd = deckRear.next;
		for (int x = 1; x < endData; x++){
			countEnd = countEnd.next;
		}
		
		CardNode s2last = deckRear.next;
		for (int x = 1; x < 27; x++){
			s2last = s2last.next;
		}
		
		s2last.next = front;
		deckRear.next = countEnd.next;
		countEnd.next = deckRear;
		}
		System.out.print("countCut:     ");
		printList(deckRear);
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		int key = 0;
		boolean keepGoing = false;
		do{
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			
			int front = deckRear.next.cardValue;
			if (front == 28){
				front = 27;
			}
			
			CardNode counter = deckRear.next;
			for (int x = 1; x < front; x++){
				counter = counter.next;
			}
			key = counter.next.cardValue;
			if ((key == 27) || (key == 28)){
				keepGoing = true;
			}
			else{
				keepGoing = false;
			}
		} while(keepGoing);
		System.out.println("Key:     " + key);
		return key;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		String messageA = message.replaceAll("\\s","");
		messageA = messageA.replaceAll("\\W","");
		messageA = messageA.toUpperCase();
		String encrypted = "";
		int currentKey = 0;
		char tverse = 'A';
		int added = 0;
		for (int x = 0; x < messageA.length(); x++){
			tverse = messageA.charAt(x);
			currentKey = getKey();
			added = tverse - 'A' + 1 + currentKey;
			if (added > 26){
				added = added - 26;
			}
			encrypted = encrypted + (char)(added - 1 + 'A');
		}
		return encrypted;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		String messageA = message.replaceAll("\\s","");
		messageA = messageA.replaceAll("\\W","");
		messageA = messageA.toUpperCase();
		String decrypted = "";
		int currentKey = 0;
		char tverse = 'A';
		int subtracted = 0;
		for (int x = 0; x < messageA.length(); x++){
			tverse = messageA.charAt(x);
			currentKey = getKey();
			subtracted = tverse - 'A' + 1;
			if (subtracted <= currentKey){
				subtracted+=26;
			}
			subtracted-=currentKey;
			decrypted = decrypted + (char)(subtracted - 1 + 'A');
		}
		return decrypted;
	}
}
