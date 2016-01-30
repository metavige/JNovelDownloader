package jNovel.kernel.utils;

import java.util.Map;

public class RegexUtils {

    public static String replace(String originalStr, Map<String, String> replacements) {

        String replStr = originalStr;

//        replacements.forEach((k,v) -> {
//            replStr = replStr.replaceAll(k, v);
//        });
        
        for (String key : replacements.keySet()) {
            try {

                replStr = replStr.replaceAll(key, replacements.get(key));
            }
            catch (Exception ex) {
                Logger.printf("轉換時發生錯誤 : %s - %s", key, replacements.get(key));
            }
        }

        return replStr;
    }
}
