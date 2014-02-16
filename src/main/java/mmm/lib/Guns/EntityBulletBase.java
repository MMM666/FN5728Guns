package mmm.lib.Guns;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class EntityBulletBase extends EntityThrowable implements IThrowableEntity, IEntityAdditionalSpawnData{

	public EntityBulletBase(World par1World) {
		super(par1World);
	}
	public EntityBulletBase(World par1World,
			EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
	}
	public EntityBulletBase(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	/**
	 * 独自仕様の生成処理
	 * @param par1World
	 * @param par2EntityLivingBase
	 * @param pBullet
	 * @param pSpeed
	 * @param pf
	 */
	public EntityBulletBase(World par1World, EntityLivingBase par2EntityLivingBase,
			ItemStack pGun, ItemStack pBullet, float pSpeed, float pf) {
//		this(par1World, par2EntityLivingBase);
		this(par1World);
//		this.thrower = par2EntityLivingBase;
		ReflectionHelper.setPrivateValue(EntityThrowable.class, this, par2EntityLivingBase, "thrower");
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(par2EntityLivingBase.posX, par2EntityLivingBase.posY + (double)par2EntityLivingBase.getEyeHeight(), par2EntityLivingBase.posZ, par2EntityLivingBase.rotationYaw, par2EntityLivingBase.rotationPitch);
		this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		float f = 0.4F;
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f);
		this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.func_70183_g()) / 180.0F * (float)Math.PI) * f);
		this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, pSpeed, pf);
	}


	@Override
	protected void onImpact(MovingObjectPosition var1) {
		// TODO Auto-generated method stub
		if (var1.entityHit != null) {
			float ldamage = 1.0F;
			
			var1.entityHit.attackEntityFrom(
					DamageSource.causeThrownDamage(this, this.getThrower()), ldamage);
		}
		// 着弾パーティクル
		for (int i = 0; i < 8; ++i) {
//			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY,
			this.worldObj.spawnParticle("smoke",
					var1.hitVec.xCoord, var1.hitVec.yCoord, var1.hitVec.zCoord,
					0.0D, 0.0D, 0.0D);
		}
		
		if (!this.worldObj.isRemote) {
			this.setDead();
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		// 高加速体に対応するための処置
		buffer.writeInt(Float.floatToIntBits((float)motionX));
		buffer.writeInt(Float.floatToIntBits((float)motionY));
		buffer.writeInt(Float.floatToIntBits((float)motionZ));
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		// 高加速体に対応するための処置
		motionX = (double)Float.intBitsToFloat(additionalData.readInt());
		motionY = (double)Float.intBitsToFloat(additionalData.readInt());
		motionZ = (double)Float.intBitsToFloat(additionalData.readInt());
		setVelocity(motionX, motionY, motionZ);
	}

	@Override
	public void setThrower(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 弾丸の色
	 * 0xRRGGBB
	 * @return
	 */
	public int getBulletColor() {
		return 0x804000;
	}

}
