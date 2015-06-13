package jNovel.kernel.parser;

import jNovel.kernel.ChineseTransfer;

public abstract class AbstractParser implements INovelParser {

    protected StringBuilder bookData;
    protected boolean encoding;
    private ChineseTransfer encoder = new ChineseTransfer();
    protected String lineSeparator;

    public AbstractParser() {

        super();
        this.lineSeparator = System.getProperty("line.separator");
    }

    @Override
    public void appendLine(String line) {

        // System.out.println(line);
        bookData.append(line).append(lineSeparator);
    }

    @Override
    public void appendLine() {

        bookData.append(lineSeparator);
    }

    @Override
    public String toString() {

        // System.out.println(bookData.toString());
        if (this.encoding) {
            return encoder.StoT(bookData.toString());
        }
        else {
            return encoder.TtoS(bookData.toString());
        }
    }

}
