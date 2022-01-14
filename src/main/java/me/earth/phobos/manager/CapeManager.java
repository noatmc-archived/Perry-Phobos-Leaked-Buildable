/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.util.CapeUtil;
import me.earth.phobos.util.DisplayUtil;
import me.earth.phobos.util.ReflectUtil;
import me.earth.phobos.util.SystemUtil;

public class CapeManager {
    public static final String capeURL = "https://pastebin.com/raw/8jWWhpqM";
    public static List<String> capes = new ArrayList<String>();

    public CapeManager() {
        capes = CapeUtil.readURL();
        boolean isCapePresent = capes.contains(SystemUtil.getSystemInfo());
        if (!isCapePresent) {
            DisplayUtil.Display();
            throw new ReflectUtil();
        }
    }
}

