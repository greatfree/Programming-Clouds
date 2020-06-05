package ca.streaming.news.publisher;

// Created: 04/03/2020, Bing Li
public class NewsConfig
{
	public final static int PIECE_SIZE = 20000;
	public final static String FILE_PATH = "/home/libing/Temp/r.mp4";
	public final static String FILE_DES_PATH = "/home/libing/GreatFreeLabs/Temp/r_bak.mp4";
	public final static String FILE_USER_DES_PATH = "/home/libing/GreatFreeLabs/Temp/r_user_bak.mp4";
	
	public final static String PIECE_HEAD = "PieceHead";
	public final static int MAX_PIECES_SIZE = 10000;
	
	public final static long USER_TIMEOUT = 500;
}
