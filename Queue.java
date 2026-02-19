package edu.ilstu;

/**
 * Queue class that is used in the Simulation class to handle a player queue made up of different types of players.
 * 
 * @author Brad Wickert
 */
public class Queue {

	/**
	 * Private inner Node class that keeps track of playerData.
	 */
	private static class Node

	{
		private Player playerData;
		private Node next;
		
		/**
		 * Node constructor with data parameter that initializes it's playerData with it.
		 * @param data
		 */
		private Node (Player data)
		{
			this.playerData = data;
		}
		
		/**
		 * Node constructor with data parameter and next parameter that initializes it's playerData and next with it.
		 * @param data
		 */
		private Node(Player data, Node next)
		{
			this.playerData = data;
			this.next = next;
		}
	}
	
	//Declaring the two Node objects of the top and bottom of the player queue, and its size.
	private Node playersHead;
	private Node playersTail;
	private int size;

	/**
	 * isEmpty() method that returns a boolean that is dependent on whether the size of the queue is zero or more than zero.
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		if(size <= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Enqueue method that puts the player parameter into the queue.
	 * @param item
	 */
	public void enqueue(Player item)
	{
		Node temp = new Node(item);
		if(isEmpty())
		{
			this.playersHead = temp;
		}
		else
		{
			this.playersTail.next = temp;
		}
		this.playersTail= temp;
		this.size++;
	}
	
	/**
	 * Dequeue method that removes the tail of the queue from the queue.
	 * @return tempPlayer
	 */
	public Player dequeue()
	{
		if(isEmpty())
		{
			throw new RuntimeException("Queue isEmpty");
		}
		Player temp = playersHead.playerData;
		this.playersHead = this.playersHead.next;
		if(this.playersHead == null)
		{
			this.playersTail = null;
		}
		this.size--;
		return temp;
	}
	
	/**
	 * Front method that returns the playerHead data.
	 * @return playersHead.playerData
	 */
	public Player front()
	{
		if(isEmpty())
		{
			throw new RuntimeException("Queue isEmpty");
		}
		else
		{
			return this.playersHead.playerData;
		}
	}
	
	/**
	 * Getter method for size.
	 * @return size
	 */
	public int getSize()
	{
		return this.size;
	}
	
	/**
	 * Equals method that checks if the parameter object of Queue is equal to the calling object of Queue.
	 * @param otherQueue
	 * @return boolean
	 */
	public boolean equals(Queue other)
	{
		if(this == other)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Clear method that clears the Queue.
	 */
	public void clear()
	{
		this.playersHead = null;
		this.playersTail = null;
		this.size = 0;
	}
	

}
