package edu.chainnet.s3.meta;

import java.util.concurrent.atomic.AtomicBoolean;

// Created: 09/11/2020, Bing Li
class Controller
{
	private AtomicBoolean isPaused;

	private Controller()
	{
		this.isPaused = new AtomicBoolean(false);
	}
	
	private static Controller instance = new Controller();
	
	public static Controller MAN()
	{
		if (instance == null)
		{
			instance = new Controller();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void setPaused(boolean isPaused)
	{
		this.isPaused.set(isPaused);
	}
	
	public boolean isPaused()
	{
		return this.isPaused.get();
	}
}
