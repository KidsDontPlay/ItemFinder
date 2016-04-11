package mrriegel.ifinder;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncMessage implements IMessage,
		IMessageHandler<SyncMessage, IMessage> {
	List<BlockPos> lis;

	public SyncMessage() {
	}

	public SyncMessage(EntityPlayer player) {
		lis = new ArrayList<BlockPos>();
		int range = ItemFinder.range;
		for (int i = -range; i <= range; i++)
			for (int j = -range; j <= range; j++)
				for (int k = -range; k <= range; k++) {
					BlockPos pos = new BlockPos(i + player.posX, j
							+ player.posY, k + player.posZ);
					if (player.worldObj.getTileEntity(pos) instanceof IInventory) {
						IInventory inv = (IInventory) player.worldObj
								.getTileEntity(pos);
						for (int ii = 0; ii < inv.getSizeInventory(); ii++) {
							if (inv.getStackInSlot(ii) != null
									&& inv.getStackInSlot(ii).isItemEqual(
											player.getHeldItemMainhand())) {
								lis.add(pos);
								break;
							}
						}
					}
				}
	}

	@Override
	public IMessage onMessage(final SyncMessage message, MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				// if (!message.lis.containsAll(ItemFinder.lis)
				// || !ItemFinder.lis.containsAll(message.lis))
				ItemFinder.instance.lis = new ArrayList<BlockPos>(message.lis);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.lis = new ArrayList<BlockPos>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			this.lis.add(new BlockPos(buf.readInt(), buf.readInt(), buf
					.readInt()));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.lis.size());
		for (BlockPos p : this.lis) {
			buf.writeInt(p.getX());
			buf.writeInt(p.getY());
			buf.writeInt(p.getZ());
		}
	}

}
