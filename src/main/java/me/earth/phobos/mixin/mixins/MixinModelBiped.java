/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityPigZombie
 */
package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.render.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelBiped.class})
public class MixinModelBiped {
    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entityIn instanceof EntityPigZombie && NoRender.getInstance().pigmen.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}

