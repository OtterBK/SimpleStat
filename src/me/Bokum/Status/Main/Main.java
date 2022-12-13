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
	public static String title = "§f[§e스탯§f] ";
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
	
	public void onEnable(){ //스테이터스 플러그인 로드 됐을때

		//HolographicDisplays API dependency 제거
//		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
//			getLogger().severe("홀로그래픽 플러그인이 없어서 꺼짐여");
//			this.setEnabled(false);
//			return;
//		}
		
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[보끔] 스테이터스 플러그인이 로드 되었습니다. 0.55ver");
		instance = this; 

		for(int i = 298; i <= 317; i++) armour.add(i);
		loadConfig(false); //설정값 로드
		loadDatas(); //스탯 데이터 로드
		
//		showLvTimer();
	}

	//HolographicDisplays API dependency 삭제
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
		getLogger().info("[보끔] 스테이터스 플러그인이 언로드 되었습니다.");
	}
	
	public boolean onCommand(CommandSender talker, Command command, String string, String[] args)
	{
	  if ((talker instanceof Player)){
		  Player p = (Player) talker;
		  if(string.equalsIgnoreCase("스탯")){
			  StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
			  if(args.length <= 0){
				  if(statData != null){
					  statData.updateInv();
					  p.openInventory(statData.statInv);
				  } else p.sendMessage(title+"당신은 스탯데이터가 없습니다.");
			  } else if(args.length >= 1 && p.isOp()){
				  if(args[0].equalsIgnoreCase("리로드")){
					  loadConfig(false);
					  loadDatas();
					  p.sendMessage(title+"스탯데이터를 리로드 했습니다.");
				  } else if(args[0].equalsIgnoreCase("속도")) p.setWalkSpeed(Float.valueOf(args[1])); //디버깅 명령어
				  else if(args[0].equalsIgnoreCase("주기"))giveStat(p, args);
				  else if(args[0].equalsIgnoreCase("인첸트")){
					  enchant(p, args);
				  } else if(args[0].equalsIgnoreCase("리스폰")){
					    getConfig().set("스폰월드", p.getLocation().getWorld().getName());
					    getConfig().set("스폰x", Integer.valueOf(p.getLocation().getBlockX()));
					    getConfig().set("스폰y", Integer.valueOf(p.getLocation().getBlockY() + 1));
					    getConfig().set("스폰z", Integer.valueOf(p.getLocation().getBlockZ()));
					    saveConfig();
					    //spawnLoc = new Location(Bukkit.getWorld(getConfig().getString("스폰월드")), getConfig().getInt("스폰x"), getConfig().getInt("스폰y"), getConfig().getInt("스폰z"));
					    p.sendMessage(title+"스폰 설정이 완료되었습니다.");
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
		try{ //설정값 로드 시도
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
			if(baseHp == 0)resetConfig(); //설정값이 잘못됐으면(기본체력이0일떄) 초기화
			else getLogger().info(title+"설정값을 성공적으로 로드했습니다.");
		} catch(Exception e) { //설정값이 잘못됐으면 초기화
			if(!retry)
			resetConfig();
		}
	}
	
	public void enchant(Player p, String args[]){
		if(args.length < 3)p.sendMessage(title+
				"/스탯 인첸트 <공격력/방어력/레벨제한/추가공격/공격제한/추가균형/균형제한/추가민첩/민첩제한/추가방어/방어제한/생명력흡수/추가체력/크리티컬확률> 숫자");
		else{
			if(args[1].equalsIgnoreCase("공격력") || args[1].equalsIgnoreCase("레벨제한") || 
					args[1].equalsIgnoreCase("추가공격") || args[1].equalsIgnoreCase("공격제한") || 
					args[1].equalsIgnoreCase("추가균형") || args[1].equalsIgnoreCase("균형제한") || 
					args[1].equalsIgnoreCase("추가민첩") || args[1].equalsIgnoreCase("민첩제한") || 
					args[1].equalsIgnoreCase("추가방어") || args[1].equalsIgnoreCase("방어제한") || 
					args[1].equalsIgnoreCase("생명력흡수") || args[1].equalsIgnoreCase("추가체력") || 
					/*args[1].equalsIgnoreCase("추가속도") || */args[1].equalsIgnoreCase("크리티컬확률")
					|| args[1].equalsIgnoreCase("방어력") ){
				int amt = 0;
				try{
					amt = Integer.valueOf(args[2]);
				} catch(Exception e){
					p.sendMessage(title+"올바른수를 입력해주세요.");
					return;
				}
				boolean check = false;
				ItemStack item = p.getItemInHand();
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList();
				if(item == null || item.getType() == Material.AIR) p.sendMessage(title + "해당 아이템은 인첸트 하실 수 없습니다.");
				else if(item.hasItemMeta()) {
					meta = item.getItemMeta();
					lore = meta.getLore();
					for(int i = 0; i < lore.size(); i++){
						if(lore.get(i).contains(args[1])) {
							lore.set(i, "§7] §c"+args[1]+" "+(amt < 0 ? "" : "+")+amt);
							check = true;
							break;
						}
					}
					if(!check) lore.add("§7] §c"+args[1]+" "+(amt < 0 ? "" : "+")+amt);
					meta.setLore(lore);
					item.setItemMeta(meta);
				} else {
					lore.add("§7] §c"+args[1]+" "+(amt < 0 ? "-" : "+")+amt);
					meta.setLore(lore);
					item.setItemMeta(meta);
				}
			} else {
				p.sendMessage(title+"/스탯 인첸트 <공격력/레벨제한/추가공격/공격제한/추가균형/균형제한/추가민첩/민첩제한/추가방어/방어제한/생명력흡수/추가체력/크리티컬확률> 중에서만 골라주세요.");
			}
		}
	}
	
	public void giveStat(Player p, String args[]){
		if(args.length < 4)p.sendMessage(title+"/스탯 주기 닉네임 <균형/공격/민첩/방어/포인트> 숫자(음수 입력시 스탯을 뺏을수 있습니다.");
		else {
			int amt = 0;
			String target = args[1];
			String type = args[2];
			try{
				amt = Integer.valueOf(args[3]);
			} catch(Exception e){
				p.sendMessage(title+"올바른수를 입력해주세요.");
				return;
			}
			StatData statData = StatFunction.getPlayerStatData(statList, target);
			if(statData == null) {
				p.sendMessage(title+"해당 플레이어의 스테이터스 데이터가 존재하지 않습니다.");
				return;
			}
			if(type.equalsIgnoreCase("균형")){
				statData.bal += amt;
				if(statData.bal < 0) statData.bal = 0; 
			} else if(type.equalsIgnoreCase("공격")){
				statData.atk += amt;
				if(statData.atk < 0) statData.atk = 0;
			} else if(type.equalsIgnoreCase("민첩")){
				statData.dex += amt;
				if(statData.dex < 0) statData.dex = 0;
			} else if(type.equalsIgnoreCase("방어")){
				statData.def += amt;
				if(statData.def < 0) statData.def = 0;
			} else if(type.equalsIgnoreCase("포인트")){
				statData.leftStat += amt;
				if(statData.leftStat < 0) statData.leftStat = 0;
			} else {
				p.sendMessage(title+"<균형/공격/민첩/방어/포인트> 만 입력이 가능합니다.");
				return;
			}
			statData.updateStat();
			statData.updateInv();
			p.sendMessage(title+"§a"+target+"f님에게 §a"+amt+"§f만큼의 §a"+type+"§f스탯을 지급하였습니다.");
		}
	}
	
	public void resetConfig(){
		getLogger().info(title+"설정값 로드에 실패했습니다. 설정값을 초기화합니다. \n- 기본체력을 0으로 설정하셨거나 설정값 형식이 잘못되었습니다.");
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
		getLogger().info(statList.size()+" 개의 스테이터스가 로드되었습니다.");
	}

	public void saveDatas(){
		for(StatData statData : statList) StatFunction.saveStatData(statData);
		getLogger().info(statList.size()+" 개의 스테이터스가 저장되었습니다.");
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
			else if(c == '&' || c == '§') i++;
		}
		try{
			n =Integer.valueOf(sb.toString());
		} catch(Exception e){
			instance.getLogger().info("아이템 정보추출과정에서 버그 발생");
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
			else if(c == '&' || c == '§') i++;
			else if(c == '.' && !check) {
				check = true;
				sb.append( str.charAt(i) );
			}
		}
		try{
			n = Double.valueOf(sb.toString());
		} catch(Exception e){
			instance.getLogger().info("아이템 정보추출과정에서 버그 발생");
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
		  if(e.getInventory().getTitle().equalsIgnoreCase("§f[§c스테이터스§f]")){
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
						p.sendMessage(title+"해당 장비는 레벨 제한으로 인하여 사용하실 수 없습니다. §c부족한 레벨:"+(limitLv - statData.lv));
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
			p.sendMessage(title+"레벨업을 하여 §c"+1*leftStatPerLv+"§f스탯을 획득하였습니다.");
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
				p.sendMessage(title+"해당 장비의 사용조건을 충족하지 못하여서 사용하실 수 없습니다.");
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
//			p.sendMessage(title+"SimpleStatus 플러그인 0.55ver - 개발: Bokum");
			p.sendMessage(title+"환영합니다. 당신의 스테이터스 데이터를 생성하였습니다.");
			p.sendMessage(title+"기본값: §aHP: "+baseHp+", LV: 0, BAL: 0 ATK: 0, DEX: 0, DEF: 0");
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
					p.sendMessage(title+"부활하셨습니다.");
				}*/
			}
			e.setDamage(chgDamage);
		} 
	}
	
	//플레이어가 엔티티 타격시 데미지 설정
	@EventHandler
	public void onEntityHitEntity(EntityDamageByEntityEvent e){
		Entity hitEntity = e.getDamager(); //타격한 대상
		Entity damagedEntity = e.getEntity(); //피격된 대상
		double chgDamage = 0; //첫 데미지는 0부터 시작
		if(hitEntity instanceof Player && damagedEntity instanceof LivingEntity){ //피겨겨 대상이 몬스터 또는 플레이어이고 타격한 대상이 플레이어일경우
			Player p = (Player) hitEntity; //타격한 대상을 플레이어로 캐스팅하여 저장
			LivingEntity le = (LivingEntity) damagedEntity; //피격된 대상도 캐스팅하여 저장
			if(StatFunction.hasStat(statList, p.getName())){ //타격한 대상이 스탯을 가지고있을시
				StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
				ItemStack item = p.getItemInHand(); //플레이어가 들고있는 무기
				if(!ItemFunction.isMetaItem(item)) return; //만약 해당무기가 아이템데이터를 가진다면
				ItemData itemData = ItemFunction.getAllItemData(p.getItemInHand()); //아이템 데이터 불러오기
				if(itemData == null) return;
				if(ItemFunction.isStatCanUse(statData, itemData)){
					chgDamage = statData.damage+statData.bonusDmg;
					if(Getrandom(1, (int)(100d/(statData.cri+statData.bonusCri))) == 1){
						p.sendMessage(title+"크리티컬!");
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
					p.sendMessage(title+"해당 장비의 사용조건을 충족하지 못하여서 사용하실 수 없습니다.");
				}
			}
		}
	}
	
	//우클릭시 갑옷 착용 캔슬, 무기 상호작용 금지
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		StatData statData = StatFunction.getPlayerStatData(statList, p.getName());
		if(statData != null) {
			ItemStack item = e.getItem();
			if(ItemFunction.isMetaItem(item)){ //해당아이템이 기본아이템 또는 손이아니면
				ItemData itemData = ItemFunction.getAllItemData(p.getItemInHand()); //해당 아이템의 데이터를 가져옴
				if(itemData == null) return;
				if(!ItemFunction.isStatCanUse(statData, itemData)) {
					e.setCancelled(true);
					p.sendMessage(title+"해당 장비의 사용조건을 충족하지 못하여서 사용하실 수 없습니다.");
				} 
				if(armour.contains(item.getTypeId())){
					e.setCancelled(true);
					p.sendMessage(title+"갑옷장비는 인벤토리를 여셔서 착용해주세요.");
					return;
				}
				statData.applyItems(-1);
			}
		}
	}
	
	//리스폰시 체력 및 속도 재적용
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if(StatFunction.hasStat(statList, e.getPlayer().getName())){ //스탯 가지고있는지 판별
			StatFunction.applyAbility(statList, e.getPlayer()); //속도, 체력 적용
		}
	}
	
	//경험치 드롭 0
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		e.setDroppedExp(0);
	}
	
	//배고픔 무한
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			e.setFoodLevel(20);
		}
	}
}


