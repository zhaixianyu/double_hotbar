package com.sidezbros.double_hotbar.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sidezbros.double_hotbar.DHModConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;


import static net.minecraft.client.gui.widget.ClickableWidget.WIDGETS_TEXTURE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	private boolean onScreen = false;
	@Shadow private int scaledHeight;
	@Shadow private int scaledWidth;
	@Shadow protected abstract PlayerEntity getCameraPlayer();
	@Shadow protected abstract void renderHotbarItem(DrawContext matrixStack, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0))
	private void renderHotbarFrame(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod) {
//			drawTexture(matrices, this.scaledWidth / 2 - 91, this.scaledHeight - 22 - DHModConfig.INSTANCE.shift, 0, 0, 182, 22-DHModConfig.INSTANCE.renderCrop);
			context.drawTexture(WIDGETS_TEXTURE, this.scaledWidth / 2 - 91, this.scaledHeight - 22 - DHModConfig.INSTANCE.shift, 0, 0, 182, 22-DHModConfig.INSTANCE.renderCrop);
			this.onScreen = true;
		}

	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1))
	private void shiftHotbarSelector(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod) {
			context.getMatrices().translate(0, -DHModConfig.INSTANCE.shift, 0);
		}

	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0))
	private void returnHotbarSelector(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod) {
			context.getMatrices().translate(0, DHModConfig.INSTANCE.shift, 0);
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
	private void shiftHotbarItems(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && DHModConfig.INSTANCE.reverseBars && !DHModConfig.INSTANCE.disableMod) {
			this.scaledHeight -= DHModConfig.INSTANCE.shift;
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1))
	private void renderHotbarItems(float tickDelta, DrawContext context, CallbackInfo ci) {
		if(DHModConfig.INSTANCE.displayDoubleHotbar && !DHModConfig.INSTANCE.disableMod) {
			if(DHModConfig.INSTANCE.reverseBars) {
				this.scaledHeight += DHModConfig.INSTANCE.shift;
			}
			int m = 1;
			for (int n2 = 0; n2 < 9; ++n2) {
				int o = this.scaledWidth / 2 - 90 + n2 * 20 + 2;
				int p = this.scaledHeight - 16 - 3 - (DHModConfig.INSTANCE.reverseBars ? 0 : DHModConfig.INSTANCE.shift);
				this.renderHotbarItem(context, o, p, tickDelta, getCameraPlayer(), getCameraPlayer().getInventory().main.get(n2+DHModConfig.INSTANCE.inventoryRow*9), m++);
			}
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z"))
	public void shiftStatusBars(DrawContext context, float tickDelta, CallbackInfo ci) {
		if(this.onScreen) {
			context.getMatrices().translate(0, -DHModConfig.INSTANCE.shift, 0);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getSleepTimer()I", ordinal = 0))
	public void returnStatusBars(DrawContext context, float tickDelta, CallbackInfo ci) {
		if(this.onScreen) {
			context.getMatrices().translate(0, DHModConfig.INSTANCE.shift, 0);
		}
		this.onScreen = false;
	}

}
