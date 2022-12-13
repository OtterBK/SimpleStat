package me.Bokum.Status.Main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatData {
	public String name;
	public double bal = 0;
	public double atk = 0;
	public double def = 0;
	public double dex = 0;
	public int lv = 0;
	public double exp = 0;
	public double hp = 0;
	public double leftStat = 0;
	
	public Inventory statInv;
	public double damage = 1;
	public double defense = 0;
	public float speed = 0.2f;
	public double drain = 0;
	public double cri = 0;
	
	public double bonusAtk = 0;
	public double bonusDex = 0;
	public double bonusBal = 0;
	public double bonusDef = 0;
	public double bonusDmg = 0;
	public float bonusSpd = 0;
	public double bonusHp = 0;
	public double bonusDrain = 0;
	public double bonusDefense = 0;
	public double bonusCri = 0;
	
	public StatData(String name, String bal, String atk, String def, String dex, String lv, String exp, String hp, String leftStat){
		this.name = name;
		try{
			this.bal = Double.valueOf(bal);
			this.atk = Double.valueOf(atk);
			this.def = Double.valueOf(def);
			this.dex = Double.valueOf(dex);
			this.lv = Integer.valueOf(lv);
			this.exp = Double.valueOf(exp);
			this.hp = Double.valueOf(hp);
			this.leftStat = Double.valueOf(leftStat);
			this.statInv = Bukkit.createInventory(null, 18, "§f[§c스테이터스§f]");
			damage = (double) (1+(this.bal * Main.bal_damage) + (this.atk * Main.atk_damage) + (this.dex * Main.dex_damage)+(this.def * Main.def_damage));
			defense = (double) ((this.bal * Main.bal_defense) + (this.atk * Main.atk_defense) + (this.dex * Main.dex_defense)+(this.def * Main.def_defense));
			speed = (float)(0.2f+(this.bal * Main.bal_speed) + (this.atk * Main.atk_speed) + (this.dex * Main.dex_speed)+(this.def * Main.def_speed));
		}catch(Exception e){
			Bukkit.getConsoleSender().sendMessage(Main.title+name+" 플러이어의 스테이터스 정보를 읽는데 실패하였습니다.");
			return;
		}
	}
	
	public void updateInv(){
		if(Bukkit.getPlayer(name).isOnline()) StatFunction.appendStatToInv(this);
	}
	
	public void updateStat(){
		this.hp = Main.baseHp+(double) ((this.bal * Main.bal_hp) + (this.atk * Main.atk_hp) + 
				(this.dex * Main.dex_hp)+(this.def * Main.def_hp)+ ((this.lv) * Main.hpPerLv));
		this.damage = (double) (1+(this.bal * Main.bal_damage) + (this.atk * Main.atk_damage) + (this.dex * Main.dex_damage)+(this.def * Main.def_damage));
		this.defense = (double) ((this.bal * Main.bal_defense) + (this.atk * Main.atk_defense) + (this.dex * Main.dex_defense)+(this.def * Main.def_defense));
		this.speed = (float)(0.2f+(this.bal * Main.bal_speed) + (this.atk * Main.atk_speed) + (this.dex * Main.dex_speed)+(this.def * Main.def_speed));
		
		if(this.damage < 0) this.damage = 0; //데미지는 음수가 될 수 없음
		if(this.hp < 1) this.hp = 1; //체력은 1미만이 될 수 없음
		if(this.speed < 0) this.speed = 0; //속도는 음수가 될 수 없음
		Player p = Bukkit.getPlayer(name);
		if(p == null) return;
		if(p.isOnline()){ //플레이어가 접속중이면 
			p.setMaxHealth(this.hp+this.bonusHp);
			try{
				p.setWalkSpeed((float) (this.speed+this.bonusSpd));
			} catch(Exception e){
				Main.instance.getLogger().info(p.getName()+"님의 속도 설정에 실패하였습니다. 시도된 속도값: "+this.speed);
			}
		}	
	}
	
	public void applyItems(int slotNum){
		Player p = Bukkit.getPlayer(name);
		if(p == null) return;
		ItemStack nowitem;
		if(slotNum == -1) nowitem = p.getItemInHand();
		else nowitem = p.getInventory().getItem(slotNum);
		bonusAtk = 0;
		bonusDex = 0;
		bonusBal = 0;
		bonusDef = 0;
		bonusDmg = 0;
		bonusSpd = 0;
		bonusHp = 0;
		bonusDrain = 0;
		bonusDefense = 0;
		bonusCri = 0;
		ItemData itemData = ItemFunction.getAllItemData(p.getInventory().getHelmet());
		if(itemData != null) applyItemData(itemData);
		itemData = ItemFunction.getAllItemData(p.getInventory().getLeggings());
		if(itemData != null) applyItemData(itemData);
		itemData = ItemFunction.getAllItemData(p.getInventory().getChestplate());
		if(itemData != null) applyItemData(itemData);
		itemData = ItemFunction.getAllItemData(p.getInventory().getBoots());
		if(itemData != null) applyItemData(itemData);
		itemData = ItemFunction.getAllItemData(nowitem);
		if(itemData != null) applyItemData(itemData);
		
		this.bonusHp += (this.bonusBal * Main.bal_hp) + (this.bonusAtk * Main.atk_hp) + 
				(this.bonusDex * Main.dex_hp)+(this.bonusDef * Main.def_hp);
		this.bonusDmg += (double) ((this.bonusBal * Main.bal_damage) + (this.bonusAtk * Main.atk_damage)
				+ (this.bonusDex * Main.dex_damage)+(this.bonusDef * Main.def_damage));
		this.bonusDefense += (double) ((this.bonusBal * Main.bal_defense) + (this.bonusAtk * Main.atk_defense)
				+ (this.bonusDex * Main.dex_defense)+(this.bonusDef * Main.def_defense));
		this.bonusSpd += (float)((this.bonusBal * Main.bal_speed) + (this.bonusAtk * Main.atk_speed) 
				+ (this.bonusDex * Main.dex_speed)+(this.bonusDef * Main.def_speed));	
		
		updateStat();
		updateInv();
	}
	
	public void applyItemData(ItemData itemData){
		if(itemData.itemLvLimit > this.lv) return;
		if(itemData.itemAtkLimit > this.atk) return;
		if(itemData.itemBalLimit > this.bal) return;
		if(itemData.itemDexLimit > this.dex) return;
		if(itemData.itemDefLimit > this.def) return;
		bonusBal += itemData.itemBalPlus;
		bonusAtk += itemData.itemAtkPlus;
		bonusDex += itemData.itemDexPlus;
		bonusDef += itemData.itemDefPlus;
		bonusDmg += itemData.itemDmgPlus;
		bonusHp += itemData.itemHp;
		bonusDrain += itemData.itemDrain;
		bonusDefense += itemData.itemDefense;
		bonusSpd += itemData.itemSpd;
		bonusCri += itemData.itemCri;
	}
}
