package org.greatfree.framework.player.ct.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.util.TerminateSignal;

// Created: 09/29/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Player master starting up ...");

		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, task);

		System.out.println("Player master started ...");

		Player printer = PlayerSystem.THREADING().reuse();

		task.setPlayer(printer);
		
		printer.notifyThreads(new PrintTaskNotification(printer.getThreadKey(), printer.toString(), 1000));

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		TerminateSignal.SIGNAL().notifyAllTermination();
		in.close();
	}

}
