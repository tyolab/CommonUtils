/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.parser;

public class SgmlTree<SgmlNodeType> extends Tree<SgmlNodeType> {

	public SgmlTree(byte[] bytes) {
		super(bytes);
	}

	public SgmlTree(SgmlNodeType root) {
		super(root);
	}

//	public SgmlTree(SgmlNodeType node) {
//		super(node);
//	}
	  
}
