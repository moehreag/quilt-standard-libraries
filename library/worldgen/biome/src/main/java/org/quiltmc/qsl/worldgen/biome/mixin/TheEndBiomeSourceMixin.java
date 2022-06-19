/*
 * Copyright 2016, 2017, 2018, 2019 FabricMC
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.worldgen.biome.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Holder;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import org.quiltmc.qsl.worldgen.biome.impl.TheEndBiomeData;

@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin extends BiomeSource {
	@Unique
	private TheEndBiomeData.Overrides overrides;

	protected TheEndBiomeSourceMixin(Stream<Holder<Biome>> stream) {
		super(stream);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Registry<Biome> biomeRegistry, CallbackInfo ci) {
		this.overrides = TheEndBiomeData.createOverrides(biomeRegistry);
		this.getBiomes().addAll(TheEndBiomeData.getAddedBiomes(biomeRegistry));
	}

	@Inject(method = "method_38109", at = @At("RETURN"), cancellable = true)
	private void getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler noise, CallbackInfoReturnable<Holder<Biome>> cir) {
		cir.setReturnValue(this.overrides.pick(biomeX, biomeY, biomeZ, noise, cir.getReturnValue()));
	}
}