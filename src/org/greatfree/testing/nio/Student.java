package org.greatfree.testing.nio;

import java.io.Serializable;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class Student implements Serializable
{
	private static final long serialVersionUID = -5936474330600863785L;

	private String name;
	private int age;
	
	public Student(String n, int a)
	{
		this.name = n;
		this.age = a;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int getAge()
	{
		return this.age;
	}
	
	public String toString()
	{
		return "Name: " + this.name + ", Age: " + this.age;
	}
}
