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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fakeNightVision;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12006
//$$ import net.minecraft.registry.entry.RegistryEntry;
//#endif

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Inject(
			method = "hasStatusEffect",
			at = @At("HEAD"),
			cancellable = true
	)
	private void fakeNightVision(
			//#if MC >= 12006
			//$$ RegistryEntry<StatusEffect> effect,
			//#else
			StatusEffect effect,
			//#endif
			CallbackInfoReturnable<Boolean> cir
	)
	{
		if (TweakerMoreConfigs.FAKE_NIGHT_VISION.getBooleanValue())
		{
			LivingEntity self = (LivingEntity)(Object)this;
			MinecraftClient mc = MinecraftClient.getInstance();
			if (effect == StatusEffects.NIGHT_VISION && (self == mc.player || self == mc.gameRenderer.getCamera().getFocusedEntity()))
			{
				cir.setReturnValue(true);
			}
		}
	}
}
