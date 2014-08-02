package tesler.will.trie;

import static java.lang.System.out;

public class Main {

	public static void main(String[] args) {
		Trie trie = new Trie();//("raw/words.txt");
		
		long time = System.currentTimeMillis();
		trie.putWord("child");
		trie.putWord("children");
		trie.putWord("chill");
		String[] words = trie.getSuperWords("poop");
		if (words != null) {
			for (String word : trie.getSuperWords("poop")) {
				out.println(word);
			}
		}
		out.println("Lookup Time: " + (System.currentTimeMillis() - time));
	}
}
