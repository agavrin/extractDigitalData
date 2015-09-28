package com.ibm.testjs;

import sun.org.mozilla.javascript.internal.NativeObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class extractDigitalData {

    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // Load example of HTML5 page
        String myPage = loadPage("http://www.ibm.com/search/csa/v18searchc/?q=bluemix&hpp=20&o=0&co=us&lo=any");

        String myPageJSOnly = cleanPage(myPage);
        // evaluate script
        engine.eval(myPageJSOnly);

        // get script object on which we want to implement the interface with
        Object obj = engine.get("digitalData");
        if (obj instanceof NativeObject) {
            NativeObject nObj = (NativeObject) obj;
            Object pageObject = nObj.get("page");
            if (pageObject != null && pageObject instanceof NativeObject) {
                NativeObject pageNativeObject = (NativeObject) pageObject;
                Object pageInfoObject = pageNativeObject.get("pageInfo");
                if (pageInfoObject != null && pageInfoObject instanceof NativeObject) {
                    NativeObject pageInfoNativeObject = (NativeObject) pageInfoObject;
                    for (Object key : pageInfoNativeObject.getAllIds()) {
                        System.out.println("['" + key + "']='" + pageInfoNativeObject.get(key) + "'");
                        Object internalObject = pageInfoNativeObject.get(key);
                        if (internalObject != null && internalObject instanceof NativeObject) {
                            NativeObject internalNativeObject = (NativeObject) internalObject;
                            for (Object internalKey : internalNativeObject.getAllIds()) {
                                System.out.println("   ['" + internalKey + "']='" + internalNativeObject.get(internalKey) + "'");
                            }
                        }

                    }
                }

            }

        }
    }

    private static String cleanPage(String myPage) {

        StringBuilder sb = new StringBuilder(myPage);
        int start = sb.indexOf("<script");
        start = sb.indexOf(">", start) + 1;
        sb.delete(0, start);
        int end = sb.indexOf("</script>");

        int myLength = sb.length();
        sb.delete(end, myLength);

        return sb.toString();

    }


    static String loadPage(String myURL) throws Exception {
        URL url = new URL(myURL);
        URLConnection con = url.openConnection();
        Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
        Matcher m = p.matcher(con.getContentType());
        String charset = m.matches() ? m.group(1) : "UTF-8";
        Reader r = new InputStreamReader(con.getInputStream(), charset);
        StringBuilder buf = new StringBuilder();
        while (true) {
            int ch = r.read();
            if (ch == '\n' || ch == '\t') {
                ch = ' ';
            }
            if (ch < 0)
                break;
            buf.append((char) ch);
        }

        return buf.toString();
    }


}