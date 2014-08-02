package tesler.will.trie;

// Copyright Will Tesler 2014.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A lexigraphic Trie.<br/><br/>
 * To read about the Trie data structure, see here:
 *  http://en.wikipedia.org/wiki/Trie
 */
public class Trie {
	
	private Node root;
	
	private static final int ASCII_UPPER_CASE = 65;
	private static final int ASCII_CHAR_OFFSET = ASCII_UPPER_CASE;
	
	private static final int ALPHABET_SIZE = 26;
	
	Trie() {root = new Node();}
	
	/**
	 * @param filepath Path to the file that holds a lexicon of words<br/><br/>
	 * Constructs a Trie that represents all of the words in the file specified
	 * by filepath.<br/><br/>
	 * Every line in the file should have the word as the first String on the
	 * line. Everything after the first String is thrown out.
	 */
	Trie(String filepath) {
		this();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(filepath));
			while (in.ready()) {
				String s = in.readLine();
				String[] split = s.split(" ");
				String word = split[0].toUpperCase();
				this.putWord(word);
			}
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param word The word we want to check against the Trie.
	 * @return true if the word is represented by the Trie, false otherwise.
	 */
	boolean hasWord(String word) {
		Node node = root;
		word = word.toUpperCase();
		for (int i = 0; i < word.length() - 1; i++) {
			//a == 0, z == 25
			int letterIndex = word.charAt(i) - ASCII_CHAR_OFFSET;
			if (node.children.containsKey(letterIndex)) {
				node = (Node) node.children.get(letterIndex);
			} else {
				return false;
			}
		}
		int finalIndex = word.charAt(word.length() - 1) - ASCII_CHAR_OFFSET;
		return (node.bitSet & (1 << finalIndex)) != 0 ? true : false;
	}
	
	/**
	 * @param word The word that we want to represent in the trie.<br/><br/>
	 * Places the word into the trie, generating new nodes if necessary.
	 */
	void putWord(String word) {
		Node node = root;
		word = word.toUpperCase();
		for (int i = 0; i < word.length() - 1; i++) {
			//a == 0, z == 25
			int letterIndex = word.charAt(i) - ASCII_CHAR_OFFSET;
			if (!node.children.containsKey(letterIndex)) {
				node.children.put(letterIndex, new Node());
			}
			node = (Node) node.children.get(letterIndex);
		}
		int finalIndex = word.charAt(word.length() - 1) - ASCII_CHAR_OFFSET;
		node.bitSet |= (1 << finalIndex);
	}
	
	/**
	 * @param partial A partial word that may be a prefix of other words.<br/><br/>
	 * Retrieves all words in which partial is a prefix of that word.<br/>
	 * @return an array of words in which the partial is a prefix of those words.
	 */
	String[] getSuperWords(String partial) {
		ArrayList<String> words = new ArrayList<String>();
		Node node = root;
		partial = partial.toUpperCase();
		
		// Iterate down to the ending node of the partial
		for (int i = 0; i < partial.length(); i++) {
			//a == 0, z == 25
			int letterIndex = partial.charAt(i) - ASCII_CHAR_OFFSET;
			if (node.children.containsKey(letterIndex)) {
				node = (Node) node.children.get(letterIndex);
			} else {
				return null;
			}
		}
		
		checkChildren(node, partial, words);
		
		return words.toArray(new String[words.size()]);
	}
	
	/**
	 * @param node
	 * @param partial the partial word we are building upon
	 * @param words list of words that are superstrings of the user's partial
	 * <br/><br/>
	 * Recursively build upon the partial word by incrementally adding a single
	 * letter and then seeing if the partial is still a prefix.<br/><br/>
	 * Helper function for the getSuperWords method.
	 */
	private void checkChildren(Node node, String partial, ArrayList<String> words) {
		for (int letterInt = 0; letterInt < ALPHABET_SIZE; letterInt++) {
			char letter = (char)(letterInt + ASCII_CHAR_OFFSET);
			if((node.bitSet & (1 << letterInt)) != 0) {
				words.add(partial + letter);
			}
			if (node.children.containsKey(letterInt)) {
				checkChildren((Node) node.children.get(letterInt), 
						partial + letter, words);
			}
		}
	}
	
	/**
	 * A node of the Trie. Holds the status of all words that end on this
	 * node, as well as a list of children to which the words in this node
	 * may be a prefix of.
	 */
	private class Node {
		/*
		 * used as a mask to determine if there is a word at the given
		 * index in the current node.
		 * For example: 0x800000 would mean a word ending in A is at this
		 * position. 0x000001 would mean a word ending in Z is at this position.
		 * 0x800001 would mean a word ending in A and a word ending in Z is at
		 * this position.
		 * We use a single int to represent this because the range of the array
		 * goes from 0 - 25 (when dealing with English letters). 
		 */
		int bitSet = 0;
		
		/*
		 * An array of Nodes, where index 0 refers to letter A, and index 25
		 * refers to letter Z.
		 */
		SparseArray children = new SparseArray(ALPHABET_SIZE);
	}
}
