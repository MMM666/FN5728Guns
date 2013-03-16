package net.minecraft.src;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.client.Minecraft;

public class mod_IFN_FN5728Guns extends BaseMod {

	@MLProp(info="Bullet SS190's ItemID.(shiftedindex = -256. -1 is All Items Disable.)")
	public static int ID_SS190 = 22240; 
	@MLProp(info="Gun Five-seveN's ItemID.(shiftedindex = -256. -1 is Disable.)")
	public static int ID_FiveseveN = 22241; 
	@MLProp(info="Gun P90's ItemID.(shiftedindex = +256. -1 is Disable.)")
	public static int ID_P90 = 22242; 
	@MLProp(info="Ignore heartstime.")
	public static boolean isArmorPiercing = true; 
	@MLProp()
	public static boolean UnlimitedInfinity = false;
	@MLProp
	public static boolean isDebugMessage = false;
	@MLProp(info="EntityID. 0 is auto assing.")
	public static int uniqueEntityIDSS190 = 223;
	
	public static Item fn_fiveseven;
	public static Item fn_p90;
	public static Item fn_SS190;
	public static Class classSS190;


	public static void Debug(String pMes) {
		if (isDebugMessage) {
			System.out.println(pMes);
		}
	}

	@Override
	public String getVersion() {
		return "1.5.0-1";
	}

	@Override
	public String getName() {
		return "FN5728Gun's";
	}

	@Override
	public String getPriorities() {
		return "required-after:mod_MMM_MMMLib";
	}

	@Override
	public void load() {
		
		if (ID_SS190 < 0) return; 
		// 5.7x28mm SS190
		fn_SS190 = new IFN_ItemSS190(ID_SS190 - 256).setUnlocalizedName("SS190");
		ModLoader.addName(fn_SS190, "5.7x28mm SS190");
		ModLoader.addRecipe(new ItemStack(fn_SS190, 16), new Object[] {
			"i", "g", "g",  
			Character.valueOf('i'), Item.ingotIron,
			Character.valueOf('g'), Item.gunpowder
		});
		
		uniqueEntityIDSS190 = uniqueEntityIDSS190 == 0 ? MMM_Helper.getNextEntityID() : uniqueEntityIDSS190;
		classSS190 = MMM_Helper.getForgeClass(this, "IFN_EntitySS190");
		if (classSS190 == null) {
			return;
		}
		ModLoader.registerEntityID(classSS190, "SS190", uniqueEntityIDSS190);
		// Modloader環境下ではfn_uniqueSS190が255以下でないとSpawnEntityが呼ばれない。
		// 値を管理するのがめんどいのでスポーン判定は別で作る。
		ModLoader.addEntityTracker(this, classSS190, uniqueEntityIDSS190, 64, 10, false);
		
		// Five-seveN
		if (ID_FiveseveN > -1) {
			fn_fiveseven = new IFN_ItemFiveseveN(ID_FiveseveN - 256).setUnlocalizedName("FiveSeven");
			ModLoader.addName(fn_fiveseven, "Five-seveN");
			ModLoader.addRecipe(new ItemStack(fn_fiveseven, 1, fn_fiveseven.getMaxDamage()), new Object[] {
				"iii", "  i", 
				Character.valueOf('i'), Item.ingotIron
			});
		}
		
		// P90
		if (ID_P90 > -1) {
			fn_p90 = new IFN_ItemP90(ID_P90 - 256).setUnlocalizedName("P90");
			ModLoader.addName(fn_p90, "P90");
			ModLoader.addRecipe(new ItemStack(fn_p90, 1, fn_p90.getMaxDamage()), new Object[] {
				"i  ", "iii", "iii", 
				Character.valueOf('i'), Item.ingotIron
			});
		}
		
		// タクティカルリロード用パケット
		ModLoader.registerPacketChannel(this, "IFN");
	}

	@Override
	public void addRenderer(Map map) {
		if (ID_SS190 >= 0) {
			// 継承クラスにも適用されるので個別登録は必要ない
			map.put(IFN_EntitySS190.class, new IFN_RenderSS190());
		}
	}

	@Override
	public Entity spawnEntity(int entityId, World world, double scaledX, double scaledY, double scaledZ) {
		// Modloader下では独自に生成するので要らない。
		// というかModLoader環境ではIDが3000以上になるのでここは呼ばれない。
		if (!MMM_Helper.isForge) return null;
		try {
			Constructor<IFN_EntitySS190> lconstructor = classSS190.getConstructor(World.class);
			IFN_EntitySS190 lentity = lconstructor.newInstance(world);
			lentity.entityId = entityId;
			lentity.setPosition(scaledX, scaledY, scaledZ);
			return lentity;
		} catch (Exception e) {
		}
		return null;
	}

	//Modloader
	@Override
	public Packet23VehicleSpawn getSpawnPacket(Entity var1, int var2) {
		// 弾を発生させる
		// Forge環境下では呼ばれない
		EntityLiving lentity = ((IFN_EntitySS190)var1).thrower;
		return new IFN_PacketSS190Spawn(var1, uniqueEntityIDSS190, lentity == null ? 0 : lentity.entityId);
	}

	@Override
	public void serverCustomPayload(NetServerHandler handler, Packet250CustomPayload packet) {
		// タクティカルリロード用
		EntityPlayerMP lplayer = handler.playerEntity;
		ItemStack lis = lplayer.getCurrentEquippedItem();
		if (lis != null && lis.getItem() instanceof IFN_ItemFN5728) {
			IFN_ItemFN5728 lifn = (IFN_ItemFN5728)lis.getItem();
			Debug(String.format("reciveIFN:%s:%04x", packet.channel, (packet.data[0] << 8) | packet.data[1]));
			if (lifn.isReload(lis)) {
				Debug(String.format("reloadNow."));
			} else {
				Debug(String.format("setReload."));
				lifn.setReload(lis, (packet.data[0] << 8) | packet.data[1]);
				lplayer.clearItemInUse();
			}
//			MMM_Helper.updateCheckinghSlot(lplayer, lis);
		}
	}

}
