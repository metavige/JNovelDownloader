package jNovel.kernel;

import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;
import jNovel.option.Option;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ReadHtml {

    private String[][] fileName;
    public String bookName;
    public String author;
    private int havethread;
    // private BufferedReader reader;
    // private String path;
    private OutputStreamWriter writer;
    private String domain;

    public ReadHtml() {

        // TODO Auto-generated constructor stub

    }

    public ReadHtml(int page) {

        fileName = new String[page][];
        havethread = 0;
        bookName = null;
        // path = "";
    }

    public void setup(int threadNumber,
                      String bookName,
                      String author,
                      UrlData urlData) {

        fileName = new String[threadNumber][];

        this.bookName = bookName;
        this.author = author;
        havethread = 0;
        this.domain = urlData.domain;
    }

    public void setPage(int page) {

        fileName = new String[page][];
        havethread = 0;
    }

    public boolean makeBook(Option option) throws IOException {

        String filename = String.format("%s%s - %s%s", option.novelPath, author, bookName , ".txt");
        writer = FileUtils.getWriteStream(filename);
        
        writer.write(bookName + "\r\n" + author + "\r\n");

        Logger.print("開始分析網頁");

        int type;
        if (domain.indexOf("eyny") >= 0) {
            type = 1;
        }
        else type = 0;
        // Encoding encoding=new Encoding();
        
        MakeBookThread[] makeBookThreads = new MakeBookThread[option.threadNumber];
        for (int n = 0; n < option.threadNumber; n++) {
            makeBookThreads[n] = new MakeBookThread(fileName[n],
                    option.encoding,
                    type);
            makeBookThreads[n].start();
        }
        try {// 等全部跑完才繼續
            for (int x = 0; x < option.threadNumber; x++) {
                makeBookThreads[x].join();
            }
        }
        catch (InterruptedException e) {
        }
        for (int n = 0; n < option.threadNumber; n++) {
            Logger.printf("第 %s 頁 小說製作完成", n);
            writer.write(makeBookThreads[n].getResult());
        }
        writer.flush();
        writer.close();

        Logger.print("小說製作完成");

        return true;
    }

    public void addFileName(String[] temp) {// 輸入完整檔案路徑 改成一次一個array

        fileName[havethread++] = temp;
    }

    public void setBookName(String data) {

        bookName = data;
    }

    public void delTempFile() {

        Logger.print("刪除暫存檔中..");
        File temp;
        for (int n = 0; n < havethread; n++) {
            for (int m = 0; m < fileName[n].length; m++) {
                temp = new File(fileName[n][m]);
                if (temp.exists()) {
                    temp.delete();
                    Logger.printf("刪除檔案 ... %s", temp.getAbsolutePath());
                }
            }
        }

        Logger.print("刪除完畢");
    }

}
