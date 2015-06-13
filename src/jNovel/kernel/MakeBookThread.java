package jNovel.kernel;

import jNovel.kernel.parser.INovelParser;
import jNovel.kernel.parser.NovelBodyParseStage;
import jNovel.kernel.parser.ParserFactory;
import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeBookThread extends Thread {

    private String[] html;
    private StringBuilder bookData;
    private BufferedReader reader;
    private boolean encoding;
    private String result;
    private int type;
    private String lineSeparator;

    private INovelParser parser;

    public MakeBookThread(String[] data, boolean encoding, int type) {

        html = data;
        bookData = new StringBuilder();
        this.encoding = encoding;
        this.type = type;

        this.lineSeparator = System.getProperty("line.separator");
    }

    public void run() {

        System.out.println(type);

        parser = ParserFactory.getParser(type, encoding);

        if (type == 1) {
            this.runType1();
        }
        else {
            this.runType0();
        }
    }

    private void runType1() {

        // archiver給伊莉用
        // 參考 網友D.A.R.K
        // http://uneedanapple.blogspot.tw/2013/09/android-app-noveldroid.html
        // 的程式碼
        // boolean inContent = false;
        int stage = 0; // 0=不再內文中 ,1=在<div class="pbody"> 中,
        // 2=在<div class="mes">中 ,
        // 3=<div id="postmessage_~~~~" class="mes">中 ,
        /**
         * 0: not in article 主題開頭 1: in author section 處理作者 2: in title section
         * 處理標題 3: in article section 處理內文
         */
        int end;
        String temp;

        // int otherTable = 0;
        /* 用於正規表示式的過濾，比replace all 快速準確 */

        Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
        Pattern pTitle = Pattern.compile("<h3>(.+)?</h3>");
        Pattern pModStamp = Pattern
                .compile(" 本帖最後由 \\S+ 於 \\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2} (\\S{2} )?編輯 ");

        // String regEx_html = "<[^>]+>";

        for (int n = 0; n < html.length; n++) {
            
            reader = FileUtils.ReadFile(html[n]);
            
            if (reader == null) {
                Logger.printf("無法讀取檔案！ %s", html[n]);
                continue;
            }
            Logger.print(html[n] + "處理中");

            try {
                while ((temp = reader.readLine()) != null) { // 一次讀取一行
                    temp = temp.trim();

                    Matcher m_html;

                    switch (stage) {
                        case 0:
                            if (temp.indexOf("<p class=\"author\">") >= 0) {
                                stage = 1;
                            }
                            break;
                        case 1:
                            if (temp.indexOf("</p>") >= 0) {//
                                stage = 2;
                            }
                            break;
                        case 2: // 應該是處理標題
                            m_html = pTitle.matcher(temp);
                            if (m_html.find()) {
                                if ((temp = m_html.group(1)) != null) // 分組0
                                    // <h3>1</h3>2
                                    // bookData.append(temp + lineSeparator);
                                    parser.appendLine(temp);
                                stage = 3;
                            }
                            break;
                        case 3:
                            m_html = pModStamp.matcher(temp);
                            if (m_html.find()) break;
                            if (temp.indexOf("<p class=\"author\">") >= 0) {
                                stage = 1;
                                // break;
                            }
                            if ((end = temp.indexOf("...&lt;div class='locked'")) == 0) {
                                if (temp.indexOf("<p class=\"author\">") >= 0) {
                                    stage = 1;
                                }
                                else {
                                    stage = 0;
                                }
                                break;
                            }

                            if (end > 0) {
                                if (temp.indexOf("<p class=\"author\">") >= 0) {
                                    stage = 1;
                                }
                                else {
                                    stage = 0;
                                }
                                temp = temp.substring(0, end);
                                temp = temp + lineSeparator;
                            }
                            //
                            // temp = Replace.replace(temp, "&nbsp;", "");
                            // temp = Replace.replace(temp, "<br/>", lineSeparator);
                            // temp = Replace.replace(temp, "<br />", lineSeparator);
                            // m_html = p_html.matcher(temp);
                            // temp = m_html.replaceAll("");
                            // temp = temp.replaceAll("^[ \t　]+", "");
                            // // if (temp.length() > 2)
                            // // temp = "　　" + temp;
                            // bookData.append(temp);
                            parser.parseMessageBodyLine(temp);

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
            // bookData.append(lineSeparator);
            parser.appendLine();

            try {
                reader.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Encoding encoding = new Encoding();
        if (this.encoding) {
            result = encoding.StoT(bookData.toString());
        }
        else {
            result = encoding.TtoS(bookData.toString());
        }
    }

    private void runType0() {

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
            
            reader = FileUtils.ReadFile(html[n]);
            
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

                                temp = parser.replaceHtmlTags(temp);

                                // bookData.append(temp);
                                // bookData.append(lineSeparator);
                                parser.appendLine(temp);
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

                                parser.parseMessageBodyStart(temp);

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
                                parser.parseMessageBodyLine(temp);
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

            // bookData.append(lineSeparator);
            parser.appendLine();

            try {
                reader.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        result = parser.toString();

        // Encoding encoding = new Encoding();
        //
        // // System.out.println(bookData.toString());
        // if (this.encoding) {
        // result = encoding.StoT(bookData.toString());
        // }
        // else {
        // result = encoding.TtoS(bookData.toString());
        // }
    }

    public String getResult() {

        return result;
    }

}
