/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketSetSlot
 */
package me.earth.phobos.mixin.mixins.accessors;

import net.minecraft.network.play.server.SPacketSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SPacketSetSlot.class})
public interface ISPacketSetSlot {
    @Accessor(value="windowId")
    public int getId();

    @Accessor(value="windowId")
    public void setWindowId(int var1);
}

