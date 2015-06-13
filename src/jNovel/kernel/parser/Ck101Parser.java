package jNovel.kernel.parser;

import jNovel.kernel.Replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析文章內容 - For ck101.com
 * 
 * @author rickychiang
 *
 */
public class Ck101Parser extends AbstractParser implements INovelParser {

    static Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

    static Pattern pModStamp = Pattern
            .compile(" 本帖最後由 \\S+ 於 \\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2} (\\S{2} )?編輯 ");

    public Ck101Parser() {

        this(true);
    }

    public Ck101Parser(boolean encoding) {

        this.bookData = new StringBuilder();
        this.encoding = encoding;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jNovel.kernel.parser.INovelParser#parseMessageBodyStart(java.lang.String)
     */
    public void parseMessageBodyStart(String lineString) {

        if (lineString.indexOf("<i class=\"pstatus\">") >= 0) { // 過慮修改時間
            lineString = lineString.replaceAll("<i class=\"pstatus\">[^<>]+ </i>", "");
        }
        if (lineString.indexOf("<div class=\"quote\">") >= 0) { // 過濾
            // 引用
            lineString = lineString.replaceAll("<font color=\"#999999\">[^<>]+</font>", "");
        }

        appendLine();
        appendLine();
        appendLine(lineString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jNovel.kernel.INovelParser#parseMessageBodyLine(java.lang.String)
     */
    @Override
    public void parseMessageBodyLine(String lineString) {

        // 如果只有換行符號，不處理
        if (Pattern.matches("^<br />", lineString)) {
            return;
        }

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

    /*
     * (non-Javadoc)
     * 
     * @see jNovel.kernel.INovelParser#replaceHtmlTags(java.lang.String)
     */
    @Override
    public String replaceHtmlTags(String lineString) {

        Matcher m_html = p_html.matcher(lineString);
        lineString = m_html.replaceAll("");

        return lineString;
    }
}
