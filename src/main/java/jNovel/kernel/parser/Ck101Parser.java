package jNovel.kernel.parser;

import jNovel.kernel.utils.Logger;
import jNovel.kernel.utils.RegexUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * 解析文章內容 - For ck101.com
 * 
 * @author rickychiang
 *
 */
public class Ck101Parser extends AbstractParser implements INovelParser {

    Map<String, String> pageTitleData;
    Map<String, String> rpData;

    public Ck101Parser() {

        this(true);
    }

    public Ck101Parser(boolean encoding) {

        this.bookData = new StringBuilder();
        this.encoding = encoding;

        rpData = new LinkedHashMap<String, String>() {

            {
                // 替換掉一般的特殊字元以及全型空白
                put("　", "");
                put("\u00A0", "");
                put("\u0020", "");
                // 把一些與 Markdown 有關的符號轉換成全型
                put("\\*", "＊");
                put("-", "─");
                put("=", "＝");
                put("#", "＃");
            }
        };

        pageTitleData = new LinkedHashMap<String, String>() {

            {
                // 用來替換標題用，主要是 for Markdown calibre
                put("^第(.*)([節章篇卷])(.*)第一([節章篇卷幕])(.*)$", "# 第$1$2 $3\n## 第$1$2 $3 第一$4 $5");
                put("^第(.*)([節章篇卷])(.*)第(.*)([節章篇卷幕])(.*)$", "\n## 第$1$2 $3 第$4$5 $6");
                put("^第(.*)([節章篇卷幕])(.*)$", "\n# 第$1$2 $3");
                put("  ", " ");
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see jNovel.kernel.parser.INovelParser#parse(java.lang.String[])
     * 
     * 改採用 jgroup 來解析 document, 直接重點的 postmessage
     * 所以就不需要再有階段處理了～
     */
    @Override
    public String parse(String[] html) {

        for (int n = 0; n < html.length; n++) {

            try {
                Document doc = Jsoup.parse(new File(html[n]), "UTF-8", "http://ck101.com/");
                Elements pageBodies = doc.select(".postmessage");

                // Logger.printf("Get PageBody Size: %d", pageBodies.size());
                for (Element pageElement : pageBodies) {
//                    Logger.printf(" ====== Page Block ====== %s ", html[n]);
                    // 移除編輯狀態
                    pageElement.select("i.pstatus").forEach(ele -> ele.remove());
                    pageElement.select("font[color=\"#999999\"]").forEach(ele -> ele.remove());
                    pageElement.select("img").forEach(ele -> ele.remove());

                    processElement(pageElement);
                }

            }
            catch (IOException e1) {
                e1.printStackTrace();
            }

            // try {
            // reader.close();
            // }
            // catch (IOException e) {
            // e.printStackTrace();
            // }
        }

        return toString();
    }

    /**
     * 處理每一個節點的資料，把它放進 Body 中
     * 
     * @param pageElement
     */
    private void processElement(Element pageElement) {

        // 抓到本文主體後，直接把所有 childnodes 取出，應該都是 textnode
        Node[] childrens = pageElement.childNodesCopy().toArray(new Node[] { });
//        boolean isPageStart = true;
//        Logger.printf("chilrens size: %d", childrens.length);

        for (int i = 0; i < childrens.length; i++) {
            Node node = childrens[i];
            String novelText = null;

            if (node.nodeName() == "#text") {
                novelText = ((TextNode) node).text();
            }
            else if (node.nodeName() == "br") {
                appendLine();
            }
            else {
                // appendLine(((Element) node).text());
                novelText = ((Element) node).text();
                Logger.printf("%d (%s) - %s, %s", i, node.childNodeSize(), node.nodeName(), node.outerHtml());
            }

            // 只有在前面檢查出需要加入，才做下面的動作
            if (novelText != null) {
                novelText = novelText.trim();

                // 根據定義的規則，替換掉相關的字串
                novelText = RegexUtils.replace(novelText, rpData);

                // 如果確認是每一篇主題的前幾行，應該都是每篇的標題，用標題的 regex 來替換
                if (i < 10) {
                    novelText = RegexUtils.replace(novelText, pageTitleData);
                }
                appendLine(novelText);
            }
        }
    }

}
