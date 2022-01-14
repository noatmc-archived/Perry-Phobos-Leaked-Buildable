
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module {
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", false));
    public Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    public Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Invisibles", false));
    public Setting<Boolean> drawFromSky = this.register(new Setting<Boolean>("DrawFromSky", false));
    public Setting<Float> width = this.register(new Setting<Float>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    public Setting<Integer> distance = this.register(new Setting<Integer>("Radius", 300, 0, 300));
    public Setting<Boolean> crystalCheck = this.register(new Setting<Boolean>("CrystalCheck", false));

    public Tracers() {
        super("Tracers", "Draws lines to other players.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Tracers.fullNullCheck()) {
            return;
        }
        GlStateManager.pushMatrix();
        Tracers.mc.world.loadedEntityList.stream().filter(EntityUtil::isLiving).filter(entity -> entity instanceof EntityPlayer ? this.players.getValue().booleanValue() && Tracers.mc.player != entity : (EntityUtil.isPassive(entity) ? this.animals.getValue().booleanValue() : this.mobs.getValue().booleanValue())).filter(entity -> Tracers.mc.player.getDistanceSq(entity) < MathUtil.square(this.distance.getValue().intValue())).filter(entity -> this.invisibles.getValue() != false || !entity.isInvisible()).forEach(entity -> {
            float[] colour = this.getColorByDistance((Entity)entity);
            this.drawLineToEntity((Entity)entity, colour[0], colour[1], colour[2], colour[3]);
        });
        GlStateManager.popMatrix();
    }

    public double interpolate(double now, double then) {
        return then + (now - then) * (double)mc.getRenderPartialTicks();
    }

    public double[] interpolate(Entity entity) {
        double posX = this.interpolate(entity.posX, entity.lastTickPosX) - Tracers.mc.getRenderManager().renderPosX;
        double posY = this.interpolate(entity.posY, entity.lastTickPosY) - Tracers.mc.getRenderManager().renderPosY;
        double posZ = this.interpolate(entity.posZ, entity.lastTickPosZ) - Tracers.mc.getRenderManager().renderPosZ;
        return new double[]{posX, posY, posZ};
    }

    public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
        double[] xyz = this.interpolate(e);
        this.drawLine(xyz[0], xyz[1], xyz[2], e.height, red, green, blue, opacity);
    }

    public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
        Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(Tracers.mc.player.rotationPitch))).rotateYaw(-((float)Math.toRadians(Tracers.mc.player.rotationYaw)));
        if (!this.drawFromSky.getValue().booleanValue()) {
            this.drawLineFromPosToPos(eyes.x, eyes.y + (double)Tracers.mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
        } else {
            this.drawLineFromPosToPos(posx, 256.0, posz, posx, posy, posz, up, red, green, blue, opacity);
        }
    }

    public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)this.width.getValue().floatValue());
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)opacity);
        GlStateManager.disableLighting();
        GL11.glLoadIdentity();
        Tracers.mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)posx, (double)posy, (double)posz);
        GL11.glVertex3d((double)posx2, (double)posy2, (double)posz2);
        GL11.glVertex3d((double)posx2, (double)posy2, (double)posz2);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glColor3d((double)1.0, (double)1.0, (double)1.0);
        GlStateManager.enableLighting();
    }

    public float[] getColorByDistance(Entity entity) {
        if (entity instanceof EntityPlayer && Phobos.friendManager.isFriend(entity.getName())) {
            return new float[]{0.0f, 0.5f, 1.0f, 1.0f};
        }
        AutoCrystal autoCrystal = Phobos.moduleManager.getModuleByClass(AutoCrystal.class);
        Color col = new Color(Color.HSBtoRGB((float)(Math.max(0.0, Math.min(Tracers.mc.player.getDistanceSq(entity), this.crystalCheck.getValue() != false ? (double)(autoCrystal.placeRange.getValue().floatValue() * autoCrystal.placeRange.getValue().floatValue()) : 2500.0) / (double)(this.crystalCheck.getValue() != false ? autoCrystal.placeRange.getValue().floatValue() * autoCrystal.placeRange.getValue().floatValue() : 2500.0f)) / 3.0), 1.0f, 0.8f) | 0xFF000000);
        return new float[]{(float)col.getRed() / 255.0f, (float)col.getGreen() / 255.0f, (float)col.getBlue() / 255.0f, 1.0f};
    }
}

