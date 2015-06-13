package jNovel;

import jNovel.kernel.Downloader;
import jNovel.kernel.ReadHtml;
import jNovel.kernel.utils.Logger;
import jNovel.option.Option;
import jNovel.ui.Frame;

import java.io.IOException;

import javax.swing.JFrame;

public class main {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        // TODO Auto-generated method stub
        Option option = new Option();
        Downloader downloader = new Downloader();
        ReadHtml readHtml = new ReadHtml();
        
        if (args.length > 0) {

            String bookName = args[0];
            String authorName = args[1];
            String url = args[2];
            int page = (args.length > 2) ? Integer.parseInt(args[3]) : 0;
            
            downloader.setup(page, url);// 分析網址
            readHtml.setup(option.threadNumber, bookName, authorName, downloader.getUrlData());
            
            Logger.print("開始下載");

            try {
                if (!downloader.downloading(option, readHtml)) {// 開始下載
                    Logger.print("下載失敗");
                }
                else {
//                    donTime = System.currentTimeMillis() - startTime;
                    if (readHtml.makeBook(option)) {
                        // 開始解析所有的網頁
                        Logger.print("小說製作完成");
                        readHtml.delTempFile();
                        Logger.print("清除暫存檔");

//                        totTime = System.currentTimeMillis() - startTime;

//                        Logger.printf("總共花費 %d ms ; 其中下載花費 %d ms; \n資料處理花費 %d ms",
//                                        totTime,
//                                        donTime,
//                                        (totTime - donTime));
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            Frame frame = new Frame(downloader, readHtml, option);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 350);
            frame.setVisible(true);
            frame.popVersionAlert(option);
            if (!option.checkPath()) {
                frame.popPathAlert();
            }
        }
        
        // int[][] d=new int[5][2];

        // String test1 ="第一章 殺手不好做，咱轉行吧";
        // System.out.println(test1.matches("第[一二三四五六七八九十百零1234567890 　]*章 [^<>]*"));

        // String[] temp2 = test1.split("class=\"postmessage\">");// 接取標題
        // // 如果有
        // // 會有內容，如果沒有是空字串
        // for(int n=0;n<temp2.length;n++){
        // System.out.println(n+"-"+temp2[n]+"\r\n");
        // }
        // String test2 = test1.replaceAll("<[^>]+>", "");
        // test1 = test1.replace("<[^>]+>", "");
        // test1=Replace.replace(test1, "<[^>]+>", "");
        // String[] test2=test1.split("/");
        // System.out.println(test1);
        // System.out.println(test2);
        // for(int n=0;n<test2.length;n++){
        // System.out.println(n+":"+test2[n]);
        // }
        // String test1 = "<title>叱吒風雲    作者:高樓大廈  (已完成) - 第55頁 - 長篇小說 -  卡提諾 - </title>";
        // String[] tempTittleString = test1.split("<title>|</title>");
        // String bookName = tempTittleString[1];
        // String test2 = test1.replaceAll("<[/]?title>", "");
        // System.out.println(test2);
        // String test2 =
        // "<link href=\"http://ck101.com/thread-1753100-1-1.html\" rel=\"canonical\" />";
        // String test3 = "http://";
        // System.out.println(test1.indexOf("<title>"));
        // System.out.println(test2.indexOf("<title>"));
        // System.out.println(test3.equals(test1.substring(0, 7)));
        // System.out.println(test1.compareTo(test3));
    }
}
