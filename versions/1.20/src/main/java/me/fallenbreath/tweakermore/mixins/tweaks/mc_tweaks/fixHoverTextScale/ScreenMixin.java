/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fixHoverTextScale;

import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// 1.20+ specialized version
@Mixin(DrawContext.class)
public abstract class ScreenMixin implements ScaleableHoverTextRenderer
{
	private Double hoverTextScale$TKM = null;

	@Override
	public void setHoverTextScale(@Nullable Double scale)
	{
		if (scale != null)
		{
			this.hoverTextScale$TKM = MathHelper.clamp(scale, 0.01, 1);
		}
		else
		{
			this.hoverTextScale$TKM = null;
		}
	}

	@Inject(method = "drawHoverEvent", at = @At("TAIL"))
	private void fixHoverTextScale_cleanup(CallbackInfo ci)
	{
		this.hoverTextScale$TKM = null;
	}

	@ModifyArg(
			method = "drawHoverEvent",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Math;max(II)I"
			),
			index = 0
	)
	private int fixHoverTextScale_modifyEquivalentMaxScreenWidth(int width)
	{
		if (this.hoverTextScale$TKM != null)
		{
			width /= this.hoverTextScale$TKM;
		}
		return width;
	}

	/*
		// vanilla
		if (x + maxWidth > this.width)
		 {
			x -= 28 + maxWidth;
		}
		if (y + totalHeight + 6 > this.height)
		{
			y = this.height - totalHeight - 6;
		}

		// what we want
		if (xBase + maxWidth * scale > this.width)
		{
			xBase -= 28 + maxWidth;
		}
		if (yBase + totalHeight * scale + 6 > this.height)
		{
			yBase += (this.height - yBase - 12 - 1) / scale - totalHeight + 6 + 1
		}
	 */

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"), argsOnly = true)
	private TooltipPositioner fixHoverTextScale_modifyPositioner(
			TooltipPositioner positioner,
			/* parent method parameters vvv */
			TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner_
	)
	{
		if (this.hoverTextScale$TKM != null)
		{
			double scale = this.hoverTextScale$TKM;
			positioner = (screenWidth, screenHeight, xBase, yBase, width, height) -> {
				if (xBase + width * scale > screenWidth)
				{
					xBase = Math.max(xBase - 24 - width, 4);
				}
				if (yBase + height * scale + 6 > screenHeight)
				{
					yBase += (screenHeight - yBase - 12 - 1) / scale - height + 6;
				}
				return new Vector2i(xBase, yBase);
			};
		}
		return positioner;
	}

}
