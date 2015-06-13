package jNovel.kernel.parser;


public class ParserFactory {
    
    public static INovelParser getParser(int type, boolean encoding) {
        if (type == 1) {
            return new EynyParser(encoding);
        }
        else {
            return new Ck101Parser(encoding);
        }
    }
}
