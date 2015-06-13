package JNovelDownloader.Kernel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析文章內容
 * 
 * @author rickychiang
 *
 */
public class Parser {

	static Pattern p_html = Pattern
			.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

	static Pattern pModStamp = Pattern
			.compile(" 本帖最後由 \\S+ 於 \\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2} (\\S{2} )?編輯 ");

	private String lineSeparator;
	private StringBuilder bookData;
	private boolean encoding;

	public Parser() {
		this(true);
	}
	
	public Parser(boolean encoding) {

		this.lineSeparator = System.getProperty("line.separator");
		this.bookData = new StringBuilder();
		this.encoding = encoding;
	}

	public void appendLine(String line) {
		bookData.append(line).append(lineSeparator);
	}

	public void appendLine() {
		bookData.append(lineSeparator);
	}
	/**
	 * 處理本文的每一行資料
	 * 
	 * @param lineString
	 * @param p_html
	 * @param pModStamp
	 */
	public void parseMessageBodyLine(String lineString) {

		Matcher m_html = pModStamp.matcher(lineString);

		lineString = m_html.replaceAll("");
		lineString = Replace.replace(lineString, "<br/>", lineSeparator);
		lineString = Replace.replace(lineString, "<br />", lineSeparator);
		lineString = Replace.replace(lineString, "&nbsp;", "");

		lineString = replaceHtmlTags(lineString);

		// if(flag==false &&
		// temp.matches("第[一二三四五六七八九十百零1234567890 　]*章 [^<>]*"))
		// //for Calibre 轉檔
		// {
		// String
		// headLineString="<floor>"+Replace.replace(temp,
		// "\r\n", "")+"</floor>";
		// bookData.append(headLineString);
		// bookData.append("\r\n");
		// flag=true;
		// }
		lineString = lineString.replaceAll("^[ \t　]+", ""); // 過濾凸排

		appendLine(lineString);
	}

	public String replaceHtmlTags(String lineString) {

		Matcher m_html = p_html.matcher(lineString);
		lineString = m_html.replaceAll("");

		return lineString;
	}

}
