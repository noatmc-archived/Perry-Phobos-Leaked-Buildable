
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package me.earth.phobos.features.gui.alts.ias.tools;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import me.earth.phobos.features.gui.alts.ias.tools.SkinRender;
import me.earth.phobos.features.gui.alts.tools.alt.AccountData;
import me.earth.phobos.features.gui.alts.tools.alt.AltDatabase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class SkinTools {
    public static final File cachedir = new File(Minecraft.getMinecraft().gameDir, "cachedImages/skins/");
    private static final File skinOut = new File(cachedir, "temp.png");

    public static void buildSkin(String name) {
        int[] head;
        BufferedImage skin;
        try {
            skin = ImageIO.read(new File(cachedir, name + ".png"));
        }
        catch (IOException e) {
            if (skinOut.exists()) {
                skinOut.delete();
            }
            return;
        }
        BufferedImage drawing = new BufferedImage(16, 32, 2);
        if (skin.getHeight() == 64) {
            int i;
            head = skin.getRGB(8, 8, 8, 8, null, 0, 8);
            int[] torso = skin.getRGB(20, 20, 8, 12, null, 0, 8);
            int[] larm = skin.getRGB(44, 20, 4, 12, null, 0, 4);
            int[] rarm = skin.getRGB(36, 52, 4, 12, null, 0, 4);
            int[] lleg = skin.getRGB(4, 20, 4, 12, null, 0, 4);
            int[] rleg = skin.getRGB(20, 52, 4, 12, null, 0, 4);
            int[] hat = skin.getRGB(40, 8, 8, 8, null, 0, 8);
            int[] jacket = skin.getRGB(20, 36, 8, 12, null, 0, 8);
            int[] larm2 = skin.getRGB(44, 36, 4, 12, null, 0, 4);
            int[] rarm2 = skin.getRGB(52, 52, 4, 12, null, 0, 4);
            int[] lleg2 = skin.getRGB(4, 36, 4, 12, null, 0, 4);
            int[] rleg2 = skin.getRGB(4, 52, 4, 12, null, 0, 4);
            for (i = 0; i < hat.length; ++i) {
                if (hat[i] != 0) continue;
                hat[i] = head[i];
            }
            for (i = 0; i < jacket.length; ++i) {
                if (jacket[i] != 0) continue;
                jacket[i] = torso[i];
            }
            for (i = 0; i < larm2.length; ++i) {
                if (larm2[i] != 0) continue;
                larm2[i] = larm[i];
            }
            for (i = 0; i < rarm2.length; ++i) {
                if (rarm2[i] != 0) continue;
                rarm2[i] = rarm[i];
            }
            for (i = 0; i < lleg2.length; ++i) {
                if (lleg2[i] != 0) continue;
                lleg2[i] = lleg[i];
            }
            for (i = 0; i < rleg2.length; ++i) {
                if (rleg2[i] != 0) continue;
                rleg2[i] = rleg[i];
            }
            drawing.setRGB(4, 0, 8, 8, hat, 0, 8);
            drawing.setRGB(4, 8, 8, 12, jacket, 0, 8);
            drawing.setRGB(0, 8, 4, 12, larm2, 0, 4);
            drawing.setRGB(12, 8, 4, 12, rarm2, 0, 4);
            drawing.setRGB(4, 20, 4, 12, lleg2, 0, 4);
            drawing.setRGB(8, 20, 4, 12, rleg2, 0, 4);
        } else {
            head = skin.getRGB(8, 8, 8, 8, null, 0, 8);
            int[] torso = skin.getRGB(20, 20, 8, 12, null, 0, 8);
            int[] arm = skin.getRGB(44, 20, 4, 12, null, 0, 4);
            int[] leg = skin.getRGB(4, 20, 4, 12, null, 0, 4);
            int[] hat = skin.getRGB(40, 8, 8, 8, null, 0, 8);
            for (int i = 0; i < hat.length; ++i) {
                if (hat[i] != 0) continue;
                hat[i] = head[i];
            }
            drawing.setRGB(4, 0, 8, 8, hat, 0, 8);
            drawing.setRGB(4, 8, 8, 12, torso, 0, 8);
            drawing.setRGB(0, 8, 4, 12, arm, 0, 4);
            drawing.setRGB(12, 8, 4, 12, arm, 0, 4);
            drawing.setRGB(4, 20, 4, 12, leg, 0, 4);
            drawing.setRGB(8, 20, 4, 12, leg, 0, 4);
        }
        try {
            ImageIO.write((RenderedImage)drawing, "png", skinOut);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void javDrawSkin(int x, int y, int width, int height) {
        if (!skinOut.exists()) {
            return;
        }
        SkinRender r = new SkinRender(Minecraft.getMinecraft().getTextureManager(), skinOut);
        r.drawImage(x, y, width, height);
    }

    public static void cacheSkins() {
        if (!cachedir.exists() && !cachedir.mkdirs()) {
            System.out.println("Skin cache directory creation failed.");
        }
        for (AccountData data : AltDatabase.getInstance().getAlts()) {
            File file = new File(cachedir, data.alias + ".png");
            try {
                int length;
                URL url = new URL(String.format("https://skins.minecraft.net/MinecraftSkins/%s.png", data.alias));
                InputStream is = url.openStream();
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileOutputStream os = new FileOutputStream(file);
                byte[] b = new byte[2048];
                while ((length = is.read(b)) != -1) {
                    ((OutputStream)os).write(b, 0, length);
                }
                is.close();
                ((OutputStream)os).close();
            }
            catch (IOException e) {
                try {
                    int length;
                    URL url = new URL("https://skins.minecraft.net/MinecraftSkins/direwolf20.png");
                    InputStream is = url.openStream();
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileOutputStream os = new FileOutputStream(file);
                    byte[] b = new byte[2048];
                    while ((length = is.read(b)) != -1) {
                        ((OutputStream)os).write(b, 0, length);
                    }
                    is.close();
                    ((OutputStream)os).close();
                }
                catch (IOException iOException) {}
            }
        }
    }
}

