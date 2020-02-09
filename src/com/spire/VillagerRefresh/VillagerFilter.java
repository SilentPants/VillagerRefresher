package com.spire.VillagerRefresh;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class VillagerFilter implements Predicate<Entity>{

	@Override
	public boolean test(Entity e) {
		if(e.getType() == EntityType.VILLAGER) {
			return true;
		}else {
			return false;
		}
	}
}
