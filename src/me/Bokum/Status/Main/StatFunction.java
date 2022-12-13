package me.Bokum.Status.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatFunction {
	
	public static boolean hasStat(List<StatData> statList, String pName){
		StatData statData = StatFunction.getPlayerStatData(statList, pName);
		if(statData == null) return false;
		else return true;
	}
	
	public static void applyAbility(List<StatData> statList, Player player){
		final Player p = player;
		final StatData statData = getPlayerStatData(statList, p.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable()
	    {
	      public void run()
	      {
	    	  Bukkit.dispatchCommand(p.getServer().getConsoleSender(), "effect "+p.getName()+" 21 1 1");
	    	  statData.updateInv();
	    	  statData.updateStat();
	      }
	    }
	    , 20L);
	}
	
	public static StatData getPlayerStatData(List<StatData> statList, String pName){
		for(StatData statData : statList)
			if(statData.name.equalsIgnoreCase(pName)) return statData;
		return null;
	}
	
	public static boolean giveLeftStat(List<StatData> statList, String pName, double d){
		StatData statData = getPlayerStatData(statList, pName);
		if(statData != null){
			statData.leftStat += d;
			return true;
		}
		return false;
	}
	
	public static boolean clickStatInv(List<StatData> statList, Player p, int slotNum){
		StatData statData = getPlayerStatData(statList, p.getName());
		if(statData == null) return false;
		if(statData.leftStat < 1) {
			p.sendMessage(Main.title+"남은 스탯포인트가 없습니다..");
			return false;
		}
		if(slotNum == 1){
			statData.bal++; 
			p.sendMessage(Main.title+"모든 스탯을 조금씩 강화하였습니다.");
		} else if(slotNum == 3){
			statData.atk++;
			p.sendMessage(Main.title+"공격력을 집중적으로 강화하였습니다.");
		} else if(slotNum == 5){
			statData.dex++;
			p.sendMessage(Main.title+"민첩을 집중적으로 강화하였습니다.");
		} else if(slotNum == 7){
			statData.def++;
			p.sendMessage(Main.title+"방어력을 집중적으로 강화하였습니다.");
		}
		statData.leftStat -= 1;
		
		statData.updateStat();
		statData.updateInv();
		return true;
	}
	
	public static void saveStatData(StatData statData){
		File file = new File(Main.instance.getDataFolder().getPath()+"/statData", statData.name+".stat");
		try {
			FileConfiguration statFile = YamlConfiguration.loadConfiguration(file);
			statFile.set("nickname", statData.name);
			statFile.set("bal", statData.bal);
			statFile.set("atk", statData.atk);
			statFile.set("def", statData.def);
			statFile.set("dex", statData.dex);
			statFile.set("lv", statData.lv);
			statFile.set("exp", statData.exp);
			statFile.set("hp", statData.hp);
			statFile.set("leftStat", statData.leftStat);
			statFile.save(file);
		} catch(Exception e){
			return;
		}	
	}
	
	public static boolean appendStatToInv(StatData statData){
		if(statData == null) return false;
		
		try{
		Inventory statinv = statData.statInv;
		Player p = Bukkit.getPlayer(statData.name);
		if(p == null) return false;
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.bal+"§f(§e+"+statData.bonusBal+")§f"+" §c균형");
		List<String> lorelist = new ArrayList<String>();
		lorelist.add("§7데미지 + "+Main.bal_damage);
		lorelist.add("§7체력 + "+Main.bal_hp);
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(1, item);
		
		item = new ItemStack(Material.IRON_SWORD, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.atk+"§f(§e+"+statData.bonusAtk+")§f"+" §c공격");
		lorelist = new ArrayList<String>();
		lorelist.add("§7데미지 + "+Main.atk_damage);
		lorelist.add("§7체력 + "+Main.atk_hp);
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(3, item);
		
		item = new ItemStack(Material.LEATHER_BOOTS, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.dex+"§f(§e+"+statData.bonusDex+")§f"+" §c민첩");
		lorelist = new ArrayList<String>();
		lorelist.add("§7데미지 + "+Main.dex_damage);
		lorelist.add("§7속도 + "+Main.dex_speed);
		lorelist.add("§7체력 + "+Main.dex_hp);
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(5, item);
		
		item = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.def+"§f(§e+"+statData.bonusDef+")§f"+" §c방어");
		lorelist = new ArrayList<String>();
		lorelist.add("§7데미지 + "+Main.def_damage);
		lorelist.add("§7체력 + "+Main.def_hp);
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(7, item);
		
		item = new ItemStack(Material.EXP_BOTTLE, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.lv+" §c레벨");
		//lorelist = new ArrayList<String>();
		//lorelist.add("§7"+statData.bal);
		//meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(9, item);	
		
		item = new ItemStack(Material.PAPER, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§7"+statData.leftStat+" §c사용가능한 스탯");
		//lorelist = new ArrayList<String>();
		//lorelist.add("§7"+statData.bal);
		//meta.setLore(lorelist);
		item.setItemMeta(meta);
		statinv.setItem(17, item); 
		
			item = new ItemStack(Material.BOOK_AND_QUILL, 1);
			meta = item.getItemMeta();
			meta.setDisplayName("§c스탯");
			lorelist = new ArrayList<String>();
			lorelist.add("§7데미지: §b"+statData.damage+"§f(§e+"+statData.bonusDmg+")§f");
			lorelist.add("§7속도: §b"+(statData.speed*5)+"§f(§e+"+(statData.bonusSpd*5)+")§f");
			lorelist.add("§7생명력흡수: §b"+statData.bonusDrain);
			lorelist.add("§7방어력: §b"+statData.bonusDefense);
			lorelist.add("§7크리티컬 확률: §b"+statData.bonusCri);
			lorelist.add("§7체력: §b"+p.getHealth()+" §f/ §b"+p.getMaxHealth()+"§f(§e+"+(statData.bonusHp)+"§f)");
			meta.setLore(lorelist);
			item.setItemMeta(meta);
			statinv.setItem(10, item);


		
		saveStatData(statData);
		}catch(Exception e){
			return false;
		}
		
		
		return true;
	}
}
