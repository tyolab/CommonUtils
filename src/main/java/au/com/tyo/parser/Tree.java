/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.parser;

public class Tree<NodeType> {
	protected NodeType root;

    public Tree(byte[] bytes) {
//        root = new NodeType(bytes);
//        root.data = rootData;
//        root.children = new ArrayList<NodeType>();
    }
    
    public Tree(NodeType root) {
    	this.root = root;
    }  

}
