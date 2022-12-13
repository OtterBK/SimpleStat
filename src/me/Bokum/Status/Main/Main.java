package me.Bokum.Status.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener{
	public List<StatData> statList = new ArrayList(100);
	public List<Integer> armour = new ArrayList(20);
	public static String title = "��f[��e���ȡ�f] ";
	public Inventory statInv;
	public int leftStatPerLv = 4;
	public static Main instance;
	public static double bal_damage = 2;
	public static double bal_hp = 8;
	public static double bal_speed = 0;
	public static double bal_defense = 0;
	public static double atk_damage = 3;
	public static double atk_hp = 5;
	public static double atk_speed = 0;
	public static double atk_defense = 0;
	public static double dex_damage = 1.5;
	public static double dex_hp = 3;
	public static double dex_speed = 0.003;
	public static double dex_defense = 0;
	public static double def_damage = 0.5;
	public static double def_hp = 10;
	public static double def_speed = 0;
	public static double def_defense = 0;
	public static double criDamage = 2.0;
	public static double hpPerLv = 80;
	public static double baseHp = 50;
	public static int autoRespawn = 0;
	//public Location spawnLoc = new Location(Bukkit.getWorld("world"), 0, 10, 0);
	
	public void onEnable(){ //�������ͽ� �÷����� �ε� ������

		//HolographicDisplays API dependency ����
//		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
//			getLogger().severe("Ȧ�α׷��� �÷������� ��� ������");
//			this.setEnabled(false);
//			return;
//		}
		
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[����] �������ͽ� �÷������� �ε� �Ǿ����ϴ�. 0.55ver");
		instance = this; 

		for(int i = 298; i <= 317; i++) armour.add(i);
		loadConfig(false); //������ �ε�
		loadDatas(); //���� ������ �ε�
		
//		showLvTimer();
	}

	//HolographicDisplays API dependency ����
//	public void showLvTimer(){
//		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//		    @Override
//			public void run(){
//				for(Player p : Bukkit.getOnlinePlayers()){
//					StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
//					if(statData != null){
//						Location l = p.getLocation();
//						l.add(0, 2.5, 0);
//						final Hologram hg = HologramsAPI.createHologram(instance, l);
//						hg.appendTextLine(ChatColor.RED+"LV: "+statData.lv);
//						Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
//							public void run() {
//								hg.delete();
//							}
//						}, 10l);
//					}
//				}
//		    }
//		}, 10, 10);
//	}
	
	public void onDisable(){
		saveDatas();
		getLogger().info("[����] �������ͽ� �÷������� ��ε� �Ǿ����ϴ�.");
	}
	
	public boolean onCommand(CommandSender talker, Command command, String string, String[] args)
	{
	  if ((talker instanceof Player)){
		  Player p = (Player) talker;
		  if(string.equalsIgnoreCase("����")){
			  StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			  if(args.length <= 0){
				  if(statData != null){
					  statData.updateInv();
					  p.openInventory(statData.statInv);
				  } else p.sendMessage(title+"����� ���ȵ����Ͱ� �����ϴ�.");
			  } else if(args.length >= 1 && p.isOp()){
				  if(args[0].equalsIgnoreCase("���ε�")){
					  loadConfig(false);
					  loadDatas();
					  p.sendMessage(title+"���ȵ����͸� ���ε� �߽��ϴ�.");
				  } else if(args[0].equalsIgnoreCase("�ӵ�")) p.setWalkSpeed(Float.valueOf(args[1])); //����� ��ɾ�
				  else if(args[0].equalsIgnoreCase("�ֱ�"))giveStat(p, args);
				  else if(args[0].equalsIgnoreCase("��þƮ")){
					  enchant(p, args);
				  } else if(args[0].equalsIgnoreCase("������")){
					    getConfig().set("��������", p.getLocation().getWorld().getName());
					    getConfig().set("����x", Integer.valueOf(p.getLocation().getBlockX()));
					    getConfig().set("����y", Integer.valueOf(p.getLocation().getBlockY() + 1));
					    getConfig().set("����z", Integer.valueOf(p.getLocation().getBlockZ()));
					    saveConfig();
					    //spawnLoc = new Location(Bukkit.getWorld(getConfig().getString("��������")), getConfig().getInt("����x"), getConfig().getInt("����y"), getConfig().getInt("����z"));
					    p.sendMessage(title+"���� ������ �Ϸ�Ǿ����ϴ�.");
				  } else if(args[0].equalsIgnoreCase("test")){
					  p.setMaxHealth(p.getMaxHealth()+1);
					  p.setMaxHealth(p.getMaxHealth()-1);
				  }
			  } 
			  return true;
		  }
	  }
	  return false;
	}
	
	public void loadConfig(boolean retry){ 
		try{ //������ �ε� �õ�
			baseHp = instance.getConfig().getDouble("baseHp");
			hpPerLv = instance.getConfig().getDouble("hpPerLv");
			bal_damage = instance.getConfig().getDouble("bal_damage");
			bal_hp = instance.getConfig().getDouble("bal_hp");
			bal_speed = instance.getConfig().getDouble("bal_speed");
			bal_defense = instance.getConfig().getDouble("bal_defense");
			atk_damage = instance.getConfig().getDouble("atk_damage");
			atk_hp = instance.getConfig().getDouble("atk_hp");
			atk_speed = instance.getConfig().getDouble("atk_speed");
			atk_defense = instance.getConfig().getDouble("atk_defnse");
			dex_damage = instance.getConfig().getDouble("dex_damage");
			dex_hp = instance.getConfig().getDouble("dex_hp");
			dex_speed = instance.getConfig().getDouble("dex_speed");
			dex_defense = instance.getConfig().getDouble("dex_defense");
			def_damage = instance.getConfig().getDouble("def_damage");
			def_hp = instance.getConfig().getDouble("def_hp");
			def_speed = instance.getConfig().getDouble("def_speed");
			def_defense = instance.getConfig().getDouble("def_defense");
			leftStatPerLv = instance.getConfig().getInt("leftStatPerLv");
			criDamage = instance.getConfig().getDouble("criDamage");
			autoRespawn = instance.getConfig().getInt("autpRespawn");
			//spawnLoc = new Location(Bukkit.getWorld(getConfig().getString("spawnWorld")), getConfig().getInt("spawnX"), getConfig().getInt("spawnY"), getConfig().getInt("spawnZ"));
			if(baseHp == 0)resetConfig(); //�������� �߸�������(�⺻ü����0�ϋ�) �ʱ�ȭ
			else getLogger().info(title+"�������� ���������� �ε��߽��ϴ�.");
		} catch(Exception e) { //�������� �߸������� �ʱ�ȭ
			if(!retry)
			resetConfig();
		}
	}
	
	public void enchant(Player p, String args[]){
		if(args.length < 3)p.sendMessage(title+
				"/���� ��þƮ <���ݷ�/����/��������/�߰�����/��������/�߰�����/��������/�߰���ø/��ø����/�߰����/�������/��������/�߰�ü��/ũ��Ƽ��Ȯ��> ����");
		else{
			if(args[1].equalsIgnoreCase("���ݷ�") || args[1].equalsIgnoreCase("��������") || 
					args[1].equalsIgnoreCase("�߰�����") || args[1].equalsIgnoreCase("��������") || 
					args[1].equalsIgnoreCase("�߰�����") || args[1].equalsIgnoreCase("��������") || 
					args[1].equalsIgnoreCase("�߰���ø") || args[1].equalsIgnoreCase("��ø����") || 
					args[1].equalsIgnoreCase("�߰����") || args[1].equalsIgnoreCase("�������") || 
					args[1].equalsIgnoreCase("��������") || args[1].equalsIgnoreCase("�߰�ü��") || 
					/*args[1].equalsIgnoreCase("�߰��ӵ�") || */args[1].equalsIgnoreCase("ũ��Ƽ��Ȯ��")
					|| args[1].equalsIgnoreCase("����") ){
				int amt = 0;
				try{
					amt = Integer.valueOf(args[2]);
				} catch(Exception e){
					p.sendMessage(title+"�ùٸ����� �Է����ּ���.");
					return;
				}
				boolean check = false;
				ItemStack item = p.getItemInHand();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList();
				if(item == null || item.getType() == Material.AIR) p.sendMessage(title + "�ش� �������� ��þƮ �Ͻ� �� �����ϴ�.");
				else if(item.hasItemMeta()) {
					meta = item.getItemMeta();
					lore = meta.getLore();
					for(int i = 0; i < lore.size(); i++){
						if(lore.get(i).contains(args[1])) {
							lore.set(i, "��7] ��c"+args[1]+" "+(amt < 0 ? "" : "+")+amt);
							check = true;
							break;
						}
					}
					if(!check) lore.add("��7] ��c"+args[1]+" "+(amt < 0 ? "" : "+")+amt);
					meta.setLore(lore);
					item.setItemMeta(meta);
				} else {
					lore.add("��7] ��c"+args[1]+" "+(amt < 0 ? "-" : "+")+amt);
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			} else {
				p.sendMessage(title+"/���� ��þƮ <���ݷ�/��������/�߰�����/��������/�߰�����/��������/�߰���ø/��ø����/�߰����/�������/��������/�߰�ü��/ũ��Ƽ��Ȯ��> �߿����� ����ּ���.");
			}
		}
	}
	
	public void giveStat(Player p, String args[]){
		if(args.length < 4)p.sendMessage(title+"/���� �ֱ� �г��� <����/����/��ø/���/����Ʈ> ����(���� �Է½� ������ ������ �ֽ��ϴ�.");
		else {
			int amt = 0;
			String target = args[1];
			String type = args[2];
			try{
				amt = Integer.valueOf(args[3]);
			} catch(Exception e){
				p.sendMessage(title+"�ùٸ����� �Է����ּ���.");
				return;
			}
			StatData statData = StatFunction.getPlayerStatData(statList, target);
			if(statData == null) {
				p.sendMessage(title+"�ش� �÷��̾��� �������ͽ� �����Ͱ� �������� �ʽ��ϴ�.");
				return;
			}
			if(type.equalsIgnoreCase("����")){
				statData.bal += amt;
				if(statData.bal < 0) statData.bal = 0; 
			} else if(type.equalsIgnoreCase("����")){
				statData.atk += amt;
				if(statData.atk < 0) statData.atk = 0;
			} else if(type.equalsIgnoreCase("��ø")){
				statData.dex += amt;
				if(statData.dex < 0) statData.dex = 0;
			} else if(type.equalsIgnoreCase("���")){
				statData.def += amt;
				if(statData.def < 0) statData.def = 0;
			} else if(type.equalsIgnoreCase("����Ʈ")){
				statData.leftStat += amt;
				if(statData.leftStat < 0) statData.leftStat = 0;
			} else {
				p.sendMessage(title+"<����/����/��ø/���/����Ʈ> �� �Է��� �����մϴ�.");
				return;
			}
			statData.updateStat();
			statData.updateInv();
			p.sendMessage(title+"��a"+target+"f�Կ��� ��a"+amt+"��f��ŭ�� ��a"+type+"��f������ �����Ͽ����ϴ�.");
		}
	}
	
	public void resetConfig(){
		getLogger().info(title+"������ �ε忡 �����߽��ϴ�. �������� �ʱ�ȭ�մϴ�. \n- �⺻ü���� 0���� �����ϼ̰ų� ������ ������ �߸��Ǿ����ϴ�.");
		bal_damage = 2;
		bal_hp = 8;
		bal_speed = 0;
		bal_defense = 0;
		atk_damage = 3;
		atk_hp = 5;
		atk_speed = 0;
		atk_defense = 0;
		dex_damage = 1.5;
		dex_hp = 3;
		dex_speed = 0.003;
		 dex_defense = 0;
		 def_damage = 0.5;
		 def_hp = 10;
		 def_speed = 0;
		 def_defense = 0;
		hpPerLv = 10;
		baseHp = 50;
		leftStatPerLv = 4;
		criDamage = 2;
		autoRespawn = 0;
		//spawnLoc = new Location(Bukkit.getWorld("world"), 0, 10, 0);
		instance.getConfig().set("baseHp", baseHp);
		instance.getConfig().set("hpPerLv", hpPerLv);
		instance.getConfig().set("bal_damage", bal_damage);
		instance.getConfig().set("bal_hp", bal_hp);
		instance.getConfig().set("bal_speed", bal_speed);
		instance.getConfig().set("bal_defense", bal_defense);
		instance.getConfig().set("atk_damage", atk_damage);
		instance.getConfig().set("atk_hp", atk_hp);
		instance.getConfig().set("atk_speed", atk_speed);
		instance.getConfig().set("atk_defense", atk_defense);
		instance.getConfig().set("dex_damage", dex_damage);
		instance.getConfig().set("dex_hp", dex_hp);
		instance.getConfig().set("dex_speed", dex_speed);
		instance.getConfig().set("dex_defense", dex_defense);
		instance.getConfig().set("def_damage", def_damage);
		instance.getConfig().set("def_hp", def_hp);
		instance.getConfig().set("def_speed", def_speed);
		instance.getConfig().set("def_defense", def_defense);
		instance.getConfig().set("leftStatPerLv", leftStatPerLv);
		instance.getConfig().set("criDamage", criDamage);
		//instance.getConfig().set("spawnWord", spawnLoc.getWorld().getName());
		//instance.getConfig().set("spawnX", spawnLoc.getX());
		//instance.getConfig().set("spawnY", spawnLoc.getY());
		//instance.getConfig().set("spawnZ", spawnLoc.getZ());
		instance.getConfig().set("autoRespawn", autoRespawn);
		instance.saveConfig();
		loadConfig(true);
	}
	
	public void loadDatas(){
		File dataFolder = new File(instance.getDataFolder().getPath()+"/statData");
		if(!dataFolder.exists())return;
		File[] files = dataFolder.listFiles();
		statList.clear();
		for(File file : files){
			if(file == null) continue;
			if(file.exists()){
				if (file.isDirectory() || !file.getAbsolutePath().endsWith(".stat")) return;
					FileConfiguration statFile = YamlConfiguration.loadConfiguration(file);
					StatData tempStat = new StatData(statFile.getString("nickname"),statFile.getString("bal"),statFile.getString("atk")
							,statFile.getString("def"),statFile.getString("dex"),statFile.getString("lv"),statFile.getString("exp")
							,statFile.getString("hp"), statFile.getString("leftStat"));
					statList.add(tempStat);
			}
		}
		
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player p : players){
			StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			if(statData != null) {
				statData.updateInv();
				statData.updateStat();
			}
		}
		getLogger().info(statList.size()+" ���� �������ͽ��� �ε�Ǿ����ϴ�.");
	}

	public void saveDatas(){
		for(StatData statData : statList) StatFunction.saveStatData(statData);
		getLogger().info(statList.size()+" ���� �������ͽ��� ����Ǿ����ϴ�.");
	}
	
	public StatData createStatData(String pName){
		StatData statData = new StatData(pName, "0", "0", "0", "0", "0", "0", ""+baseHp, "0");
		statList.add(statData);
		StatFunction.saveStatData(statData);
		return statData;
	}
	
	public static int onlyNum(String str) {
		if ( str == null ) return 0;
		boolean minus = false;
		int n;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			if( Character.isDigit( c ) ) {
				sb.append( str.charAt(i) );
			} else if(c == '-') minus = true;
			else if(c == '&' || c == '��') i++;
		}
		try{
			n =Integer.valueOf(sb.toString());
		} catch(Exception e){
			instance.getLogger().info("������ ��������������� ���� �߻�");
			return 0;
		}
		if(minus)  n *= -1;
		return n;
	}
	
	public static double onlyDoubleNum(String str) {
		if ( str == null ) return 0;
		boolean minus = false;
		boolean check = false;
		Double n;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			if( Character.isDigit( c ) ) {
				sb.append( str.charAt(i) );
			} else if(c == '-') minus = true;
			else if(c == '&' || c == '��') i++;
			else if(c == '.' && !check) {
				check = true;
				sb.append( str.charAt(i) );
			}
		}
		try{
			n = Double.valueOf(sb.toString());
		} catch(Exception e){
			instance.getLogger().info("������ ��������������� ���� �߻�");
			return 0;
		}
		if(minus)  n *= -1;
		return n;
	}
	
	public static int Getrandom(int min, int max){
		  return (int)(Math.random() * (max-min+1)+min);
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent e){
		  if(!(e.getWhoClicked() instanceof Player)) return; 
		  Player p = (Player) e.getWhoClicked();
		  int slotNum = e.getSlot();
		  if(e.getInventory().getTitle().equalsIgnoreCase("��f[��c�������ͽ���f]")){
			  e.setCancelled(true);
				if(slotNum == 1 || slotNum == 3 || slotNum == 5 || slotNum == 7){
					StatFunction.clickStatInv(statList, p, e.getSlot());
				}
		  } /*else if(armour.contains(e.getCurrentItem().getTypeId())){
			  StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			  if(statData != null) {
					int limitLv = ItemFunction.getItemLvLimit(e.getCurrentItem());
					if(limitLv > statData.lv) {
						e.setCancelled(true);
						p.sendMessage(title+"�ش� ���� ���� �������� ���Ͽ� ����Ͻ� �� �����ϴ�. ��c������ ����:"+(limitLv - statData.lv));
						return;
					}
				 
			  }
		  }*/
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e){
		if(e.getPlayer() instanceof Player){
			Player p = (Player) e.getPlayer();
			StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			if(statData != null) statData.applyItems(-1);
		}
	}
	
	public void LevelUp(Player p){
		StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
		if(statData != null){
			StatFunction.giveLeftStat(statList, p.getName(), 1 * leftStatPerLv);
			statData.lv += 1;
			statData.updateStat();
			statData.updateInv();
			p.sendMessage(title+"�������� �Ͽ� ��c"+1*leftStatPerLv+"��f������ ȹ���Ͽ����ϴ�.");
		}
	}

	@EventHandler
	public void onPlayerLevelup(PlayerLevelChangeEvent evt){
		Player p = evt.getPlayer();
		StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
		if(statData == null) return;

		int newLevel = evt.getNewLevel();
		int nowLevel = statData.lv;

		while(newLevel-- > nowLevel)
		{
			LevelUp(p);
		}
	}

	@EventHandler
	public void onPlayerHeldItem(PlayerItemHeldEvent e){
		Player p = e.getPlayer();
		StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
		if(statData != null) {
			if(p.getInventory().getItem(e.getNewSlot()) == null || p.getInventory().getItem(e.getNewSlot()).getType() == Material.AIR) return; 
			ItemData itemData = ItemFunction.getAllItemData(p.getInventory().getItem(e.getNewSlot()));
			if(itemData == null) return;
			if(itemData.itemLvLimit > statData.lv || itemData.itemBalLimit > statData.bal || itemData.itemAtkLimit > statData.atk
					|| itemData.itemDexLimit > statData.dex || itemData.itemDefLimit > statData.def) {
				e.setCancelled(true);
				p.sendMessage(title+"�ش� ����� ��������� �������� ���Ͽ��� ����Ͻ� �� �����ϴ�.");
				return;
			}
			statData.applyItems(e.getNewSlot());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		StatData checkData = StatFunction.getPlayerStatData(statList, p.getName());
		if(checkData == null){
			checkData = createStatData(p.getName());
//			p.sendMessage(title+"SimpleStatus �÷����� 0.55ver - ����: Bokum");
			p.sendMessage(title+"ȯ���մϴ�. ����� �������ͽ� �����͸� �����Ͽ����ϴ�.");
			p.sendMessage(title+"�⺻��: ��aHP: "+baseHp+", LV: 0, BAL: 0 ATK: 0, DEX: 0, DEF: 0");
			p.setLevel(0);
			p.setExp(0);
			p.setHealth(checkData.hp);
		} 
		final StatData statData = checkData;
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable()
	    {
	      public void run()
	      {
	    	  statData.updateInv();
	    	  statData.updateStat();
	      }
	    }
	    , 20l);
	}
	
	@EventHandler
	public void onEntityDamaged(EntityDamageEvent e){
		Entity hitEntity = e.getEntity();
		if(hitEntity instanceof Player){
			Player p = (Player) hitEntity;
			StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			double damage = e.getDamage();
			
			double chgDamage = damage - statData.defense - statData.bonusDefense;
			if(chgDamage <= 0) chgDamage = 1;
			if(p.getHealth() - chgDamage <= 0){
				/*if(autoRespawn > 0){
					e.setCancelled(true); 
					p.teleport(spawnLoc);
					p.sendMessage(title+"��Ȱ�ϼ̽��ϴ�.");
				}*/
			}
			e.setDamage(chgDamage);
		} 
	}
	
	//�÷��̾ ��ƼƼ Ÿ�ݽ� ������ ����
	@EventHandler
	public void onEntityHitEntity(EntityDamageByEntityEvent e){
		Entity hitEntity = e.getDamager(); //Ÿ���� ���
		Entity damagedEntity = e.getEntity(); //�ǰݵ� ���
		double chgDamage = 0; //ù �������� 0���� ����
		if(hitEntity instanceof Player && damagedEntity instanceof LivingEntity){ //�ǰܰ� ����� ���� �Ǵ� �÷��̾��̰� Ÿ���� ����� �÷��̾��ϰ��
			Player p = (Player) hitEntity; //Ÿ���� ����� �÷��̾�� ĳ�����Ͽ� ����
			LivingEntity le = (LivingEntity) damagedEntity; //�ǰݵ� ��� ĳ�����Ͽ� ����
			if(StatFunction.hasStat(statList, p.getName())){ //Ÿ���� ����� ������ ������������
				StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
				ItemStack item = p.getItemInHand(); //�÷��̾ ����ִ� ����
				if(!ItemFunction.isMetaItem(item)) return; //���� �ش繫�Ⱑ �����۵����͸� �����ٸ�
				ItemData itemData = ItemFunction.getAllItemData(p.getItemInHand()); //������ ������ �ҷ�����
				if(itemData == null) return;
				if(ItemFunction.isStatCanUse(statData, itemData)){
					chgDamage = statData.damage+statData.bonusDmg;
					if(Getrandom(1, (int)(100d/(statData.cri+statData.bonusCri))) == 1){
						p.sendMessage(title+"ũ��Ƽ��!");
						chgDamage *= criDamage;
					}
					double drainAmt = statData.drain+statData.bonusDrain;
					if(drainAmt > 0){
						double pHealth = p.getHealth();
						if(drainAmt > le.getHealth()) drainAmt = le.getHealth();
						pHealth = p.getHealth()+drainAmt;
						if(pHealth > p.getMaxHealth()) pHealth = p.getMaxHealth();
						p.setHealth(pHealth);
					}
					e.setDamage(chgDamage);
				} else {
					e.setCancelled(true);
					p.sendMessage(title+"�ش� ����� ��������� �������� ���Ͽ��� ����Ͻ� �� �����ϴ�.");
				}
			}
		}
	}
	
	//��Ŭ���� ���� ���� ĵ��, ���� ��ȣ�ۿ� ����
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
		if(statData != null) {
			ItemStack item = e.getItem();
			if(ItemFunction.isMetaItem(item)){ //�ش�������� �⺻������ �Ǵ� ���̾ƴϸ�
				ItemData itemData = ItemFunction.getAllItemData(p.getItemInHand()); //�ش� �������� �����͸� ������
				if(itemData == null) return;
				if(!ItemFunction.isStatCanUse(statData, itemData)) {
					e.setCancelled(true);
					p.sendMessage(title+"�ش� ����� ��������� �������� ���Ͽ��� ����Ͻ� �� �����ϴ�.");
				} 
				if(armour.contains(item.getTypeId())){
					e.setCancelled(true);
					p.sendMessage(title+"�������� �κ��丮�� ���ż� �������ּ���.");
					return;
				}
				statData.applyItems(-1);
			}
		}
	}
	
	//�������� ü�� �� �ӵ� ������
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(StatFunction.hasStat(statList, e.getPlayer().getName())){ //���� �������ִ��� �Ǻ�
			StatFunction.applyAbility(statList, e.getPlayer()); //�ӵ�, ü�� ����
		}
	}
	
	//����ġ ��� 0
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		e.setDroppedExp(0);
	}
	
	//����� ����
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			e.setFoodLevel(20);
		}
	}
}


