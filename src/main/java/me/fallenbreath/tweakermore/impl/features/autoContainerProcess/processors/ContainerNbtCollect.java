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

package me.fallenbreath.tweakermore.impl.features.autoContainerProcess.processors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class ContainerNbtCollect implements IContainerProcessor
{

	private static final HashMap<String,Integer> counter = new HashMap<>();

	@Override
	public TweakerMoreConfigBooleanHotkeyed getConfig()
	{
		return TweakerMoreConfigs.CollectContainerNbtToFile;
	}

	@Override
	public ProcessResult process(ClientPlayerEntity player, ContainerScreen<?> containerScreen, List<Slot> allSlots, List<Slot> playerInvSlots, List<Slot> containerInvSlots)
	{

		String containerName = containerScreen.getTitle().getString();
		JsonArray slotArray = new JsonArray();

		for (Slot slot : containerInvSlots)
		{
			JsonObject slotJson = new JsonObject();
			ItemStack stack = slot.getStack();

			slotJson.addProperty("slotId", slot.id);
			slotJson.addProperty("item", stack.getItem().getTranslationKey());
			slotJson.addProperty("name", stack.hasCustomName());
			slotJson.addProperty("count", stack.getCount());

			CompoundTag tag = stack.getTag();

			if (stack.hasTag() && tag != null) {
				slotJson.addProperty("nbt", tag.toString());
			}
			slotArray.add(slotJson);
		}

		JsonObject containerJson = new JsonObject();
		containerJson.addProperty("containerName", containerName);
		containerJson.add("slots", slotArray);
		counter.putIfAbsent(containerName, 0);
		counter.put(containerName, counter.get(containerName)+1);
		saveJsonToFile(containerJson,containerName);

		return new ProcessResult(true, false);
	}

	private void saveJsonToFile(JsonObject jsonObject, String name) {
		LocalDateTime time = LocalDateTime.now();
		Path outputPath = Paths.get("config/tweakermore/"+ time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm")) +"-"+name+"-"+counter.get(name)+".json");
		try {
			Files.createDirectories(outputPath.getParent());
			try (FileWriter writer = new FileWriter(outputPath.toFile(), StandardCharsets.UTF_8)) {
				writer.write(jsonObject.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
