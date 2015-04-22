package com.example.instaphoto;

/**
 * 
 * @author Timur Priymak
 *
 * @param <T> generic object
 */
public class SimpleStack<T> {
	/**
	 * Default size of stack if size is not specified
	 */
	private static final int DEFAULT_SIZE = 10;
	/**
	 * Base array of stack
	 */
	private T[] my_stack;
	/**
	 * Number of elements in stack
	 */
	private int my_size;
	/**
	 * The top of the stack
	 */
	private int my_top;
	/**
	 * Constructor with user inputed size
	 * @param the_size
	 */
	public SimpleStack(int the_size)
	{
		this.my_stack = (T[]) new Object[the_size];
		my_top = 0;
		my_size = 0;
	}
	/**
	 * Constructor with default size
	 */
	public SimpleStack()
	{
		this.my_stack = (T[]) new Object[DEFAULT_SIZE];
		my_top = 0;
		my_size = 0;
	}
	/**
	 * Pops one element of the stack
	 * @return the element at the toop of the stack
	 */
	public T pop()
	{
		T my_object;
		my_top--;
		my_size--;
		//signals top to go to the top of the stack and make a circle
		if(my_top < 0 && my_stack[my_stack.length - 1] != null)
		{
			my_top = my_stack.length - 1;
			my_object = my_stack[my_top];
			my_stack[my_top] = null;
			return my_object;
		}
		//signifies the stack is empty
		else if(my_top < 0 && my_stack[my_stack.length - 1] == null)
		{
			throw new IndexOutOfBoundsException();
		}
		//signifies the stack is empty
		else if(my_stack[my_top] == null)
		{
			throw new IndexOutOfBoundsException();
		}
		//pop element off stack
		else
		{
			my_object = my_stack[my_top];
			my_stack[my_top] = null;
			return my_object;
		}
	}
	/**
	 * pushes an element onto the stack
	 * takes element off the bottom if stack is already full
	 * @param the_object
	 */
	public void push(T the_object)
	{
		if(my_top > my_stack.length - 1)
		{
			my_top = 0;
		}
		else
		{
			my_size++;
		}
		my_stack[my_top] = the_object;
		my_top++;
	}
	/**
	 * number of elements in stack
	 * @return number of elements
	 */
	public int getSize()
	{
		return my_size;
	}
}
