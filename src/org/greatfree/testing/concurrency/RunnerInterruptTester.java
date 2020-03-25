package org.greatfree.testing.concurrency;

import java.util.Scanner;

import org.greatfree.concurrency.Runner;

// Created: 11/07/2019, Bing Li
class RunnerInterruptTester
{

	public static void main(String[] args)
	{
		InterTask r = new InterTask();
		Runner<InterTask> runner = new Runner<InterTask>(r);
		runner.start();
		
		Scanner in = new Scanner(System.in);
//		System.out.println("Enter to interrupt ManagedRunner ...");
//		in.nextLine();
//		r.interrupt();

		System.out.println("Enter to interrupt Runner ...");
		in.nextLine();
		runner.interrupt();

		System.out.println("Enter to exit ...");
		in.nextLine();
		in.close();
	}

}
