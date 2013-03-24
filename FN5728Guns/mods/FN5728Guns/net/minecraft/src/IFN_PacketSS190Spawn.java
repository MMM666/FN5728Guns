package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;

public class IFN_PacketSS190Spawn extends Packet23VehicleSpawn {
	
	public IFN_PacketSS190Spawn(Entity par1Entity, int par2, int par3) {
		super();
		
		this.entityId = par1Entity.entityId;
		this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
		this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
		this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
		this.type = par2;
		this.throwerEntityId = par3;
		this.speedX = Float.floatToIntBits((float)par1Entity.motionX);
		this.speedY = Float.floatToIntBits((float)par1Entity.motionY);
		this.speedZ = Float.floatToIntBits((float)par1Entity.motionZ);
	}

	@Override
	public void readPacketData(DataInputStream par1DataInputStream)
			throws IOException {
		this.entityId = par1DataInputStream.readInt();
		this.type = par1DataInputStream.readByte();
		this.xPosition = par1DataInputStream.readInt();
		this.yPosition = par1DataInputStream.readInt();
		this.zPosition = par1DataInputStream.readInt();
		this.throwerEntityId = par1DataInputStream.readInt();

		if (this.throwerEntityId > 0) {
			this.speedX = par1DataInputStream.readInt();
			this.speedY = par1DataInputStream.readInt();
			this.speedZ = par1DataInputStream.readInt();
		}
	}
	
	@Override
	public void writePacketData(DataOutputStream par1DataOutputStream)
			throws IOException {
		par1DataOutputStream.writeInt(this.entityId);
		par1DataOutputStream.writeByte(this.type);
		par1DataOutputStream.writeInt(this.xPosition);
		par1DataOutputStream.writeInt(this.yPosition);
		par1DataOutputStream.writeInt(this.zPosition);
		par1DataOutputStream.writeInt(this.throwerEntityId);

		if (this.throwerEntityId > 0) {
			par1DataOutputStream.writeInt(this.speedX);
			par1DataOutputStream.writeInt(this.speedY);
			par1DataOutputStream.writeInt(this.speedZ);
		}
	}
	
	@Override
	public int getPacketSize() {
		return 21 + (throwerEntityId > 0 ? 12 : 0);
	}


	@Override
	public void processPacket(NetHandler par1NetHandler) {
		if (par1NetHandler instanceof NetClientHandler) {
			Minecraft mc = MMM_Helper.mc;
			WorldClient lworld = mc.theWorld;
			double lx = (double)this.xPosition / 32.0D;
			double ly = (double)this.yPosition / 32.0D;
			double lz = (double)this.zPosition / 32.0D;
			
			Entity le = (mc.thePlayer.entityId == throwerEntityId) ? mc.thePlayer : lworld.getEntityByID(throwerEntityId);
			if (le instanceof EntityLiving) {
				IFN_EntitySS190 lentity = new IFN_EntitySS190(lworld, lx, ly, lz);
				lentity.serverPosX = this.xPosition;
				lentity.serverPosY = this.yPosition;
				lentity.serverPosZ = this.zPosition;
				lentity.rotationYaw = 0.0F;
				lentity.rotationPitch = 0.0F;
				lentity.entityId = this.entityId;
				lentity.thrower = (EntityLiving)le;
				lentity.setVelocity((double)Float.intBitsToFloat(this.speedX), (double)Float.intBitsToFloat(this.speedY), (double)Float.intBitsToFloat(this.speedZ));
				lworld.addEntityToWorld(this.entityId, lentity);
			}
		}
	}

}
