package mmm.lib.Guns;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;

public class EntityBulletBase extends EntityThrowable implements IThrowableEntity, IEntityAdditionalSpawnData{

	public EntityBulletBase(World par1World) {
		super(par1World);
		// TODO Auto-generated constructor stub
	}

	public EntityBulletBase(World par1World,
			EntityLivingBase par2EntityLivingBase) {
		super(par1World, par2EntityLivingBase);
		// TODO Auto-generated constructor stub
	}

	public EntityBulletBase(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		// TODO Auto-generated constructor stub
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
			this.worldObj.spawnParticle("smoke", this.posX, this.posY,
					this.posZ, 0.0D, 0.0D, 0.0D);
		}
		
		if (!this.worldObj.isRemote) {
			this.setDead();
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setThrower(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 0xRRGGBB
	 * @return
	 */
	public int getBulletColor() {
		return 0x804000;
	}

}
