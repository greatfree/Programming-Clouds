package edu.chainnet.s3.testing;

// Created: 07/11/2020, Bing Li
class KNTester
{

	public static void main(String[] args)
	{
		int k = 3;
		int n = 103;
		int g = k;
		if (n % k != 0)
		{
			g = k + 1;
		}
		
		for (int i = 0; i < 103; i++)
		{
			System.out.println(i + ") " + ", edID = " + (i % g) + ", position = " + (i / g));
		}
	}

}
