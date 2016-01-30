package jNovel.kernel.parser;

public class PageCounter {
	
	public static PageCounter Instance = new PageCounter();
	
	private int page = 1;
	
	public int get() {
		return page++;
	}
	
}
