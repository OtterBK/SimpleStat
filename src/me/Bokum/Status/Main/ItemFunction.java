package me.Bokum.Status.Main;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ItemFunction {
	public static ItemData getAllItemData(ItemStack item){
		try{
			if ((item == null) || (!item.hasItemMeta()) || (item.getType() == Material.AIR)) return null;
			ItemData itemData = new ItemData();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			for(String str : lore){
				if(str.contains("공격력")) itemData.itemDmgPlus = (Main.onlyDoubleNum(str));
				if(str.contains("방어력")) itemData.itemDefense = (Main.onlyDoubleNum(str));
				if(str.contains("레벨제한")) itemData.itemLvLimit = (Main.onlyDoubleNum(str));
				if(str.contains("추가공격")) itemData.itemAtkPlus = (Main.onlyDoubleNum(str));
				if(str.contains("공격제한")) itemData.itemAtkLimit = (Main.onlyDoubleNum(str));
				if(str.contains("추가균형")) itemData.itemBalPlus = (Main.onlyDoubleNum(str));
				if(str.contains("균형제한")) itemData.itemBalLimit = (Main.onlyDoubleNum(str));
				if(str.contains("추가민첩")) itemData.itemDexPlus = (Main.onlyDoubleNum(str));
				if(str.contains("민첩제한")) itemData.itemDexLimit = (Main.onlyDoubleNum(str));
				if(str.contains("추가방어")) itemData.itemDefPlus = (Main.onlyDoubleNum(str));
				if(str.contains("방어제한")) itemData.itemDefLimit = (Main.onlyDoubleNum(str));
				if(str.contains("생명력흡수")) itemData.itemDrain = (Main.onlyDoubleNum(str));
				if(str.contains("추가체력")) itemData.itemHp = (Main.onlyDoubleNum(str));
				if(str.contains("추가속도")) itemData.itemSpd = (Main.onlyDoubleNum(str));
				if(str.contains("크리티컬확률")) itemData.itemCri = (Main.onlyDoubleNum(str));
			}
			return itemData;
			}catch(Exception e){
			return null;
		}
	}
	
	public static int getItemLvLimit(ItemStack item){
		if ((item == null) || (!item.hasItemMeta()) || (item.getType() == Material.AIR)) return 0;
		int limitLv = 0;
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		for(String str : lore)
			if(str.contains("레벨제한")) {
				limitLv = (Main.onlyNum(str));
			}
		return limitLv;
	}
	
	public static boolean isMetaItem(ItemStack item){
		if ((item == null) || (!item.hasItemMeta()) || (item.getType() == Material.AIR)) return false;
		else return true;
	}
	
	public static boolean isStatCanUse(StatData statData, ItemData itemData){
		if(itemData.itemLvLimit > statData.lv || itemData.itemBalLimit > statData.bal || itemData.itemAtkLimit > statData.atk
		|| itemData.itemDexLimit > statData.dex || itemData.itemDefLimit > statData.def) return false;
		else return true;
	}
}
