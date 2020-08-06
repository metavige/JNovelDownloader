package jNovel;

import jNovel.kernel.Downloader;
import jNovel.kernel.ReadHtml;
import jNovel.kernel.utils.Logger;
import jNovel.option.Option;
import jNovel.ui.Frame;

import javax.swing.*;
import java.io.IOException;

public class Main {

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
  }
}
