package net.minecraft.src;

import java.lang.reflect.Constructor;


/**
 * 弾薬用のクラス。
 * 弾の発射に関わる実装はこっちでする。
 */
public class IFN_ItemSS190 extends Item {

	public IFN_ItemSS190(int par1) {
		super(par1);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabCombat);
	}

	/**
	 * 弾の発射。
	 * 弾薬を別クラスにしてそっちに移すか？
	 * @param pDamage:弾のダメージ値
	 * @param f1:弾道安定性。
	 * @param f2:装薬効率
	 * @param f3:反動制御率
	 */
	protected boolean fireBullet(ItemStack itemstack, World world, EntityPlayer entityplayer, int pDamage, float f1, float f2, float f3) {
		// 発射（エンチャント対応）
		if (!world.isRemote) {
			IFN_EntitySS190 entityss190 = null;
			try {
				Constructor<IFN_EntitySS190> lconstructor = mod_IFN_FN5728Guns.classSS190.getConstructor(World.class, EntityLiving.class, float.class, float.class);
				entityss190 = lconstructor.newInstance(world, entityplayer, 2.0F - f1, f2);
			} catch (Exception e) {
			}
			
			// Power
			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack);
			if (k > 0) {
				entityss190.setDamage(entityss190.getDamage() + (double)k * 0.5D + 0.5D);
			}
			// Punch
			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemstack);
			if (l > 0) {
				entityss190.setKnockbackStrength(l);
			}
			// Fire
			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemstack) > 0) {
				entityss190.setFire(100);
//				entityss190.setFlag(0, true);
			}
			// Infinity
			entityss190.isInfinity = (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) > 0);
			
			world.playSoundAtEntity(entityplayer, "FN5728.fnP90_s", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
			world.spawnEntityInWorld(entityss190);
		}
		// プレーヤーに対する反動制御などはここで
		entityplayer.rotationPitch += (itemRand.nextFloat() * -3F) * f3;
		// 無限弾のエンチャントに対応、弾薬使用時はtrueを返す
		return (!mod_IFN_FN5728Guns.UnlimitedInfinity 
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) <= 0)
				&& !entityplayer.capabilities.isCreativeMode;
	}

}
