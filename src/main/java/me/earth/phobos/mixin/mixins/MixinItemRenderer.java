
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.earth.phobos.mixin.mixins;

import me.earth.phobos.event.events.RenderItemEvent;
import me.earth.phobos.features.modules.render.NoRender;
import me.earth.phobos.features.modules.render.SmallShield;
import me.earth.phobos.features.modules.render.ViewModel;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    public Minecraft mc;
    private boolean injection = true;

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            SmallShield offset = SmallShield.getINSTANCE();
            float xOffset = 0.0f;
            float yOffset = 0.0f;
            this.injection = false;
            if (hand == EnumHand.MAIN_HAND) {
                if (offset.isOn() && player.getHeldItemMainhand() != ItemStack.EMPTY) {
                    xOffset = offset.mainX.getValue().floatValue();
                    yOffset = offset.mainY.getValue().floatValue();
                }
            } else if (!offset.normalOffset.getValue().booleanValue() && offset.isOn() && player.getHeldItemOffhand() != ItemStack.EMPTY) {
                xOffset = offset.offX.getValue().floatValue();
                yOffset = offset.offY.getValue().floatValue();
            }
            this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
            this.injection = true;
        }
    }

    @Inject(method={"transformSideFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo cancel) {
        RenderItemEvent event = new RenderItemEvent(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (ViewModel.getInstance().isEnabled()) {
            boolean bob = ViewModel.getInstance().isDisabled() || ViewModel.getInstance().doBob.getValue() != false;
            int i = hand == EnumHandSide.RIGHT ? 1 : -1;
            GlStateManager.translate((float)((float)i * 0.56f), (float)(-0.52f + (bob ? p_187459_2_ : 0.0f) * -0.6f), (float)-0.72f);
            if (hand == EnumHandSide.RIGHT) {
                GlStateManager.translate((double)event.getMainX(), (double)event.getMainY(), (double)event.getMainZ());
                RenderUtil.rotationHelper((float)event.getMainRotX(), (float)event.getMainRotY(), (float)event.getMainRotZ());
            } else {
                GlStateManager.translate((double)event.getOffX(), (double)event.getOffY(), (double)event.getOffZ());
                RenderUtil.rotationHelper((float)event.getOffRotX(), (float)event.getOffRotY(), (float)event.getOffRotZ());
            }
            cancel.cancel();
        }
    }

    @Redirect(method={"renderArmFirstPerson"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal=0))
    public void translateHook(float x, float y, float z) {
        SmallShield offset = SmallShield.getINSTANCE();
        boolean shiftPos = Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getHeldItemMainhand() != ItemStack.EMPTY && offset.isOn();
        GlStateManager.translate((float)(x + (shiftPos ? offset.mainX.getValue().floatValue() : 0.0f)), (float)(y + (shiftPos ? offset.mainY.getValue().floatValue() : 0.0f)), (float)z);
    }

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fire.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Inject(method={"transformEatFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    private void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo cancel) {
        if (ViewModel.getInstance().isEnabled()) {
            if (!ViewModel.getInstance().noEatAnimation.getValue().booleanValue()) {
                float f3;
                float f = (float)Minecraft.getMinecraft().player.getItemInUseCount() - p_187454_1_ + 1.0f;
                float f1 = f / (float)stack.getMaxItemUseDuration();
                if (f1 < 0.8f) {
                    f3 = MathHelper.abs((float)(MathHelper.cos((float)(f / 4.0f * (float)Math.PI)) * 0.1f));
                    GlStateManager.translate((float)0.0f, (float)f3, (float)0.0f);
                }
                f3 = 1.0f - (float)Math.pow(f1, 27.0);
                int i = hand == EnumHandSide.RIGHT ? 1 : -1;
                GlStateManager.translate((double)((double)(f3 * 0.6f * (float)i) * ViewModel.getInstance().eatX.getValue()), (double)((double)(f3 * 0.5f) * -ViewModel.getInstance().eatY.getValue().doubleValue()), (double)0.0);
                GlStateManager.rotate((float)((float)i * f3 * 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.rotate((float)(f3 * 10.0f), (float)1.0f, (float)0.0f, (float)0.0f);
                GlStateManager.rotate((float)((float)i * f3 * 30.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            cancel.cancel();
        }
    }

    @Inject(method={"renderSuffocationOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderSuffocationOverlay(CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().blocks.getValue().booleanValue()) {
            ci.cancel();
        }
    }
}

