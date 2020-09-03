package org.greatfree.testing.util;

import java.io.Serializable;

// Created: 09/03/2020, Bing Li
class Student implements Serializable
{
	private static final long serialVersionUID = 3228905997184912026L;
	
	private String name;
	private int age;
	
	public Student(String name, int age)
	{
		this.name = name;
		this.age = age;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int getAge()
	{
		return this.age;
	}
}
