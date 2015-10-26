/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FixedSizeLinkedList<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4382270311820092109L;

	private static final int DEFAULT_SIZE = 10;
	
	private LinkedList<T> list;
	
	private int limit;
	
	private int index;

	public FixedSizeLinkedList() {
		limit = DEFAULT_SIZE;
		init();
	}
	
	public FixedSizeLinkedList(int size) {
		this.limit = size;
		init();
	}
	
	private void init() {
		index = -1;
		list = new LinkedList<T>();
	}
	
	public T getFirst() {
		if (list.size() > 0)
			return list.get(0);
		return null;
	}
	
	public T getLast() {
		if (list.size() > 0)
			return list.get(list.size() - 1);
		return null;
	}
	
	public T getCurrent() {
		if (list.size() == 0)
			return null;
		
		if (index == -1)
			index = 0;
		
		return list.get(index);
	}
	
	public T getNext() {
		if (index == -1)  // which means current index == 0, there is no next
			return null;
		
		if (index > 0 )
			--index;
		return getCurrent();
	}
	
	public T getPrev() {
		if (index == -1)  // which means current index == 0, get the previous will be 1;
			index = 0;
		
		if ((list.size() - 1) > index)
			++index;

		return getCurrent();
	}
	
	public void push_back(T input) {
		list.addLast(input);
		
		while (list.size() > limit)
			list.removeFirst();
	}
	
	public T pop() {
		return list.pop();
	}

	public void insert(T input) {
//		while (index < (list.size() - 1)) 
//			list.removeLast();
		
//		if (index >= limit) {
//			list.removeFirst();
//		}
		if (index > -1 && index < list.size()) 
			list.add(index, input);
		else
			list.addFirst(input);
		
		while (list.size() > limit)
			list.removeLast();
		
		while (index >= list.size())
			--index;
//		index = list.size() - 1;
	}
	
	public List<T> toList() {
		return list;
	}
	
	public int size() {
		return list.size();
	}
}
