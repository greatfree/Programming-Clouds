package org.greatfree.testing.util;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.util.FileManager;
import org.greatfree.util.Rand;

// Created: 03/14/2020, Bing Li
class RandomTextTester
{

	public static void main(String[] args) throws IOException
	{
		List<String> texts = FileManager.readText("/home/libing/Temp/text_sample.txt");
		Scanner in = new Scanner(System.in);
		String option;
		do
		{
			System.out.println(Rand.getRandomListElement(texts));
			System.out.println("Continue (y or n)?");
			option = in.nextLine();
		}
		while (!option.equals("n"));
		in.close();
	}

}
