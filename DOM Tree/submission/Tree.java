package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> tagRecord = new Stack<TagNode>();
		int counter = 0;
		
		// Create HTML & Body Tags, because they will always be there
		TagNode html = new TagNode("html", null, null);
		TagNode body = new TagNode("body", null, null);
		root = html;
		root.firstChild = body;
		tagRecord.push(root);
		tagRecord.push(body);
		
		sc.nextLine(); sc.nextLine();	// Skip the first two lines (HTML, BODY tags)
		while(sc.hasNextLine()) {
			String temp = sc.nextLine();
			counter = 0;				// Use to identify tags
			if(temp.charAt(0) == '<') {
				if(temp.charAt(1) == '/') {
					tagRecord.pop();
					continue;
				} else {
					temp = temp.substring(1,temp.length()-1);
					counter = 1;
				}
			}
			TagNode newTag = new TagNode(temp, null, null);
			if(tagRecord.peek().firstChild == null) {
				tagRecord.peek().firstChild = newTag; 
			} else {
				TagNode peekChild = tagRecord.peek().firstChild;
				while(peekChild.sibling != null) {
					peekChild = peekChild.sibling;
				}
				peekChild.sibling = newTag;
			}
			if(counter == 1){
				tagRecord.push(newTag);
			}
		}
		
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		swap(root, oldTag, newTag);
	}
	
	private void swap(TagNode tempRoot, String old, String newer){
		// Base Case
		if (tempRoot == null){
			return;
		}
		// When to Replace the tag
		if ((tempRoot.tag == old) && (tempRoot.firstChild != null)){
			tempRoot.tag = newer;
		}
		
		// Recurse until done
		swap(tempRoot.sibling, old, newer);
		swap(tempRoot.firstChild, old, newer);
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		TagNode tbl = findTable(root);
		TagNode tableRow = tbl.firstChild;
		for(int x=1; x != row; x++) {
			tableRow = tableRow.sibling;
		}
		for(TagNode cell = tableRow.firstChild; cell != null; cell = cell.sibling) {
			TagNode bTag = new TagNode("b", cell.firstChild, null);
			cell.firstChild = bTag;
		}
	}
	
	private TagNode findTable(TagNode tempRoot){
		// Base Case
		if (tempRoot == null){
			return null;
		}
		
		if (tempRoot.tag.equals("table")){
			return tempRoot;
		}
		
		TagNode sib = findTable(tempRoot.sibling);
		TagNode first = findTable(tempRoot.firstChild);
		if(sib != null) return sib; 
		if(first != null) return first;
		
		return null;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if((tag.equals("p") || tag.equals("em") || tag.equals("b"))){
			removeCase1(root, tag);
		}
		if((tag.equals("ol") || tag.equals("ul"))){
			removeCase2(root, tag);
		}
	}
	
	private void removeCase1(TagNode tempRoot, String tag) { // "p", "em", "b"
		if(tempRoot == null){
			return;
		}
		if(tempRoot.tag.equals(tag) && tempRoot.firstChild != null) {
			tempRoot.tag = tempRoot.firstChild.tag;
			if(tempRoot.firstChild.sibling != null) {
				TagNode traverseTag = null;
				for(traverseTag = tempRoot.firstChild; traverseTag.sibling != null; traverseTag = traverseTag.sibling); 
					traverseTag.sibling = tempRoot.sibling;
					tempRoot.sibling = tempRoot.firstChild.sibling;
			}
			tempRoot.firstChild = tempRoot.firstChild.firstChild;
		}
		removeCase1(tempRoot.firstChild, tag); 
		removeCase1(tempRoot.sibling, tag);
	}
	
	private void removeCase2(TagNode tempRoot, String tag) { // tag to be removed is <ol> or <ul>
		if(tempRoot == null) return;
		if(tempRoot.tag.equals(tag) && tempRoot.firstChild != null) {
			tempRoot.tag = "p";
			TagNode traverseTag = null;
			for(traverseTag = tempRoot.firstChild; traverseTag.sibling != null; traverseTag = traverseTag.sibling) traverseTag.tag = "p"; 
			// ^ changes all <li> tags to <p> tags and finds last <li> TagNode
			traverseTag.tag = "p";
			traverseTag.sibling = tempRoot.sibling;
			tempRoot.sibling = tempRoot.firstChild.sibling;
			tempRoot.firstChild = tempRoot.firstChild.firstChild;
		}
		removeCase2(tempRoot.firstChild, tag); 
		removeCase2(tempRoot.sibling, tag);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if(tag.equals("em") || tag.equals("b")) addTagPrivate(root, word.toLowerCase(), tag);
	}
	
	private void addTagPrivate(TagNode tempRoot, String word, String tag) {
		// Base Case
		if(tempRoot == null){
			return; 
		}
		
		addTagPrivate(tempRoot.firstChild, word, tag);
		addTagPrivate(tempRoot.sibling, word, tag);
		
		if(tempRoot.firstChild == null) {
			while(tempRoot.tag.toLowerCase().contains(word)) {
				String[] splits = tempRoot.tag.split(" ");
				Boolean wordMatch = false;
				String taggedWord = "";
				StringBuilder taggerString = new StringBuilder(tempRoot.tag.length());
				int counter = 0;
				for(int words = 0; words < splits.length; words++) {
					if(splits[words].toLowerCase().matches(word+"[.,?!:;]?")) {
						wordMatch = true;
						taggedWord = splits[words];
						for(int x=words+1; x<splits.length; x++){
							taggerString.append(splits[x]+" ");
						}
						break;
					}
				}
				if(!wordMatch){
					return;
				}
				
				String finalString = taggerString.toString().trim();
				if(counter == 0) {
					tempRoot.firstChild = new TagNode(taggedWord, null, null);
					tempRoot.tag = tag;
					if(!finalString.equals("")) { 
						tempRoot.sibling = new TagNode(finalString, null, tempRoot.sibling);
						tempRoot = tempRoot.sibling;
					}
				} else {
					TagNode taggedWordNode = new TagNode(taggedWord, null, null);
					TagNode newTag = new TagNode(tag, taggedWordNode, tempRoot.sibling);
					tempRoot.sibling = newTag;
					tempRoot.tag = tempRoot.tag.replaceFirst(" " + taggedWord, "");
					if(!finalString.equals("")) {
						tempRoot.tag = tempRoot.tag.replace(finalString, "");
						newTag.sibling = new TagNode(finalString, null, newTag.sibling);
						tempRoot = newTag.sibling;
					}
				}
			} 
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
