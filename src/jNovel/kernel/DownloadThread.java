package jNovel.kernel;

import jNovel.kernel.utils.ConsoleLogger;
import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.IMessageLogger;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {

    private String[] from;
    private String[] to;
    public boolean downloadstate;
    private int threatNember;
    // private String sessionId = null;
    private IMessageLogger messager = new ConsoleLogger();

    public DownloadThread(String[] from, String[] to, int t) {

        this.from = from;
        this.to = to;
        this.threatNember = t;
        downloadstate = true;
    }

    public DownloadThread(String from, String to, int t) {

        this.from = new String[1];
        this.to = new String[1];
        this.from[0] = from;
        this.to[0] = to;
        this.threatNember = t;
        downloadstate = true;
    }

    /*
     * public DownloadThread(String[] from, String[] to, int t, String sessionId) {
     * this.from = from;
     * this.to = to;
     * this.threatNember = t;
     * downloadstate = true;
     * this.sessionId = sessionId;
     * }
     * 
     * public DownloadThread(String from, String to, int t, String sessionId) {
     * this.from = new String[1];
     * this.to = new String[1];
     * this.from[0] = from;
     * this.to[0] = to;
     * this.threatNember = t;
     * downloadstate = true;
     * this.sessionId = sessionId;
     * }
     */
    public DownloadThread(String[] from,
                          String[] to,
                          int t,
                          String sessionId,
                          IMessageLogger messager) {

        this.from = from;
        this.to = to;
        this.threatNember = t;
        downloadstate = true;
        // this.sessionId = sessionId;
        this.messager = messager;
    }

    public DownloadThread(String from, String to, int t, String sessionId, IMessageLogger messager) {

        this.from = new String[1];
        this.to = new String[1];
        this.from[0] = from;
        this.to[0] = to;
        this.threatNember = t;
        downloadstate = true;
        // this.sessionId = sessionId;
        this.messager = messager;
    }

    public DownloadThread(String[] from, String[] to, int t, IMessageLogger messager) {

        this.from = from;
        this.to = to;
        this.threatNember = t;
        downloadstate = true;
        this.messager = messager;
    }

    public DownloadThread(String from, String to, int t, IMessageLogger messager) {

        this.from = new String[1];
        this.to = new String[1];
        this.from[0] = from;
        this.to[0] = to;
        this.threatNember = t;
        downloadstate = true;
        this.messager = messager;
    }

    public void run() {

        int downloadmiss = 0;
        for (int n = 0; n < this.to.length; n++) {
            StringBuffer total = new StringBuffer();

            try {
                messager.print("開始下載檔案: " + from[n]);

                URL url = new URL(from[n]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // if (from[n].indexOf("eyny") >= 0) {

                connection.setDoOutput(true);//
                switch (threatNember) {
                    case 0:
                        connection
                                .setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (Linux; U; Android 4.0.3; zh-tw; HTC_Sensation_Z710e Build/IML74K)AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
                        break;
                    case 1:
                        connection
                                .setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0; SAMSUNG; OMNIA7)　");
                        break;
                    case 2:
                        connection
                                .setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A5313e Safari/7534.48.3");
                        break;
                    case 3:
                        connection
                                .setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (Linux; Android 4.2.2; Nexus 7 Build/JDQ39) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
                        break;
                    default:
                        connection
                                .setRequestProperty("User-Agent",
                                        "Mozilla/5.0 (Linux; Android 4.2.2; Nexus 7 Build/JDQ39) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19");
                        break;
                }

                connection.connect();

                BufferedReader reader = FileUtils.ReadFileFromStream(connection.getInputStream());

                // InputStream inStream = (InputStream) connection.getInputStream();
                // BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,
                // "utf8"));
                String line = "";
                String lineSeparator = System.getProperty("line.separator");
                while ((line = reader.readLine()) != null) {
                    total.append(line + lineSeparator);
                }
                // System.out.println("檔案："+total);
            }
            catch (Exception e) {
                e.printStackTrace();
                messager.print("取得網頁html時發生錯誤....");

                if (downloadmiss > 20) {
                    downloadmiss++;
                    System.out.println("等待一秒嘗試重新下載....");
                    if (messager != null) {
                        messager.print("等待一秒嘗試重新下載....");
                    }
                    n--;
                    try {
                        sleep(1000);

                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                else {
                    messager.print("沒救了....");
                    downloadstate = false;
                    return;
                }

            }
            try {
                FileUtils.WriteData(to[n], total.toString());
                
                messager.print("下載完成");
            }
            catch (Exception e) {
                // TODO: handle exception
                downloadstate = false;
                return;
            }

        }
        return;
    }

}
