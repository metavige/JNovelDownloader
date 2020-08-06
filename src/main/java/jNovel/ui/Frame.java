package jNovel.ui;

import jNovel.kernel.Analysis;
import jNovel.kernel.DownloadThread;
import jNovel.kernel.Downloader;
import jNovel.kernel.ReadHtml;
import jNovel.kernel.Replace;
import jNovel.kernel.UrlData;
import jNovel.kernel.utils.IMessageLogger;
import jNovel.kernel.utils.Logger;
import jNovel.option.About;
import jNovel.option.Option;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Frame extends JFrame implements IMessageLogger {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private JTextField urlTextField;
  private JTextField authorTextField;
  private JTextField bookNameTextField;
  private JFormattedTextField pageTextField;

  private JButton downloadButton;
  private JLabel urlLabel;
  private JLabel authorLabel;
  private JLabel bookNameLabel;
  private JLabel pageLabel;
  private JPanel urlPanel;
  private JPanel downloadPanel;
  private JPanel bookNamePanel;
  private JTextArea resultTextArea;
  private JScrollPane resultScrollPane;
  private JPanel resultPanel;
  private JButton settingButton;
  private double theNewVersion;

  private Option option;
  private Downloader downloader;
  private ReadHtml readHtml;

  public Frame(final Downloader downloader, final ReadHtml readHtml, final Option option)
    throws Exception {

    super(About.tittle + "-" + About.version + "  by " + About.author);

    // Save arguments
    this.option = option;
    this.downloader = downloader;
    this.readHtml = readHtml;

    // 改變預設 logger，變成自己，把 message 導向到 JTextArea
    Logger.setupLogger(this);
    // =============================================
    // all swing items initialize
    // =============================================
    initLabels();
    initTextFields();
    initButtons(option);

    // =============================================
    // layout settings
    // =============================================
    initLayout();

    Logger.print("啟動中...");

    // For Test
    pageTextField.setText("1");
    bookNameTextField.setText("殖裝");
    authorTextField.setText("鉛筆刀");
    urlTextField.setText("http://ck101.com/thread-2029796-1-1.html");

    option.printOption();// 印出初始訊息
  }

  private void initLayout() {

    setLayout(new FlowLayout()); // set frame layout

    add(settingButton);

    bookNamePanel = new JPanel();
    bookNamePanel.add(bookNameLabel);
    bookNamePanel.add(bookNameTextField);

    bookNamePanel.add(authorLabel);
    bookNamePanel.add(authorTextField);
    add(bookNamePanel);

    urlPanel = new JPanel();
    urlPanel.add(urlLabel);
    urlPanel.add(urlTextField);
    add(urlPanel);

    downloadPanel = new JPanel();
    downloadPanel.add(pageLabel);
    downloadPanel.add(pageTextField);
    downloadPanel.add(downloadButton);
    add(downloadPanel);

    resultPanel = new JPanel();
    resultScrollPane = new JScrollPane(resultTextArea);
    resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    resultPanel.add(resultScrollPane);
    add(resultPanel);
  }

  private void initButtons(final Option option) {

    /********************** 設定 ***************************/
    settingButton = new JButton("設定");
    settingButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {

        // TODO Auto-generated method stub
        OptionFrame frame = new OptionFrame(option);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 200);
        frame.setVisible(true);
      }
    });

    downloadButton = new JButton("下載");
    // 把下載按鈕的事件指向自己
    downloadButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        new Thread(new Runnable() {

          @Override
          public void run() {
            MakeBook();
          }
        }).start();
      }
    });
  }

  private void initTextFields() {

    /********************** 設定書名 ***************************/
    authorTextField = new JTextField("", 20);
    bookNameTextField = new JTextField("", 20);
    /********************** 網址輸入 ***************************/
    urlTextField = new JTextField("", 50); // 網址輸入視窗

    // 改成只能輸入 number
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
    decimalFormat.setGroupingUsed(false);
    pageTextField = new JFormattedTextField(decimalFormat);
    pageTextField.setColumns(4); // whatever size you wish to set
    pageTextField.setText("1");

    resultTextArea = new JTextArea(8, 50);// 訊息視窗
    resultTextArea.setLineWrap(true);
  }

  private void initLabels() {

    bookNameLabel = new JLabel("小說名稱");
    authorLabel = new JLabel("作者");
    urlLabel = new JLabel("網址：");
    pageLabel = new JLabel("下載到第幾頁?");
  }

  public void MakeBook() {

    double startTime, donTime, totTime;
    startTime = System.currentTimeMillis();
    try {
      String page = pageTextField.getText();
      String bookName = bookNameTextField.getText();
      String authorName = authorTextField.getText();
      String url = urlTextField.getText();

      // 確認所有該填的資料都有填寫
      if (check(option, page, bookName, authorName, url)) {

        // 下載、建書兩大元件初始化
        Logger.print("初始化...");

        downloader.setup(Integer.parseInt(page), url);// 分析網址
        readHtml.setup(option.threadNumber, bookName, authorName, downloader.getUrlData());
        //
        Logger.print("開始下載");

        try {
          if (!downloader.downloading(option, readHtml)) {// 開始下載
            Logger.print("下載失敗");
          }
          else {
            donTime = System.currentTimeMillis() - startTime;
            if (readHtml.makeBook(option)) {
              // 開始解析所有的網頁
              Logger.print("小說製作完成");
              readHtml.delTempFile();
              Logger.print("清除暫存檔");

              totTime = System.currentTimeMillis() - startTime;

              Logger.printf("總共花費 %d ms ; 其中下載花費 %d ms; \n資料處理花費 %d ms",
                totTime,
                donTime,
                (totTime - donTime));
            }
          }
        }
        catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      else {
        Logger.print("請檢查填寫的資料是否有錯");
      }
    }
    catch (Exception e) {
      // TODO 自動產生的 catch 區塊
      e.printStackTrace();
    }
  }

  public void popVersionAlert(Option option) {

    try {
      theNewVersion = checkVersion(option);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (theNewVersion > About.versionNumber) {
      Logger.printf("本軟體最新版本為 %s 請至http://www.pupuliao.info 下載最新版本", theNewVersion);
    }
    else {
      Logger.printf("目前最新版本：%s ", theNewVersion);
    }
    if (theNewVersion > About.versionNumber) {
      JOptionPane.showMessageDialog(null, String
        .format("本軟體最新版本為 %s 請至官網下載最新版本. 有更新版本喔!! %s",
          theNewVersion,
          JOptionPane.WARNING_MESSAGE));
    }
  }

  private boolean check(Option option, String page, String bookName, String author, String url)
    throws IOException {

    if (page.isEmpty() || bookName.isEmpty() || author.isEmpty()) {
      UrlData urlData = Analysis.analysisUrl(url);
      if (urlData.wrongUrl) {
        Logger.print("網址有問題 無法分析");
        return false;
      }
      else {
        int p = getPage(option, url);
        if (page.isEmpty() || !page.matches("[1-9][0-9]*")) {
          pageTextField.setText(String.valueOf(p));
        }
        if (bookName.isEmpty()) {
          bookNameTextField.setText(getTittle(option));
        }
        if (author.isEmpty()) {
          authorTextField.setText("預設作者");
        }
      }

    }

    return true;
  }

  private double checkVersion(Option option) throws Exception {

    // String targetURL =
    // "http://code.google.com/p/jnoveldownload/downloads/list";
    String targetURL = "http://sourceforge.net/projects/jnoveldownload/files";
    String to = option.tempPath + "version.html";
    double version = 0;
    DownloadThread downloadThread = new DownloadThread(targetURL, to, 0);
    try {
      downloadThread.start();
      downloadThread.join();
    }
    catch (Exception e) {
      // TODO: handle exception
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(to),
      "UTF-8"));
    // <a href="detail?name=JNovelDownloader_v2_1.jar&amp;can=2&amp;q=">
    String temp;
    while ((temp = reader.readLine()) != null) {
      if (temp.indexOf("<tr title=\"JNovelDownloader_v") >= 0) {
        String temp2[] = temp.split("_");
        version = Double.parseDouble(temp2[1].charAt(1) + "." + temp2[2].charAt(0));
        break;
      }
    }
    reader.close();

    return version;
  }

  public void popPathAlert() {

    JOptionPane.showMessageDialog(null,
      "您的小說下載路徑或是暫存路徑有問題，請選擇[設定]重新設定",
      "路徑有問題",
      JOptionPane.WARNING_MESSAGE);
  }

  /**
   * 取得網頁中設定的頁數
   *
   * @param option
   * @param url
   * @return
   * @throws IOException
   */
  private int getPage(Option option, String url) throws IOException {

    int result = 0;
    DownloadThread downloadthread = new DownloadThread(url, option.tempPath + "/temp.html", 1);
    downloadthread.start();
    try {
      downloadthread.join();
    }
    catch (InterruptedException e) {

    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(option.tempPath
      + "/temp.html"),
      "UTF-8"));
    String temp;
    String temp2[];
    while ((temp = reader.readLine()) != null) {
      if (temp.indexOf("class=\"pg\"") >= 0) {
        if (temp.indexOf("class=\"last\"") >= 0) {
          temp2 = temp.split("class=\"last\">.. ");
          temp2 = temp2[1].split("</a>");
          result = Integer.parseInt(temp2[0]);
        }
        else
          if (temp.indexOf("class=\"nxt\"") >= 0) {
            temp2 = temp.split("class=\"nxt\"");
            temp2 = temp2[0].split("<a href");
            temp2 = temp2[temp2.length - 2].split("</a>");
            temp2 = temp2[0].split(">");
            result = Integer.parseInt(temp2[1]);
          }
          else
            if (temp.indexOf("<strong>") >= 0) {
              temp2 = temp.split("<strong>");
              temp2 = temp2[1].split("</strong>");
              result = Integer.parseInt(temp2[0]);
            }
        break;
      }
    }
    reader.close();
    return result;
  }

  private String getTittle(Option option) throws IOException {// 必須要先執行過getPage

    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(option.tempPath
      + "/temp.html"),
      "UTF-8"));
    String temp;
    String temp2[];
    String result = null;
    while ((temp = reader.readLine()) != null) {
      if (temp.indexOf("<title>") >= 0) {
        temp2 = temp.split("title>");
        temp2 = temp2[1].split(" - ");
        result = temp2[0];
        // result = Replace.replace(result, "【", "[");
        // result = Replace.replace(result, "】", "]");
        result = Replace.replace(result, ":", "");
        result = Replace.replace(result, " ", "");
        break;
      }
    }
    reader.close();
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see jNovel.ui.IMessageDisplayer#printMessage(java.lang.String, java.lang.Object)
   */
  @Override
  public void printf(String format, Object... params) {

    print(String.format(format, params));
  }

  /*
   * (non-Javadoc)
   *
   * @see jNovel.ui.IMessageDisplayer#printMessage(java.lang.String)
   */
  @Override
  public void print(String message) {

    if (resultTextArea == null) {
      System.out.println(message);
    }
    else {
      resultTextArea.append(message + "\r\n");
      resultTextArea.setCaretPosition(resultTextArea.getText().length());
    }
  }
}
