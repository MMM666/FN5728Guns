package net.minecraft.src;

import java.lang.reflect.Constructor;


/**
 * �e��p�̃N���X�B
 * �e�̔��˂Ɋւ������͂������ł���B
 */
public class IFN_ItemSS190 extends Item {

	public IFN_ItemSS190(int par1) {
		super(par1);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabCombat);
	}

	/**
	 * �e�̔��ˁB
	 * �e���ʃN���X�ɂ��Ă������Ɉڂ����H
	 * @param pDamage:�e�̃_���[�W�l
	 * @param f1:�e�����萫�B
	 * @param f2:�������
	 * @param f3:�������䗦
	 */
	protected boolean fireBullet(ItemStack itemstack, World world, EntityPlayer entityplayer, int pDamage, float f1, float f2, float f3) {
		// ���ˁi�G���`�����g�Ή��j
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
		// �v���[���[�ɑ΂��锽������Ȃǂ͂�����
		entityplayer.rotationPitch += (itemRand.nextFloat() * -3F) * f3;
		// �����e�̃G���`�����g�ɑΉ��A�e��g�p����true��Ԃ�
		return (!mod_IFN_FN5728Guns.UnlimitedInfinity 
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) <= 0)
				&& !entityplayer.capabilities.isCreativeMode;
	}

}
