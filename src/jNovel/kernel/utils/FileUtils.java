package jNovel.kernel.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileUtils {

    public static final String DEFAULT_FILE_ENCODING = "UTF-8";
    public static String LineSeparator = System.getProperty("line.separator");
    public static String FileSeparator = System.getProperty("file.separator");

    public static BufferedReader readFile(File file) {

        try {
            return readFileFromStream(new FileInputStream(file));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } // 開啟檔案
    }

    public static BufferedReader readFile(String filename) {

        return readFile(new File(filename));
    }

    public static BufferedReader readFileFromStream(InputStream inStream)
        throws UnsupportedEncodingException {

        return new BufferedReader(new InputStreamReader(inStream, DEFAULT_FILE_ENCODING));
    }

    public static void writeData(String filename, String data) throws IOException {

        writeData(new File(filename), data);
    }

    public static void writeData(File file, String data) throws IOException {

        OutputStreamWriter writer = getWriteStream(file);
        writer.write(data);
        writer.flush();
        writer.close();
    }

    public static OutputStreamWriter getWriteStream(File file)
        throws UnsupportedEncodingException,
            FileNotFoundException {

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                DEFAULT_FILE_ENCODING);
        return writer;
    }

    public static OutputStreamWriter getWriteStream(String filename)
        throws UnsupportedEncodingException,
            FileNotFoundException {

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename),
                DEFAULT_FILE_ENCODING);
        return writer;
    }
}
