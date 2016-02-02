package mrriegel.ifinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Mod(modid = ItemFinder.MODID, version = ItemFinder.VERSION, name = ItemFinder.MODNAME/*
																					 * ,
																					 * clientSideOnly
																					 * =
																					 * true
																					 */)
public class ItemFinder {
	public static final String MODID = "ifinder";
	public static final String MODNAME = "ItemFinder";
	public static final String VERSION = "1.0";
	public static KeyBinding light = new KeyBinding(ItemFinder.MODID + ".find",
			Keyboard.KEY_F, ItemFinder.MODNAME);

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		ClientRegistry.registerKeyBinding(ItemFinder.light);
	}

	@SubscribeEvent
	public void liv(LivingUpdateEvent e) {
		if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.entityLiving;
			int range = 16;
			for (int i = -range; i <= range; i++)
				for (int j = -range; j <= range; j++)
					for (int k = -range; k <= range; k++) {
						BlockPos pos = new BlockPos(i + player.posX, j
								+ player.posY, k + player.posZ);
						if (player.worldObj.getTileEntity(pos) instanceof IInventory) {
							IInventory inv = (IInventory) player.worldObj
									.getTileEntity(pos);
							for (int ii = 0; ii < inv.getSizeInventory(); ii++) {
								// if (inv.getStackInSlot(ii) != null)
//								System.out.println(inv.getStackInSlot(ii) + " "
//										+ ii);
								if (inv.getStackInSlot(ii) != null
										&& inv.getStackInSlot(ii).isItemEqual(
												player.getHeldItem())) {
									break;
								}
							}
						}
					}
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!ItemFinder.light.isKeyDown())
			return;
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if (player.getHeldItem() == null)
			return;
		List<BlockPos> lis = new ArrayList<BlockPos>();
		int range = 16;
		for (int i = -range; i <= range; i++)
			for (int j = -range; j <= range; j++)
				for (int k = -range; k <= range; k++) {
					BlockPos pos = new BlockPos(i + player.posX, j
							+ player.posY, k + player.posZ);
					if (player.worldObj.getTileEntity(pos) instanceof IInventory) {
						IInventory inv = (IInventory) player.worldObj
								.getTileEntity(pos);
						for (int ii = 0; ii < inv.getSizeInventory(); ii++) {
							// if (inv.getStackInSlot(ii) != null)
//							System.out.println(inv.getStackInSlot(ii) + " "
//									+ inv.getSizeInventory());
							if (inv.getStackInSlot(ii) != null
									&& inv.getStackInSlot(ii).isItemEqual(
											player.getHeldItem())) {
								lis.add(pos);
								break;
							}
						}
					}
				}
		// System.out.println(lis);
		double doubleX = player.lastTickPosX
				+ (player.posX - player.lastTickPosX) * event.partialTicks;
		double doubleY = player.lastTickPosY
				+ (player.posY - player.lastTickPosY) * event.partialTicks;
		double doubleZ = player.lastTickPosZ
				+ (player.posZ - player.lastTickPosZ) * event.partialTicks;
		GlStateManager.pushAttrib();
		GlStateManager.disableTexture2D();
		// GlStateManager.disableLighting();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
		// code
		for (BlockPos p : lis) {
			float ox = p.getX(), oy = p.getY(), oz = p.getZ();
			RenderHelper.enableStandardItemLighting();
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer renderer = tessellator.getWorldRenderer();
			renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			GlStateManager.color(0f, 0f, 0f, 1f);
			GL11.glLineWidth(2f);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			// ox = ox - 1f;
			// oy = oy - 1f;
			// oz = oz - 1f;

			float off = 1f;
			renderer.pos(ox, oy, oz).endVertex();
			renderer.pos(ox + off, oy, oz).endVertex();

			renderer.pos(ox, oy, oz).endVertex();
			renderer.pos(ox, oy + off, oz).endVertex();

			renderer.pos(ox, oy, oz).endVertex();
			renderer.pos(ox, oy, oz + off).endVertex();

			renderer.pos(ox + off, oy + off, oz + off).endVertex();
			renderer.pos(ox, oy + off, oz + off).endVertex();

			renderer.pos(ox + off, oy + off, oz + off).endVertex();
			renderer.pos(ox + off, oy, oz + off).endVertex();

			renderer.pos(ox + off, oy + off, oz + off).endVertex();
			renderer.pos(ox + off, oy + off, oz).endVertex();

			renderer.pos(ox, oy + off, oz).endVertex();
			renderer.pos(ox, oy + off, oz + off).endVertex();

			renderer.pos(ox, oy + off, oz).endVertex();
			renderer.pos(ox + off, oy + off, oz).endVertex();

			renderer.pos(ox + off, oy, oz).endVertex();
			renderer.pos(ox + off, oy, oz + off).endVertex();

			renderer.pos(ox + off, oy, oz).endVertex();
			renderer.pos(ox + off, oy + off, oz).endVertex();

			renderer.pos(ox, oy, oz + off).endVertex();
			renderer.pos(ox + off, oy, oz + off).endVertex();

			renderer.pos(ox, oy, oz + off).endVertex();
			renderer.pos(ox, oy + off, oz + off).endVertex();
			tessellator.draw();
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);

		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}
