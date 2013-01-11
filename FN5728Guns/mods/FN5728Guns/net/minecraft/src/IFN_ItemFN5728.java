package net.minecraft.src;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IFN_ItemFN5728 extends ItemBow {
	
	/*
	 * �����[�h�̃V�[�P���X
	 * 0x0000	:�ˌ����
	 * 0x1000	:�����[�h�J�n
	 * 0x2000	:�}�K�W�������[�X�A����24bit�̓����[�h���̎c�e
	 * 0x8000	:�����[�h����
	 */
	
	
	public IFN_ItemFN5728(int i) {
		super(i);
		setMaxDamage(0);
		maxStackSize = 1;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// �����[�h����
		reloadMagazin(itemstack, world, entityplayer);
		return itemstack;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityPlayer entityplayer, int i) {
		// �����[�h���~
		cancelReload(itemstack, 0x8000);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world,	EntityPlayer entityplayer) {
		// �g���K�[
		int li = getReload(itemstack);
		if (li <= 0) {
			if (canReload(itemstack, entityplayer)) {
				// �m�[�}�������[�h
				if (isEmpty(itemstack)) {
					releaseMagazin(itemstack, world, entityplayer);
					MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
				}
			}
		}
		if (li == 0x0010) {
			if (canReload(itemstack, entityplayer)) {
				// �^�N�e�B�J�������[�h
				releaseMagazin(itemstack, world, entityplayer);
				MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
			}
		}
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
		if (world.isRemote) {
			if (MMM_Helper.mc.thePlayer != entity) {
				// �N���C�A���g�̕ێ����Ă���v���[���[�ȊO�͏�������K�v���Ȃ�
				return;
			}
			if (entity instanceof EntityPlayer) {
				if (((EntityPlayer)entity).getCurrentEquippedItem() != itemstack) {
					return;
				}
			}
			// �}���`�p�^�N�e�B�J�������[�h���菈��
			int li = getReload(itemstack);
			try {
				// �N�A���C�A���g��p�R�[�h�Ȃ̂�ForgeMP���ƃG���[���o��
				// �Ƃ��������Ń}���`����Modloader����ׂ��E�E�E�B
				if (MMM_Helper.mc.gameSettings.keyBindAttack.pressed) {
					if (li == 0x0000) {
//						System.out.println("tacticalIFN");
						li = 0x0010;
						setReload(itemstack, li);
						ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
					}
				} else {
					if (li == 0x0010) {
//						System.out.println("nomalIFN");
						li = 0x000;
						setReload(itemstack, li);
						ModLoader.clientSendPacket(new Packet250CustomPayload("IFN", new byte[] {(byte)((li >>> 8) & 0xff), (byte)(li & 0xff)}));
					}
				}
			} catch (Error e) {
			} catch (Exception e) {
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		// �����[�h���͎��Ԃ�ύX
		int li = getReload(itemstack);
		if ((li >= 0x1000) && (li & 0xf000) < 0x8000) {
			return reloadTime();
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		// �����[�h���͍\�����Ⴄ
		return isReload(itemstack) ? EnumAction.block : EnumAction.bow;
	}


	// �Ǝ�
	protected boolean fireBullet(ItemStack itemstack, World world, EntityPlayer entityplayer, float f, float f2, float f3) {
		// ���ˁi�G���`�����g�Ή��j
		if (!world.isRemote) {
			IFN_EntitySS190 entityss190 = null;
			try {
				Constructor<IFN_EntitySS190> lconstructor = mod_IFN_FN5728Guns.classSS190.getConstructor(World.class, EntityLiving.class, float.class, float.class);
				entityss190 = lconstructor.newInstance(world, entityplayer, 2.0F - f, f3);
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
		entityplayer.rotationPitch += f2;
		// �����e�̃G���`�����g�ɑΉ��A�e��g�p����true��Ԃ�
		return (!mod_IFN_FN5728Guns.UnlimitedInfinity 
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) <= 0)
				&& !entityplayer.capabilities.isCreativeMode;
	}

	protected void cancelReload(ItemStack itemstack, int force) {
		if (getReload(itemstack) >= force) {
			// �����[�h�̃L�����Z��
			setReload(itemstack, 0);
		}
	}

	protected boolean canReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// �����[�h���\���ǂ����̔���i�G���`�����g�Ή��j
//		if (entityplayer.capabilities.depleteBuckets || EnchantmentHelper.getEnchantmentLevel(Enchantment.field_46042_v.effectId, itemstack) > 0) return true;
		if (entityplayer.capabilities.isCreativeMode) return true;
		for (ItemStack is : entityplayer.inventory.mainInventory) {
			if (is != null && is.getItem().itemID == getBulletID(itemstack)) return true;
		}
		return false;
	}

	protected boolean isEmpty(ItemStack itemstack) {
		// �c�e�[���H
		return itemstack.getItemDamage() >= getMaxDamage();
	}

	protected void releaseMagazin(ItemStack itemstack, World world, Entity entity) {
		// �}�K�W���������[�X�����Ƃ��̓���A�c�e���L�^
		setReload(itemstack, (0x2000 | (itemstack.getItemDamage() & 0x0fff)));
		itemstack.setItemDamage(getMaxDamage());
	}

	protected void reloadMagazin(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		// �}�K�W������ꂽ�Ƃ��̓���
//		if (!world.isRemote) 
		{
			// �����[�h
			if (entityplayer == null || entityplayer.capabilities.isCreativeMode
					|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) > 0) {
				itemstack.setItemDamage(0);
			} else {
				// �C���x���g������e������炷
				int k = getReload(itemstack);
				k = (k > 0) ? k & 0x0fff : 0;
				for (int l = 0; l < entityplayer.inventory.mainInventory.length; l++) {
					ItemStack is = entityplayer.inventory.mainInventory[l];
					if (is != null && is.getItem().itemID == getBulletID(itemstack)) {
						if (is.stackSize > k) {
							is.stackSize -= k;
							itemstack.setItemDamage(0);
							break;
						} else {
							k -= is.stackSize;
							itemstack.setItemDamage(k);
							is.stackSize = 0;
							entityplayer.inventory.mainInventory[l] = null;
						}
					}
				}
			}
		}
		setReload(itemstack, 0x8000);
		MMM_Helper.updateCheckinghSlot(entityplayer, itemstack);
	}

	// �����[�h�ɂ����鎞��
	public abstract int reloadTime();
	
	public int getBulletID(ItemStack itemstack) {
		// �e��̎��
		return mod_IFN_FN5728Guns.fn_SS190.itemID;
	}
	
	// littleMaidMob�͂��̃��\�b�h���Q�Ƃ��ē��ꓮ����s���܂�
	public boolean isWeaponReload(ItemStack itemstack, EntityPlayer entityplayer) {
		// �����[�h���s����ׂ����H
		cancelReload(itemstack, 0x8000);
		return isEmpty(itemstack) && canReload(itemstack, entityplayer);
	}

	public boolean isWeaponFullAuto(ItemStack itemstack) {
		// �t���I�[�g���킩�H
		// �i�E�N���b�N�������_�Ŏˌ��J�n�������́j
		return false;
	}

	/**
	 * �����[�h�J�E���^�ǂݎ��
	 */
	public int getReload(ItemStack pItemstack) {
		checkTags(pItemstack);
		return pItemstack.getTagCompound().getInteger("Reload");
	}

	/**
	 * �����[�h�J�E���^�̃Z�b�g
	 */
	public void setReload(ItemStack pItemstack, int pValue) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		lnbt.setInteger("Reload", pValue);
	}

	/**
	 * �����[�h�����ˁH
	 */
	public boolean isReload(ItemStack pItemstack) {
		return getReload(pItemstack) > 0;
	}

	// �A�˗p�̃^�C�~���O��H
	/**
	 * �A�˃^�C�~���O�̐ݒ�B
	 * 1=50ms�A20=1000ms=1s�B
	 */
	public byte getCycleCount(ItemStack pItemstack) {
		return (byte)1;
	}

	public void resetBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		pItemstack.getTagCompound().setByte("Bolt", getCycleCount(pItemstack));
	}

	/**
	 * ���˃^�C�~���O�̊m�F
	 */
	public boolean cycleBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		byte lb = lnbt.getByte("Bolt");
		if (lb <= 0) {
//			if (pReset) resetBolt(pItemstack);
			return true;
		} else {
			lnbt.setByte("Bolt", --lb);
			return false;
		}
	}

	public int getBolt(ItemStack pItemstack) {
		checkTags(pItemstack);
		NBTTagCompound lnbt = pItemstack.getTagCompound();
		return lnbt.getByte("Bolt");
	}

	public boolean checkTags(ItemStack pitemstack) {
		// NBTTag�̏�����
		if (pitemstack.hasTagCompound()) {
			return true;
		}
		NBTTagCompound ltags = new NBTTagCompound();
		pitemstack.setTagCompound(ltags);
		ltags.setInteger("Reload", 0x0000);
		ltags.setByte("Bolt", (byte)0);
		return false;
	}

	public static Entity checkMaid(Entity entity) {
		// ���C�h����`�F�b�N
		try {
			Field field = entity.getClass().getField("maidAvatar");
			entity = (Entity)field.get(entity);
		}
		catch (NoSuchFieldException e) {
		}
		catch (Exception e) {
		}
		return entity;
	}

}
