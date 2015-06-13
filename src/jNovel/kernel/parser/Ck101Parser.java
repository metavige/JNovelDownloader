package jNovel.kernel.parser;

import jNovel.kernel.Replace;
import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
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

    @Override
    public String parse(String[] html) {

        // boolean inContent = false;
        NovelBodyParseStage stage = NovelBodyParseStage.Parpare; // 0=不再內文中 ,1=在<div class="pbody">
                                                                 // 中,
        // 2=在<div class="mes">中 ,
        // 3=<div id="postmessage_~~~~" class="mes">中 ,
        String temp;
        boolean flag = false;
        int otherTable = 0; // 標記每一組訊息留言

        /* 用於正規表示式的過濾，比replace all 快速準確 */
        Matcher m_html;

        // String regEx_html = "<[^>]+>";
        Pattern pModStamp = Pattern
                .compile(" 本帖最後由 \\S+ 於 \\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2} (\\S{2} )?編輯 ");

        for (int n = 0; n < html.length; n++) {
            
            BufferedReader reader = FileUtils.readFile(html[n]);
            
            if (reader == null) {
                Logger.printf("無法讀取檔案！ %s", html[n]);
                continue;
            }
            Logger.print(html[n] + "處理中");

            try {
                while ((temp = reader.readLine()) != null) { // 一次讀取一行
                    temp = temp.trim();
                    switch (stage) {
                        case Parpare:
                            // System.out.println(">>>>> 找到本文！");
                            // 第一階段，找出本文主體
                            if (temp.indexOf("class=\"pbody") >= 0) {
                                stage = NovelBodyParseStage.EnterBody;
                            }
                            break;
                        case EnterBody:
                            // 第二階段，找出標題
                            System.out.println(">>>>> 找到標題！");
                            if (temp.indexOf("<h") >= 0) {// 出現標題

                                // temp = Replace.replace(temp, " ", "");
                                temp.replaceAll(" ", "");

                                temp = replaceHtmlTags(temp);

                                // bookData.append(temp);
                                // bookData.append(lineSeparator);
                                appendLine(temp);
                            }

                            if (temp.indexOf("<div class=\"mes") >= 0) {
                                // System.out.println(temp);
                                stage = NovelBodyParseStage.EnterArticleBegin;
                            }
                            break;
                        case EnterArticleBegin:
                            // 第三階段，找到文章的開頭
                            System.out.println(">>>>> 找到文章的開頭！" + temp);

                            if (temp.indexOf("class=\"postmessage\">") >= 0) {// 找出
                                // 文章內容
                                // System.out.println(temp);
                                stage = NovelBodyParseStage.EnterArticle;
                                // String[] temp2 =
                                // temp.split("class=\"postmessage\">");// 接取標題
                                // if(temp2.length<=0)
                                // temp = temp2[1];

                                if (temp.indexOf("<div class=\"quote\">") >= 0) {
                                    // 過濾引用
                                    otherTable++;
                                }

                                parseMessageBodyStart(temp);

                                // 如果有
                                // 會有內容，如果沒有是空字串
                            }
                            break;
                        case EnterArticle:
                            // 第四階段，處理本文內容
                            System.out.println(">>>>> 處理本文內容 " + temp);

                            // 判斷是否到了結尾，要準備離開
                            if (temp.indexOf("<div ") >= 0) // 避免碰到下一階層
                            {
                                System.out.println(">>>>> 發現 div，進入下一層");
                                otherTable++;
                            }

                            if (temp.indexOf("</div>") >= 0) {
                                if (otherTable > 0) // 從底層離開
                                {
                                    System.out.println(">>>>> 發現結尾，回到上一層");
                                    otherTable--;
                                }
                                else {
                                    System.out.println(">>>>> 發現結尾，離開");
                                    // 偵測是否離開了
                                    temp = temp.replace("</div>", " ");
                                    stage = NovelBodyParseStage.Parpare;
                                    flag = false;
                                    // temp += lineSeparator + lineSeparator + lineSeparator;
                                }
                            }

                            if (true) {
                                parseMessageBodyLine(temp);
                            }

                            break;
                        default:
                            break;
                    }
                }
            }
            catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            appendLine();

            try {
                reader.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return toString();
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
