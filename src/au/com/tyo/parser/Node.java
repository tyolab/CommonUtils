package au.com.tyo.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node<NodeType> implements Iterable<NodeType>/*, Iterator<Node>*/ {

    protected byte[] bytes;
    protected NodeType parent;
    protected List<NodeType> children;
    
    protected int level = 0;
    
//    protected Iterator<NodeType> iterator = null;
    
	public Node(byte[] bytes) {
		  this.bytes = bytes;
		  init();
//		  iterator = children.iterator();
	}

	public Node() {
		init();
	}
	
	protected void init() {
		children = new ArrayList<NodeType>();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean addChild(NodeType n) {
	    return children.add(n);
	}

	public boolean removeChild(NodeType n) {
	    return children.remove(n);
	}

	public Iterator<NodeType> iterator() {
		level = 0;
	    return children.iterator();
	}
	
	public NodeType getChild(int index) {
		return children.size() > 0 && index < children.size() ? children.get(index) : null;
	}
	
	public int countChildren() {
		return children.size();
	}

//	public boolean hasNext() {
//		if (iterator == null)
//			iterator = children.iterator();
//		return iterator.hasNext();
//	}
//
//	public NodeType next() {
//		iterator = (Iterator<NodeType>) iterator.next();
//		return (NodeType) iterator;
//	}
//
//	public void remove() {
//		iterator.remove();
//	}
	
	public String toString() {
		return this.toString();
	}
}