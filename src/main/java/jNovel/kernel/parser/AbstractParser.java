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

    protected void appendLine(String line) {

        // System.out.println(line);
        bookData.append(line);
    }

    protected void appendLine() {

        bookData.append(lineSeparator);
    }

    @Override
    public String toString() {

        String bookDataStr = bookData.toString();
        // final format better text for read
        bookDataStr = bookDataStr
                .replaceAll("\n\n\n\n", "\n")
                .replaceAll("\n\n\n", "\n")
                    .replaceAll("\n\n", "\n")
                    .replaceAll("\n", "\n\n")
                    .replaceAll("\r", "\r\n");

        // System.out.println(bookData.toString());
        if (this.encoding) {
            return encoder.StoT(bookDataStr);
        }
        else {
            return encoder.TtoS(bookDataStr);
        }
    }

}
