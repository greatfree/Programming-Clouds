package us.treez.inventory.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

import us.treez.inventory.db.BusinessDB;

// Created: 02/05/2020, Bing Li
class BusinessServer
{
	private ServerContainer server;
	
	private BusinessServer()
	{
	}
	
	private static BusinessServer instance = new BusinessServer();
	
	public synchronized static BusinessServer CS()
	{
		if (instance == null)
		{
			instance = new BusinessServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		BusinessDB.CS().dispose();
		this.server.stop(timeout);
	}
	
	public void start(int port, ServerTask task, String idbPath, String odbPath) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new ServerContainer(port, task);
		this.server.start();
		BusinessDB.CS().init(idbPath, odbPath);
	}
}
