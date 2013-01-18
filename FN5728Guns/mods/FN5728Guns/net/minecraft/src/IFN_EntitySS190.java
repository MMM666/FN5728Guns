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
	private double damage;	// ダメージ倍率
	private int knockbackStrength;		// ノックバック
	public EntityLiving thrower;
	public boolean isInfinity;
	public static boolean isTracer = false;


	public static EntityLiving getAvatar(EntityLiving pEntity){
		// TODO:littleMaid用コードここから
		try {
			// 射手の情報をEntityLittleMaidAvatarからEntityLittleMaidへ置き換える
			Field field = pEntity.getClass().getField("avatar");
			pEntity = (EntityLiving)field.get(pEntity);
		}
		catch (NoSuchFieldException e) {
		}
		catch (Exception e) {
		}
		// ここまで
		return pEntity;
	}
	
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
		// 弾道を安定させるために耐火属性を付与
		isImmuneToFire = !worldObj.isRemote;
		isInfinity = false;
	}

	public IFN_EntitySS190(World world) {
		super(world);
	}

	public IFN_EntitySS190(World world, double px, double py, double pz) {
		this(world);
		
		setLocationAndAngles(px, py, pz, 0F, 0F);
	}

	public IFN_EntitySS190(World world, EntityLiving entityliving, float f) {
		this(world, entityliving, f, 35.8F);
	}

	public IFN_EntitySS190(World world, EntityLiving entityliving, float f, float speed) {
		super(world, getAvatar(entityliving));
		try {
			thrower = (EntityLiving)ModLoader.getPrivateValue(EntityThrowable.class, this, 6);//entityliving;
		} catch (Exception e) {
		}
		
		// f:ぶれ
		// speed:弾速
		setLocationAndAngles(entityliving.posX, entityliving.posY + (double)entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
		posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
		setPosition(posX, posY, posZ);
		float f1 = 0.4F;
		motionX = -MathHelper.sin((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;
		motionZ = MathHelper.cos((rotationYaw / 180F) * 3.141593F) * MathHelper.cos((rotationPitch / 180F) * 3.141593F) * f1;
		motionY = -MathHelper.sin(((rotationPitch + func_70183_g()) / 180F) * 3.141593F) * f1;
		setThrowableHeading(motionX, motionY, motionZ, speed, f);
	}

	@Override
	public void setThrowableHeading(double d, double d1, double d2, float f, float f1) {
		super.setThrowableHeading(d, d1, d2, f, f1);
		ticksInGround = 0;
	}

	@Override
	protected float func_70182_d() {
		// 弾速、意味なし
		return 35.8F;
	}
	
	@Override
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		// 変な処理がついてるので上書き
		this.setPosition(par1, par3, par5);
		this.setRotation(par7, par8);
	}
	
	@Override
	public void setVelocity(double par1, double par3, double par5) {
		// 弾速が早過ぎるとパケットの方で速度制限がかかっているため弾道が安定しなくなる。
		// 基本的にVelocityのアップデートが無ければ問題ないが、燃えているとおかしくなる。
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;
		
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var7) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}
	}

	@Override
	public void setFire(int par1) {
		// 耐火属性がついているときは時間を4倍
		super.setFire(isImmuneToFire ? par1 * 4 : par1);
	}
	

	@Override
	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
//        super.onUpdate();
		super.onEntityUpdate();
//		if(throwableShake > 0)
//		{
//			throwableShake--;
//		}
		if(inGround) {
			if (thrower == null) {
				setDead();
				return;
			}
			
			int i = worldObj.getBlockId(xTile, yTile, zTile);
			if(i != inTile) {
				setDead();
				return;
			} else {
				ticksInGround++;
				if(ticksInGround == 1200) {
					setDead();
				}
				
				if (isBurning() && (ticksInGround == 1)) {
//					System.out.println("light");
					worldObj.setLightValue(EnumSkyBlock.Block, xTile, yTile, zTile, 0xff);
					worldObj.updateAllLightTypes(xTile - 1, yTile, zTile);
					worldObj.updateAllLightTypes(xTile + 1, yTile, zTile);
					worldObj.updateAllLightTypes(xTile, yTile - 1, zTile);
					worldObj.updateAllLightTypes(xTile, yTile + 1, zTile);
					worldObj.updateAllLightTypes(xTile, yTile, zTile - 1);
					worldObj.updateAllLightTypes(xTile, yTile, zTile + 1);
				}
				return;
			}
		} else {
			ticksInAir++;
		}
		Vec3 vec3d;
		Vec3 vec3d1;
		MovingObjectPosition movingobjectposition;
		while (true) {
			// 特定物と当たった場合の処理
			vec3d = Vec3.createVectorHelper(posX, posY, posZ);
			vec3d1 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			movingobjectposition = worldObj.rayTraceBlocks_do_do(vec3d, vec3d1, false, true);
			if (movingobjectposition == null) break;
			xTile = movingobjectposition.blockX;
			yTile = movingobjectposition.blockY;
			zTile = movingobjectposition.blockZ;
			int bid = worldObj.getBlockId(xTile, yTile, zTile);
			// 窓ガラス、鉢植の破壊
			if (bid == Block.thinGlass.blockID || bid == Block.flowerPot.blockID) {
				motionX *= 0.8;
				motionY *= 0.8;
				motionZ *= 0.8;
				
				onBlockDestroyed(xTile, yTile, zTile);
			} else {
				break;
			}
		}
		
		vec3d = Vec3.createVectorHelper(posX, posY, posZ);
		vec3d1 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
		if(movingobjectposition != null) {
			vec3d1 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		}
		if(!worldObj.isRemote) {
			Entity entity = null;
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double d = 0.0D;
			for(int k = 0; k < list.size(); k++) {
				Entity entity1 = (Entity)list.get(k);
				if(!entity1.canBeCollidedWith() || entity1 == thrower && ticksInAir < 5)
				{
					continue;
				}
				float f4 = 0.3F;
				AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f4, f4, f4);
				MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
				if(movingobjectposition1 == null)
				{
					continue;
				}
				double d1 = vec3d.distanceTo(movingobjectposition1.hitVec);
				if(d1 < d || d == 0.0D)
				{
					entity = entity1;
					d = d1;
				}
			}
			
			if(entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}
		}
		if(movingobjectposition != null) {
			onImpact(movingobjectposition);
		}
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float)((Math.atan2(motionX, motionZ) * 180D) / 3.1415927410125732D);
		for(rotationPitch = (float)((Math.atan2(motionY, f) * 180D) / 3.1415927410125732D); rotationPitch - prevRotationPitch < -180F; prevRotationPitch -= 360F) { }
		for(; rotationPitch - prevRotationPitch >= 180F; prevRotationPitch += 360F) { }
		for(; rotationYaw - prevRotationYaw < -180F; prevRotationYaw -= 360F) { }
		for(; rotationYaw - prevRotationYaw >= 180F; prevRotationYaw += 360F) { }
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float f1 = 0.99F;
		float f2 = getGravityVelocity();
		if(isInWater())
		{
			for(int j = 0; j < 4; j++)
			{
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
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.entityHit != null) {
			// ダメージの距離減衰を付けた
			float f1 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			int j1 = (int)Math.ceil((double)f1 * damage / 10D);
//            System.out.println(String.format("ss190 - %d", j1));
			if (isBurning()) {
				movingobjectposition.entityHit.setFire(5);
			}
			if (mod_IFN_FN5728Guns.isArmorPiercing) {
				// スーパーアーマー無視
				movingobjectposition.entityHit.hurtResistantTime = 0;
			}
			if (thrower instanceof EntityPlayer) {
				// RSHUD対策・当たり判定
				((EntityPlayer)thrower).addStat(StatList.damageDealtStat, j1);
			}
			if(movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), j1)) {
				// ダメージが通った
				if (movingobjectposition.entityHit instanceof EntityLiving)
				{
					EntityLiving lel = (EntityLiving)movingobjectposition.entityHit;
					
					// TODO:今のところ意味不明
					if (!this.worldObj.isRemote) {
						lel.setArrowCountInEntity(lel.getArrowCountInEntity() + 1);
					}
					// ノックバック
					if (knockbackStrength > 0)
					{
						float f7 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
						if (f7 > 0.0F)
						{
							movingobjectposition.entityHit.addVelocity((motionX * (double)knockbackStrength * 0.60000002384185791D) / (double)f7, 0.10000000000000001D, (motionZ * (double)knockbackStrength * 0.60000002384185791D) / (double)f7);
						}
					}
					EnchantmentThorns.func_92096_a(this.thrower, lel, this.rand);
					
					// TODO:インフィでなければ経験値を出すようにする。
					if (isInfinity) {
						lel.recentlyHit = 0;
					}
				}
			}
			worldObj.playSoundAtEntity(movingobjectposition.entityHit, "FN5728.bullethitEntity", 1.0F, rand.nextFloat() * 0.2F + 0.9F);
			Vec3 vec3d = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 vec3d2 = Vec3.createVectorHelper(motionX, motionY, motionZ);
			double d1 = vec3d.distanceTo(movingobjectposition.hitVec) / vec3d2.lengthVector();
			posX += motionX * d1;
			posY += motionY * d1;
			posZ += motionZ * d1;
			setDead();
		} else {
			// ブロックにあたった
			xTile = movingobjectposition.blockX;
			yTile = movingobjectposition.blockY;
			zTile = movingobjectposition.blockZ;
			inTile = worldObj.getBlockId(xTile, yTile, zTile);
//            inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
			motionX = (float)(movingobjectposition.hitVec.xCoord - posX);
			motionY = (float)(movingobjectposition.hitVec.yCoord - posY);
			motionZ = (float)(movingobjectposition.hitVec.zCoord - posZ);
			float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
			posX -= (motionX / (double)f2) * 0.05000000074505806D;
			posY -= (motionY / (double)f2) * 0.05000000074505806D;
			posZ -= (motionZ / (double)f2) * 0.05000000074505806D;
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			inGround = true;
			// 燃えてるときの光源判定
			if (!worldObj.isRemote) {
				worldObj.playSoundAtEntity(this, "FN5728.bullethitBlock", 1.0F, rand.nextFloat() * 0.2F + 0.9F);
				
				if (Block.blocksList[inTile] instanceof BlockTNT) {
					// TNTを起爆
					Block.tnt.onBlockDestroyedByExplosion(worldObj, xTile, yTile, zTile);
					worldObj.setBlockWithNotify(xTile, yTile, zTile, 0);
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}
		posX -= motionX;
		posY -= motionY;
		posZ -= motionZ;
	}

	@Override
	public void setDead() {
		super.setDead();
		// 燃えてるときの光源判定
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


	// 独自
	public void onBlockDestroyed(int blockX, int blockY, int blockZ) {
		int bid = worldObj.getBlockId(blockX, blockY, blockZ);
		int bmd = worldObj.getBlockMetadata(blockX, blockY, blockZ);
		Block block = Block.blocksList[bid];
		if(block == null) {
			return;
		}
		worldObj.playAuxSFX(2001, blockX, blockY, blockZ, bid + (bmd  << 12));
		boolean flag = worldObj.setBlockWithNotify(blockX, blockY, blockZ, 0);
		if (block != null && flag) {
			block.onBlockDestroyedByPlayer(worldObj, blockX, blockY, blockZ, bmd);
		}
	}

}
