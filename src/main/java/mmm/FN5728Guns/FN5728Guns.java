package mmm.FN5728Guns;

import java.io.File;

import mmm.lib.Guns.EntityBulletBase;
import mmm.lib.Guns.RenderBulletBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
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
		fn_SS190		= new ItemSS190().setUnlocalizedName("ss190").setTextureName("mmm:SS190");
		fn_fiveseven	= new ItemFiveseveN().setUnlocalizedName("fiveseven").setTextureName("mmm:FiveseveN");
		fn_p90			= new ItemP90().setUnlocalizedName("p90").setTextureName("mmm:P90");
		
		GameRegistry.registerItem(fn_SS190, "SS190");
		GameRegistry.registerItem(fn_fiveseven, "FiveseveN");
		GameRegistry.registerItem(fn_p90, "P90");
		
		EntityRegistry.registerModEntity(EntityBulletBase.class, "BulletBase", 0, this, 120, 1, false);
		
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent pEvent) {
		// 一応ドキュメント上ではここでレシピとかを宣言するらしい。
		// レシピ
		// 5.7x28mm SS190
		GameRegistry.addRecipe(new ItemStack(fn_SS190, 16),
				"i",
				"g",
				"g",
				'i', Items.iron_ingot,
				'g', Items.gunpowder
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
		RenderingRegistry.registerEntityRenderingHandler(EntityBulletBase.class, new RenderBulletBase());
		
		
		
//		FMLCommonHandler.instance().bus().register(new RefinedMilitaryShovelReplicaEventHandler());
//		ModLoader.registerPacketChannel(this, "IFN");
		
		// MMMLibのRevisionチェック
//		MMM_Helper.checkRevision("7");
//		MMM_Config.checkConfig(this.getClass());
	}

}
