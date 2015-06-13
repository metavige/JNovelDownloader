package jNovel.kernel;

import jNovel.kernel.parser.INovelParser;
import jNovel.kernel.parser.ParserFactory;

public class MakeBookThread extends Thread {

    private String[] html;
    private boolean encoding;
    private String result;
    private int type;

    private INovelParser parser;

    public MakeBookThread(String[] data, boolean encoding, int type) {

        html = data;
        this.encoding = encoding;
        this.type = type;
    }

    public void run() {

        System.out.println(type);
        
        parser = ParserFactory.getParser(type, encoding);
        this.result = this.parser.parse(this.html);
    }

    public String getResult() {

        return result;
    }

}
