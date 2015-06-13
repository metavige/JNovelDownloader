package jNovel.option;

import jNovel.kernel.utils.FileUtils;
import jNovel.kernel.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Option {

    public String tempPath;// 暫存區
    public boolean encoding;// 1-繁體 0=簡體
    public boolean replace;// 是否要做替換
    public String novelPath;// 小說存放位置
    private File file;
    public int threadNumber;

    public Option() throws IOException {

        file = new File("option.ini");
        File tempFile;
        String temp;
        String[] temp2;

        if (ifNoSetUp()) {

            BufferedReader reader = FileUtils.ReadFile(file);
            
            temp = reader.readLine();
            temp2 = temp.split("-");
            tempPath = temp2[1];
            temp = reader.readLine();
            temp2 = temp.split("-");
            novelPath = temp2[1];
            temp = reader.readLine();
            temp2 = temp.split("-");
            encoding = Boolean.valueOf(temp2[1]);
            temp = reader.readLine();
            temp2 = temp.split("-");
            replace = Boolean.valueOf(temp2[1]);
            if ((temp = reader.readLine()) != null) {
                temp2 = temp.split("-");
                threadNumber = Integer.parseInt(temp2[1]);
            }
            else threadNumber = 4;
            reader.close();
            /**檢查檔案路徑是否存在***/
            tempFile = new File(tempPath);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
            tempFile = new File(novelPath);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        else {
            setUp();
            creatOptionfile();
        }

    }

    private void setUp() {// 設定初始化

        File temp = new File("");
        String fileSeparator = System.getProperty("file.separator");
        tempPath = temp.getAbsolutePath() + fileSeparator + "temp" + fileSeparator;
        novelPath = temp.getAbsolutePath() + fileSeparator + "down" + fileSeparator;
        encoding = true; // 預設繁體
        replace = false; // 預設不處理
        threadNumber = 4;
        temp = new File(tempPath);
        temp.mkdir();
        temp = new File(novelPath);
        temp.mkdir();

    }

    private boolean ifNoSetUp() {

        return file.exists();
    }

    private void creatOptionfile() throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("tempPath-" + tempPath + FileUtils.LineSeparator);
        sb.append("novelPath-" + novelPath + FileUtils.LineSeparator);
        sb.append("encoding-" + String.valueOf(encoding) + FileUtils.LineSeparator);
        sb.append("replace-" + String.valueOf(replace) + FileUtils.LineSeparator);
        sb.append("threadNumber-" + String.valueOf(threadNumber) + FileUtils.LineSeparator);
        
        FileUtils.WriteData(file, sb.toString());
    }

    public void saveOption() throws IOException {

        file.delete();

        StringBuilder sb = new StringBuilder();
        sb.append("tempPath-" + tempPath + FileUtils.LineSeparator);
        sb.append("novelPath-" + novelPath + FileUtils.LineSeparator);
        sb.append("encoding-" + String.valueOf(encoding) + FileUtils.LineSeparator);
        sb.append("replace-" + String.valueOf(replace) + FileUtils.LineSeparator);
        sb.append("threadNumber-" + String.valueOf(threadNumber) + FileUtils.LineSeparator);
        
        FileUtils.WriteData(file, sb.toString());
        
        /**檢查檔案路徑是否存在***/
        File tempFile = new File(tempPath);
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
        tempFile = new File(novelPath);
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
    }

    public void printOption() {

        Logger.print("暫存檔位置：" + tempPath);
        Logger.print("小說存放位置：" + novelPath);
        Logger.print((encoding) ? "正體中文" : "簡體中文");
        Logger.print("多執行序數目：" + threadNumber);
    }

    public boolean checkPath() {

        boolean result = true;

        File file = new File(this.tempPath);
        if (!file.exists()) {
            File temp1 = new File("");
            this.tempPath = temp1.getAbsolutePath() + FileUtils.FileSeparator + "temp" + FileUtils.FileSeparator;
            result = false;
        }
        file = new File(this.novelPath);
        if (!file.exists()) {
            // File temp1 =new File("");
            // this.tempPath=temp1.getAbsolutePath()+"\\temp\\";
            result = false;
        }

        return result;
    }

    // private void creatOption

}
