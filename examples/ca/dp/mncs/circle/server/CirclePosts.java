package ca.dp.mncs.circle.server;

import java.util.ArrayList;
import java.util.List;

// Created: 02/25/2020, Bing Li
class CirclePosts
{
	private List<String> posts;
	
	private List<String> likes;
	
	private CirclePosts()
	{
		posts = new ArrayList<String>();
		posts.add("Hello, this is a holiday");
		posts.add("OK, let us go to school");
		posts.add("Play soccer tomorrow night, OK?");
		
		likes = new ArrayList<String>();
		likes.add("Mike");
		likes.add("John");
		likes.add("Lee");
	}
	
	private static CirclePosts instance = new CirclePosts();
	
	public static CirclePosts CS()
	{
		if (instance == null)
		{
			instance = new CirclePosts();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public List<String> getLatestPosts()
	{
		return posts;
	}
	
	public void addPost(String post)
	{
		posts.add(post);
	}

	public List<String> getLikes(String postID)
	{
		return likes;
	}
	
	public void addLike(String friendName)
	{
		this.likes.add(friendName);
	}
}
