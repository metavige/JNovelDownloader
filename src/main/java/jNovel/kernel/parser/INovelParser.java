package jNovel.kernel.parser;

public interface INovelParser {

    /**
     * 把資料加入，並加上換行符號
     * @param line
     */
    public void appendLine(String line);

    /**
     * 資料加上換行符號
     */
    public void appendLine();

    /**
     * 處理每篇單元文章的開頭
     * 
     * @param lineString
     */
    public void parseMessageBodyStart(String lineString);

    /**
     * 處理本文的每一行資料
     * 
     * @param lineString
     * @param p_html
     * @param pModStamp
     */
    public void parseMessageBodyLine(String lineString);

    /**
     * @param lineString
     * @return
     */
    public String replaceHtmlTags(String lineString);

    public abstract String parse(String[] html);

}
