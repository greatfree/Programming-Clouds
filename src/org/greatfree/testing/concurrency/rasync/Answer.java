package org.greatfree.testing.concurrency.rasync;

import org.greatfree.concurrency.Response;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
class Answer extends Response
{
	private String answer;

	public Answer(String answer, String collaboratorKey)
	{
		super(collaboratorKey);
		this.answer = answer;
	}

	public String getAnswer()
	{
		return this.answer;
	}
}
