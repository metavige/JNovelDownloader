package jNovel.kernel.parser;

import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EynyParser extends AbstractParser implements INovelParser {

  public EynyParser() {

    // TODO Auto-generated constructor stub
  }

  public EynyParser(boolean encoding) {

    // TODO Auto-generated constructor stub
  }

  public void parseMessageBodyStart(String lineString) {
  }

  /**
   * 處理本文的每一行資料
   *
   * @param lineString
   * @param p_html
   * @param pModStamp
   */
  public void parseMessageBodyLine(String lineString) {

  }

  public String replaceHtmlTags(String lineString) {

    return "";
  }

  public String parse(String[] html) {

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

      BufferedReader reader = FileUtils.readFile(html[n]);

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
                  appendLine(temp);
                stage = 3;
              }
              break;
            case 3:
              m_html = pModStamp.matcher(temp);
              if (m_html.find())
                break;
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
                temp = temp + FileUtils.LineSeparator;
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
              parseMessageBodyLine(temp);

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

}
