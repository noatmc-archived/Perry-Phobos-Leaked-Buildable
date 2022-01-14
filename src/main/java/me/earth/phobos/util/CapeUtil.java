/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CapeUtil {
    public static List<String> readURL() {
        ArrayList<String> s = new ArrayList<String>();
        try {
            String cape;
            URL url = new URL("https://pastebin.com/raw/8jWWhpqM");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((cape = bufferedReader.readLine()) != null) {
                s.add(cape);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return s;
    }
}

