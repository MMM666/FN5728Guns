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
	
	
	public static Item fn_fiveseven;
	public static Item fn_p90;
	public static Item fn_SS190;
	public static int fn_uniqueSS190;
	public static Class classSS190;


	@Override
	public String getVersion() {
		return "1.4.7-1";
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
		int icon;

		if (ID_SS190 < 0) return; 
		// 5.7x28mm SS190
		icon = MMM_Helper.isForge ? 2 : ModLoader.addOverride("/gui/items.png", "/icon/SS190.png");
		fn_SS190 = new Item(ID_SS190 - 256).setIconIndex(icon).setItemName("ss190").setCreativeTab(CreativeTabs.tabCombat);
		ModLoader.addName(fn_SS190, "5.7x28mm SS190");
		ModLoader.addRecipe(new ItemStack(fn_SS190, 16), new Object[] {
			"i", "g", "g",  
			Character.valueOf('i'), Item.ingotIron,
			Character.valueOf('g'), Item.gunpowder
		});
		MMM_Helper.setForgeIcon(fn_SS190);
		
		fn_uniqueSS190 = ModLoader.getUniqueEntityId();
		classSS190 = MMM_Helper.getEntityClass(this, "IFN_EntitySS190");
		if (classSS190 == null) {
			return;
		}
		ModLoader.registerEntityID(classSS190, "SS190", fn_uniqueSS190);
		// Modloader�����ł�fn_uniqueSS190��255�ȉ��łȂ���SpawnEntity���Ă΂�Ȃ��B
		// �l���Ǘ�����̂��߂�ǂ��̂ŃX�|�[������͕ʂō��B
		ModLoader.addEntityTracker(this, classSS190, fn_uniqueSS190, 64, 10, false);
		
		// Five-seveN
		if (ID_FiveseveN > -1) {
			icon = MMM_Helper.isForge ? 18 : ModLoader.addOverride("/gui/items.png", "/icon/Five-seveN.png");
			fn_fiveseven = new IFN_ItemFiveseveN(ID_FiveseveN - 256).setIconIndex(icon).setItemName("FiveSeven");
			ModLoader.addName(fn_fiveseven, "Five-seveN");
			ModLoader.addRecipe(new ItemStack(fn_fiveseven, 1, fn_fiveseven.getMaxDamage()), new Object[] {
				"iii", "  i", 
				Character.valueOf('i'), Item.ingotIron
			});
			MMM_Helper.setForgeIcon(fn_fiveseven);
		}
		
		// P90
		if (ID_P90 > -1) {
			icon = MMM_Helper.isForge ? 34 : ModLoader.addOverride("/gui/items.png", "/icon/P90.png");
			fn_p90 = new IFN_ItemP90(ID_P90 - 256).setIconIndex(icon).setItemName("P90");
			ModLoader.addName(fn_p90, "P90");
			ModLoader.addRecipe(new ItemStack(fn_p90, 1, fn_p90.getMaxDamage()), new Object[] {
				"i  ", "iii", "iii", 
				Character.valueOf('i'), Item.ingotIron
			});
			MMM_Helper.setForgeIcon(fn_p90);
		}
		
		// �^�N�e�B�J�������[�h�p�p�P�b�g
		ModLoader.registerPacketChannel(this, "IFN");
	}

	@Override
	public void addRenderer(Map map) {
		if (ID_SS190 >= 0) {
			// �p���N���X�ɂ��K�p�����̂Ōʓo�^�͕K�v�Ȃ�
			map.put(IFN_EntitySS190.class, new IFN_RenderSS190());
		}
	}

	@Override
	public Entity spawnEntity(int entityId, World world, double scaledX, double scaledY, double scaledZ) {
		// Modloader���ł͓Ǝ��ɐ�������̂ŗv��Ȃ��B
		// �Ƃ�����ModLoader���ł�ID��3000�ȏ�ɂȂ�̂ł����͌Ă΂�Ȃ��B
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
		// �e�𔭐�������
		// Forge�����ł͌Ă΂�Ȃ�
		EntityLiving lentity = ((IFN_EntitySS190)var1).thrower;
		return new IFN_PacketSS190Spawn(var1, fn_uniqueSS190, lentity == null ? 0 : lentity.entityId);
	}

	@Override
	public void serverCustomPayload(NetServerHandler handler, Packet250CustomPayload packet) {
		// �^�N�e�B�J�������[�h�p
		EntityPlayerMP lplayer = handler.playerEntity;
		ItemStack lis = lplayer.getCurrentEquippedItem();
		if (lis != null && lis.getItem() instanceof IFN_ItemFN5728) {
			IFN_ItemFN5728 lifn = (IFN_ItemFN5728)lis.getItem();
//			System.out.println(String.format("reciveIFN:%s:%d", packet.channel, (packet.data[0] << 8) | packet.data[1]));
			lifn.setReload(lis, (packet.data[0] << 8) | packet.data[1]);
			MMM_Helper.updateCheckinghSlot(lplayer, lis);
		}
	}

}
