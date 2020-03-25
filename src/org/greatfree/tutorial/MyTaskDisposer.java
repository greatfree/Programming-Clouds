package org.greatfree.tutorial;

import org.greatfree.reuse.RunnerDisposable;

// Created: 05/10/2015, Bing Li
public class MyTaskDisposer implements RunnerDisposable<MyTask>
{
	@Override
	public void dispose(MyTask t)
	{
		t.dispose();
	}

	@Override
	public void dispose(MyTask t, long time)
	{
		t.dispose();
	}
}
