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
				if(str.contains("���ݷ�")) itemData.itemDmgPlus = (Main.onlyDoubleNum(str));
				if(str.contains("����")) itemData.itemDefense = (Main.onlyDoubleNum(str));
				if(str.contains("��������")) itemData.itemLvLimit = (Main.onlyDoubleNum(str));
				if(str.contains("�߰�����")) itemData.itemAtkPlus = (Main.onlyDoubleNum(str));
				if(str.contains("��������")) itemData.itemAtkLimit = (Main.onlyDoubleNum(str));
				if(str.contains("�߰�����")) itemData.itemBalPlus = (Main.onlyDoubleNum(str));
				if(str.contains("��������")) itemData.itemBalLimit = (Main.onlyDoubleNum(str));
				if(str.contains("�߰���ø")) itemData.itemDexPlus = (Main.onlyDoubleNum(str));
				if(str.contains("��ø����")) itemData.itemDexLimit = (Main.onlyDoubleNum(str));
				if(str.contains("�߰����")) itemData.itemDefPlus = (Main.onlyDoubleNum(str));
				if(str.contains("�������")) itemData.itemDefLimit = (Main.onlyDoubleNum(str));
				if(str.contains("��������")) itemData.itemDrain = (Main.onlyDoubleNum(str));
				if(str.contains("�߰�ü��")) itemData.itemHp = (Main.onlyDoubleNum(str));
				if(str.contains("�߰��ӵ�")) itemData.itemSpd = (Main.onlyDoubleNum(str));
				if(str.contains("ũ��Ƽ��Ȯ��")) itemData.itemCri = (Main.onlyDoubleNum(str));
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
			if(str.contains("��������")) {
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
