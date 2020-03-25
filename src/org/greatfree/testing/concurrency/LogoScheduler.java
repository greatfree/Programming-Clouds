package org.greatfree.testing.concurrency;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.greatfree.concurrency.batch.BalancedAdaptable;

// Created: 04/23/2018, Bing Li
class LogoScheduler implements BalancedAdaptable<HumanLogo>
{

	@Override
	public void dispose() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isShutdown()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPause() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPaused()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void holdOn(long time) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keepOn()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getFastThreadKeys()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getFastStartTime(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getFastEndTime(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFastTask(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSlowStartTime(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSlowEndTime(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSlowTask(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int isFastEmpty(String key) throws InterruptedException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int isIdle(String key)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date getIdleTime(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getSlowThreadKeys()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void alleviateSlow(String key) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreFast(String key) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendSlow() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killSlow(String key) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void killAll() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<HumanLogo> getLeftTasks(String taskKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void submit() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

}
