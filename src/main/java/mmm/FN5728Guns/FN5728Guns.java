package mmm.FN5728Guns;

import java.io.File;

import mmm.lib.ProxyCommon;
import mmm.lib.guns.EntityBulletBase;
import mmm.lib.guns.GunsBase;
import mmm.lib.guns.ItemGunsBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(
		modid	= "FN5728Guns",
		name	= "FN5728Guns",
		version	= "1.7.x-srg-1"
		)
public class FN5728Guns {

	@SidedProxy(clientSide = "mmm.FN5728Guns.ProxyClient", serverSide = "mmm.lib.ProxyCommon")
	public static ProxyCommon proxy;
	
	public static boolean isArmorPiercing = true; 
	public static boolean UnlimitedInfinity = false;
	public static boolean isDebugMessage = true;
	
	public static Item fn_fiveseven;
	public static Item fn_p90;
	public static Item fn_SS190;
	protected static File configFile;


	public static void Debug(String pText, Object... pData) {
		// デバッグメッセージ
		if (isDebugMessage) {
			System.out.println(String.format("FN5728-" + pText, pData));
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent pEvent) {
		// コンフィグの解析・設定
		configFile = pEvent.getSuggestedConfigurationFile();
		Configuration lconf = new Configuration(configFile);
		lconf.load();
		isDebugMessage	= lconf.get("RefinedMilitaryShovelReplica", "isDebugMessage", true).getBoolean(true);
		lconf.save();
		
		// アイテムの登録
		fn_SS190		= new ItemSS190().setUnlocalizedName("ss19").setTextureName("mmm:SS190");
		fn_fiveseven	= new ItemFiveseveN().setUnlocalizedName("fiveseven").setTextureName("mmm:FiveseveN");
		fn_p90			= new ItemP90().setUnlocalizedName("p90").setTextureName("mmm:P90");
		
		GameRegistry.registerItem(fn_SS190, "SS190");
		GameRegistry.registerItem(fn_fiveseven, "FiveseveN");
		GameRegistry.registerItem(fn_p90, "P90");
		
		((ItemGunsBase)fn_fiveseven).init();
		((ItemGunsBase)fn_p90).init();
		
		EntityRegistry.registerModEntity(EntityBulletBase.class, "BulletBase", 0, this, 120, 1, false);
		
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent pEvent) {
		// 一応ドキュメント上ではここでレシピとかを宣言するらしい。
		GunsBase.init();
		// レシピ
		// 5.7x28mm SS190
		GameRegistry.addRecipe(new ItemStack(fn_SS190, 16),
				"i",
				"g",
				"g",
				'i', Items.iron_ingot,
				'g', Items.gunpowder
			);
		// 5.7x28mm L191
		GameRegistry.addRecipe(new ItemStack(fn_SS190, 16, 1),
				"ir",
				"g ",
				"g ",
				'i', Items.iron_ingot,
				'g', Items.gunpowder,
				'r', new ItemStack(Items.dye, 1, 1)
			);
		// 5.7x28mm SS197SR
		GameRegistry.addRecipe(new ItemStack(fn_SS190, 20, 7),
				"ir",
				"g ",
				"g ",
				'i', Items.iron_ingot,
				'g', Items.gunpowder,
				'r', new ItemStack(Items.dye, 1, 12)
			);
		// Five-seveN
		GameRegistry.addRecipe(new ItemStack(fn_fiveseven, 1, fn_fiveseven.getMaxDamage()),
				"iii",
				"  i", 
				'i', Items.iron_ingot,
				'g', Items.gunpowder
			);
		// P90
		GameRegistry.addRecipe(new ItemStack(fn_p90, 1, fn_p90.getMaxDamage()),
				"i  ",
				"iii",
				"iii", 
				'i', Items.iron_ingot
			);
		
		// レンダラの登録
		proxy.init();
		
		
//		FMLCommonHandler.instance().bus().register(new RefinedMilitaryShovelReplicaEventHandler());
//		ModLoader.registerPacketChannel(this, "IFN");
		
		// MMMLibのRevisionチェック
//		MMM_Helper.checkRevision("7");
//		MMM_Config.checkConfig(this.getClass());
	}

}
