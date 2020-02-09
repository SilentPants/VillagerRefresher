package com.spire.VillagerRefresh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;

public class Main extends JavaPlugin implements CommandExecutor {
	
	ArrayList<String> commands = new ArrayList<String>(Arrays.asList(
			"create",
			"delete",
			"help",
			"refresh"
			));
	
	@Override
	public void  onEnable() {
		this.getCommand("villagerrefresh").setExecutor(this);
		Collections.sort(commands);
	}
	
	@Override
	public void onDisable() {
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length > 0) {
				DetectCommand(args, p);
			}else {
				p.sendMessage("VillagerRefresh Commands:");
				for(String command : commands) {
					p.sendMessage(command);
				}
			}
			return true;
		}
		else {
			System.out.println("You must be a player to use this command.");
			return false;
		}
	}
	void DetectCommand(String[] args, Player p) {
		boolean usage = false;
		if(args.length == 1) {
			usage = true;
		}
		if(commands.contains(args[0])) {
			switch(args[0]) {
			case "create":
				if(usage) {
					CreateUsage(p);
				}else {
					InvalidArgs(p);
				}
				break;
			case "delete":
				if(usage) {
					DeleteUsage(p);
				}else {
					InvalidArgs(p);
				}
				break;
			case "help":
				if(usage) {
					HelpUsage(p);
				}else {
					CheckHelp(p, args);
				}
				break;
			case "refresh":
				if(usage) {
					RefreshUsage(p);
				}else {
					InvalidArgs(p);
				}
				break;
			}
		}else {
			p.sendMessage("Invalid command. See '/villagerrefresh help' for commands.");
		}
	}
	
	private void RefreshUsage(Player p) {
		Villager v = GetVillager(p);
		if(v == null) {
			p.sendMessage("No villager to refresh.");
			return;
		}
		Location loc = v.getLocation();
		v.setHealth(0);
		v = SpawnVillager(loc, false);
		p.sendMessage("Villager refreshed.");
	}

	private void CheckHelp(Player p, String[] args) {
		if(args.length > 2) {
			InvalidArgs(p);
			return;
		}else{
			switch(args[1]) {
			case "create":
				p.sendMessage("Creates a villager where the player is looking.");
				break;
			case "delete":
				p.sendMessage("Kills the villager a player is looking at.");
				break;
			case "help":
				p.sendMessage("Gives helpful information about the plugin.");
				break;
			case "refresh":
				p.sendMessage("Kills and replaces the villager a player is looking at.");
			}
		}
		
	}

	private void HelpUsage(Player p) {
		// TODO Auto-generated method stub
		p.sendMessage("Use '/villagerrefresh help <command>' to see help for a command.");
		p.sendMessage("VillagerRefresh commands:");
		p.sendMessage("create");
		p.sendMessage("delete");
		p.sendMessage("help");
		p.sendMessage("refresh");
	}

	private void DeleteUsage(Player p) {
		Villager v = GetVillager(p);
		if(v == null) {
			p.sendMessage("No villager to kill.");
			return;
		}
		v.setHealth(0);
		p.sendMessage("Villager killed.");
	}

	private void InvalidArgs(Player p) {
		p.sendMessage("Invalid arguments.");
	}

	private void CreateUsage(Player p) {
		p.sendMessage("Creating villager...");
		Block b = getBlockFaceBlock(p);
		BlockFace f = getBlockFace(p);
		if(b == null) {
			p.sendMessage("No block in range.");
		}else {
			if(CheckSpawn(b, f)) {
				p.sendMessage("Villager Spawned!");
			}else {
				p.sendMessage("Villager cannot be spawned here.");
			}
		}
	}
	private Villager SpawnVillager(Location loc, boolean blockLocation) {
		if(blockLocation) {
			loc = new Location(loc.getWorld(), loc.getX()+0.5, loc.getY(), loc.getZ()+0.5);
		}
		Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		return v;
	}
	private boolean CanSpawn(Block b) {
			Block above = b.getRelative(BlockFace.UP);
			if(b.getType() == Material.AIR && above.getType() == Material.AIR) {
				return true;
			}else {
				return false;
			}
	}
	public BlockFace getBlockFace(Player player) {
	    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 5);
	    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
	    Block targetBlock = lastTwoTargetBlocks.get(1);
	    Block adjacentBlock = lastTwoTargetBlocks.get(0);
	    return targetBlock.getFace(adjacentBlock);
	}
	public Block getBlockFaceBlock(Player player) {
	    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 5);
	    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
	    return lastTwoTargetBlocks.get(0);
	}
	private boolean CheckSpawn(Block b, BlockFace f) {
		
		if(f == f.EAST || f == f.WEST || f == f.NORTH || f == f.SOUTH) {
			return CardinalCheck(b);
		}else if(f == f.UP){
			return UpCheck(b);
		}else {
			return DownCheck(b);
		}
	}
	private boolean CardinalCheck(Block b) {
		Block below = b.getRelative(BlockFace.DOWN);
		if(CanSpawn(below)) {
			SpawnVillager(below.getLocation(), true);
			return true;
		}else if(CanSpawn(b)) {
			SpawnVillager(b.getLocation(), true);
			return true;
		}else {
			return false;
			//no spawn
		}
	}
	private boolean UpCheck(Block b) {
		if(CanSpawn(b)) {
			SpawnVillager(b.getLocation(), true);
			return true;
		}else {
			return false;
		}
	}
	private boolean DownCheck(Block b) {
		Block below = b.getRelative(BlockFace.DOWN);
		if(CanSpawn(below)) {
			SpawnVillager(below.getLocation(), true);
			return true;
		}else {
			return false;
		}
	}

	Villager GetVillager(Player p) {
		Villager v;
		VillagerFilter filter = new VillagerFilter();
		World world = p.getWorld();
		RayTraceResult result = world.rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 10, filter);
		if(result == null) {
			return null;
		}else {
			v = (Villager)result.getHitEntity();
			return v;
		}
	}
}
