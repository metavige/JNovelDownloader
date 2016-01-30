package jNovel.kernel.utils;


/**
 * 提供顯示介面的功能，有需要可以把資料顯示在不同的地方
 * 
 * @author rickychiang
 *
 */
public interface IMessageLogger {

    public void printf(String format, Object... params);

    public void print(String message);

}
