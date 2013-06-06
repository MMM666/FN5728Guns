package net.minecraft.src;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Field;
import java.util.List;

public class IFN_EntitySS190 extends EntityThrowable {
	
	protected int xTile;
	protected int yTile;
	protected int zTile;
	protected int inTile;
	protected int ticksInGround;
	protected int ticksInAir;
	protected double damage;	// �_���[�W�{��
	protected int knockbackStrength;		// �m�b�N�o�b�N
	public EntityLiving thrower;
	public boolean isInfinity;
	public static boolean isTracer = false;


	@Override
	protected void entityInit() {
		xTile = -1;
		yTile = -1;
		zTile = -1;
		inTile = 0;
		ticksInAir = 0;
		ticksInGround = 0;
		yOffset = 0.0F;
		damage = 2D;
		knockbackStrength = 0;
		// �e�������肳���邽�߂ɑωΑ�����t�^
		if (worldObj != null) {
			isImmuneToFire = !worldObj.isRemote;
		}
		isInfinity = false;
	}

	@Override
	public void setWorld(World par1World) {
		super.setWorld(par1World);
		isImmuneToFire = !worldObj.isRemote;
	}

	public IFN_EntitySS190(World world) {
		super(world);
	}

	public IFN_EntitySS190(World world, double px, double py, double pz) {
		this(world);
		
		setLocationAndAngles(px, py, pz, 0F, 0F);
	}

	public IFN_EntitySS190(World world, EntityLiving entityliving, float f) {
		this(world, entityliving, f, 1.0F);
	}

	public IFN_EntitySS190(World world, EntityLiving entityliving, float f, float speedRate) {
		super(world, (EntityLiving)MMM_Helper.getAvatarEntity(entityliving));
		thrower = super.getThrower();
//		try {
//			thrower = (EntityLiving)ModLoader.getPrivateValue(EntityThrowable.class, this, 6);//entityliving;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		// f:�Ԃ�
		// speed:�e��
		setLocationAndAngles(entityliving.posX, entityliving.posY + (double)entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
		posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
		setPosition(posX, posY, posZ);
		float f1 = 0.4F;
		motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;
		motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;
		motionY = -MathHelper.sin(((rotationPitch + func_70183_g()) / 180F) * 3.141593F) * f1;
		setThrowableHeading(motionX, motionY, motionZ, func_70182_d() * speedRate, f);
	}

	@Override
	public void setThrowableHeading(double d, double d1, double d2, float f, float f1) {
		super.setThrowableHeading(d, d1, d2, f, f1);
		ticksInGround = 0;
	}

	@Override
	protected float func_70182_d() {
		// �e��
		return 35.8F;
	}

	@Override
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		// �ςȏ��������Ă�̂ŏ㏑��
		if (!inGround) {
			setPosition(par1, par3, par5);
			setRotation(par7, par8);
		}
//		mod_IFN_FN5728Guns.Debug(String.format("move:%f, %f, %f", par1, par3, par5));
	}

	@Override
	public void setVelocity(double par1, double par3, double par5) {
		// �e�������߂���ƃp�P�b�g�̕��ő��x�������������Ă��邽�ߒe�������肵�Ȃ��Ȃ�B
		// ��{�I��Velocity�̃A�b�v�f�[�g��������Ζ��Ȃ����A�R���Ă���Ƃ��������Ȃ�B
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;
		
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var7) * 180.0D / Math.PI);
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}
	}

	@Override
	public void setFire(int par1) {
		// �ωΑ��������Ă���Ƃ��͎��Ԃ�4�{
		super.setFire(isImmuneToFire ? par1 * 4 : par1);
	}
	
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canAttackWithItem() {
		return false;
	}


	@Override
	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
//        super.onUpdate();
		super.onEntityUpdate();
		if (throwableShake > 0) {
			throwableShake--;
			isAirBorne = true;
		}
		if (inGround) {
			if (thrower == null) {
				setDead();
			}
			
			int i = worldObj.getBlockId(xTile, yTile, zTile);
			if(i != inTile) {
				setDead();
			} else {
				ticksInGround++;
				if(ticksInGround == 1200) {
					setDead();
				}
				
				if (isBurning() && (ticksInGround == 1)) {
					// �R���Ă�Ƃ��̌�������
//					mod_IFN_FN5728Guns.Debug("light");
					worldObj.setLightValue(EnumSkyBlock.Block, xTile, yTile, zTile, 0xff);
					worldObj.updateAllLightTypes(xTile - 1, yTile, zTile);
					worldObj.updateAllLightTypes(xTile + 1, yTile, zTile);
					worldObj.updateAllLightTypes(xTile, yTile - 1, zTile);
					worldObj.updateAllLightTypes(xTile, yTile + 1, zTile);
					worldObj.updateAllLightTypes(xTile, yTile, zTile - 1);
					worldObj.updateAllLightTypes(xTile, yTile, zTile + 1);
				}
			}
			return;
		} else {
			ticksInAir++;
		}
		
		Vec3 lvo;
		Vec3 lvt;
		Vec3 lvh = null;
		MovingObjectPosition lmop;
		MovingObjectPosition lmop1;
		
		while (true) {
			// �ŏ��ɓ�����u���b�N�̔���
			lvo = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
			lvt = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
			lmop = worldObj.rayTraceBlocks_do_do(lvo, lvt, false, true);
			lvo = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
			if (lmop == null) {
				lvt = worldObj.getWorldVec3Pool().getVecFromPool(posX + motionX, posY + motionY, posZ + motionZ);
			} else {
				lvt = worldObj.getWorldVec3Pool().getVecFromPool(lmop.hitVec.xCoord, lmop.hitVec.yCoord, lmop.hitVec.zCoord);
			}
			// Entity�ɑ΂��铖�蔻��
			// �I�u�W�F�N�g�j��̂��߂ɃT�[�o�[�A�N���C�A���g�����Ŕ���B
			Entity lentity = null;
			List llist = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double llmax = Double.MAX_VALUE;
			float lda = 0.3F;
			for(int k = 0; k < llist.size(); k++) {
				Entity entity1 = (Entity)llist.get(k);
				if (!entity1.canBeCollidedWith() || entity1 == thrower && ticksInAir < 5) {
					continue;
				}
				AxisAlignedBB laabb = entity1.boundingBox.expand(lda, lda, lda);
				lmop1 = laabb.calculateIntercept(lvo, lvt);
				if (lmop1 == null) continue;
				double ld = lvo.distanceTo(lmop1.hitVec);
				if (ld < llmax) {
					lentity = entity1;
					llmax = ld;
					lvh = lmop1.hitVec;
				}
			}
			// �����Ώۂ�����ꍇEntity������B
			if (lentity != null) {
				lmop = new MovingObjectPosition(lentity);
				lmop.hitVec = lvh;
			}
			if (lmop != null) {
				if (lmop.typeOfHit == EnumMovingObjectType.TILE &&
						worldObj.getBlockId(lmop.blockX, lmop.blockY, lmop.blockZ) == Block.portal.blockID) {
					setInPortal();
					break;
				} else {
					onImpact(lmop);
					if (inGround) break;
				}
			} else {
				break;
			}
		}
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
		for (rotationPitch = (float)((Math.atan2(motionY, f) * 180D) / 3.1415927410125732D); rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) { }
		for (; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) { }
		for (; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) { }
		for (; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) { }
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float f1 = 0.99F;
		float f2 = getGravityVelocity();
		if (isInWater()) {
			for(int j = 0; j < 4; j++) {
				float f3 = 0.25F;
				worldObj.spawnParticle("bubble", posX - motionX * (double)f3, posY - motionY * (double)f3, posZ - motionZ * (double)f3, motionX, motionY, motionZ);
			}
			f1 = 0.8F;
		}
		motionX *= f1;
		motionY *= f1;
		motionZ *= f1;
		motionY -= f2;
		setPosition(posX, posY, posZ);
		doBlockCollisions();
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.entityHit != null) {
			// �_���[�W�̋���������t����
			float lfd = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			int ldam = (int)Math.ceil((double)lfd * damage / 10D);
//			mod_IFN_FN5728Guns.Debug(String.format("ss190 - %d", j1));
			if (isBurning()) {
				movingobjectposition.entityHit.setFire(5);
			}
			if (mod_IFN_FN5728Guns.isArmorPiercing) {
				// �X�[�p�[�A�[�}�[����
				movingobjectposition.entityHit.hurtResistantTime = 0;
			}
			if (thrower instanceof EntityPlayer) {
				// RSHUD�΍�E�����蔻��
				((EntityPlayer)thrower).addStat(StatList.damageDealtStat, ldam);
			}
			if(movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), ldam)) {
				// �_���[�W���ʂ���
				if (movingobjectposition.entityHit instanceof EntityLiving) {
					EntityLiving lel = (EntityLiving)movingobjectposition.entityHit;
					
					// �m�b�N�o�b�N
					if (knockbackStrength > 0) {
						if (lfd > 0.0F) {
							lel.addVelocity(
									(motionX * (double)knockbackStrength * 0.60000002384185791D) / (double)lfd,
									(motionY * (double)knockbackStrength * 0.60000002384185791D) / (double)lfd + 0.1D,
									(motionZ * (double)knockbackStrength * 0.60000002384185791D) / (double)lfd);
						}
					}
					// ����
					if (thrower != null) {
						EnchantmentThorns.func_92096_a(thrower, lel, this.rand);
					}
					
					// TODO:�C���t�B�łȂ���Όo���l���o���悤�ɂ���B
					if (isInfinity) {
						lel.recentlyHit = 0;
					}
				}
			}
			movingobjectposition.entityHit.playSound("FN5728.bullethitEntity", 1.0F, rand.nextFloat() * 0.2F + 0.9F);
			motionX = movingobjectposition.hitVec.xCoord - posX;
			motionY = movingobjectposition.hitVec.yCoord - posY;
			motionZ = movingobjectposition.hitVec.zCoord - posZ;
			inGround = true;
			setDead();
		} else {
			// �u���b�N�ɂ�������
			xTile = movingobjectposition.blockX;
			yTile = movingobjectposition.blockY;
			zTile = movingobjectposition.blockZ;
			inTile = worldObj.getBlockId(xTile, yTile, zTile);
//            inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
			// ���K���X�A���A�̔j��
			if (inTile == Block.thinGlass.blockID || inTile == Block.flowerPot.blockID) {
				motionX *= 0.8;
				motionY *= 0.8;
				motionZ *= 0.8;
				
				onBlockDestroyed(xTile, yTile, zTile);
			} else {
				if (Block.blocksList[inTile] instanceof BlockTNT) {
					// TNT���N��
//					Block.blocksList[inTile].onBlockDestroyedByExplosion(worldObj, xTile, yTile, zTile, new Explosion(worldObj, thrower, xTile, yTile, zTile, 0.0F));
					((BlockTNT)Block.blocksList[inTile]).func_94391_a(worldObj, xTile, yTile, zTile, 1, thrower);
					worldObj.setBlockToAir(xTile, yTile, zTile);
					setDead();
				} else {
				}
				motionX = movingobjectposition.hitVec.xCoord - posX;
				motionY = movingobjectposition.hitVec.yCoord - posY;
				motionZ = movingobjectposition.hitVec.zCoord - posZ;
				inGround = true;
				if (!worldObj.isRemote) {
					worldObj.playSoundAtEntity(this, "FN5728.bullethitBlock", 1.0F, rand.nextFloat() * 0.2F + 0.9F);
					
				}
			}
			mod_IFN_FN5728Guns.Debug("Block:%d, %d, %d", xTile, yTile, zTile);
			isAirBorne = true;
			velocityChanged = true;
		}
		if (inGround) {
			for (int i = 0; i < 8; i++) {
				worldObj.spawnParticle("snowballpoof",
						movingobjectposition.hitVec.xCoord,
						movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		// �R���Ă�Ƃ��̌�������
		worldObj.setLightValue(EnumSkyBlock.Block, xTile, yTile, zTile, 0x00);
		worldObj.updateAllLightTypes(xTile - 1, yTile, zTile);
		worldObj.updateAllLightTypes(xTile + 1, yTile, zTile);
		worldObj.updateAllLightTypes(xTile, yTile - 1, zTile);
		worldObj.updateAllLightTypes(xTile, yTile + 1, zTile);
		worldObj.updateAllLightTypes(xTile, yTile, zTile - 1);
		worldObj.updateAllLightTypes(xTile, yTile, zTile + 1);
		worldObj.updateAllLightTypes(xTile, yTile, zTile);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short)xTile);
		nbttagcompound.setShort("yTile", (short)yTile);
		nbttagcompound.setShort("zTile", (short)zTile);
		nbttagcompound.setByte("inTile", (byte)inTile);
//		nbttagcompound.setByte("shake", (byte)throwableShake);
		nbttagcompound.setByte("inGround", (byte)(inGround ? 1 : 0));
		nbttagcompound.setDouble("damage", damage);
		nbttagcompound.setInteger("knockback", knockbackStrength);
		nbttagcompound.setBoolean("isInfinity", isInfinity);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		xTile = nbttagcompound.getShort("xTile");
		yTile = nbttagcompound.getShort("yTile");
		zTile = nbttagcompound.getShort("zTile");
		inTile = nbttagcompound.getByte("inTile") & 0xff;
//		throwableShake = nbttagcompound.getByte("shake") & 0xff;
		inGround = nbttagcompound.getByte("inGround") == 1;
		damage = nbttagcompound.getDouble("damage");
		knockbackStrength = nbttagcompound.getInteger("knockback");
		isInfinity = nbttagcompound.getBoolean("isInfinity");
	}

	public void setDamage(double d) {
		damage = d;
	}

	public double getDamage() {
		return damage;
	}

	public void setKnockbackStrength(int i) {
		knockbackStrength = i;
	}


	// �Ǝ�
	public void onBlockDestroyed(int blockX, int blockY, int blockZ) {
		int bid = worldObj.getBlockId(blockX, blockY, blockZ);
		int bmd = worldObj.getBlockMetadata(blockX, blockY, blockZ);
		Block block = Block.blocksList[bid];
		if(block == null) {
			return;
		}
		worldObj.playAuxSFX(2001, blockX, blockY, blockZ, bid + (bmd  << 12));
		boolean flag = worldObj.setBlockToAir(blockX, blockY, blockZ);
		if (block != null && flag) {
			block.onBlockDestroyedByPlayer(worldObj, blockX, blockY, blockZ, bmd);
		}
	}

}
