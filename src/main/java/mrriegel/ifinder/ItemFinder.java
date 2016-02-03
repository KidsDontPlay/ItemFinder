package mrriegel.ifinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ItemFinder.MODID, version = ItemFinder.VERSION, name = ItemFinder.MODNAME)
public class ItemFinder {
	public static final String MODID = "ifinder";
	public static final String MODNAME = "ItemFinder";
	public static final String VERSION = "1.0";

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(
			ItemFinder.MODID);

	public static List<BlockPos> lis = new ArrayList<BlockPos>();
	public static long last = 0;

	public static Configuration config;
	public static int range, color;
	public static boolean alwaysOn;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		range = config.get(Configuration.CATEGORY_GENERAL, "range", 16)
				.getInt();
		color = config.get(Configuration.CATEGORY_CLIENT, "color", -1).getInt();
		alwaysOn = config.get(Configuration.CATEGORY_CLIENT, "alwaysOn", false)
				.getBoolean();
		if (config.hasChanged())
			config.save();

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new Client());
			ClientRegistry.registerKeyBinding(Client.light);
		}
		INSTANCE.registerMessage(KeyMessage.class, KeyMessage.class, 0,
				Side.SERVER);
		INSTANCE.registerMessage(SyncMessage.class, SyncMessage.class, 1,
				Side.CLIENT);
//		List<BlockPos> li = Arrays.asList(new BlockPos[] {
//				new BlockPos(12, 3, -33), new BlockPos(-777, 1000, 0) });
//		System.out.println("lis: " + li);
//		String s = new Gson().toJson(li);
//		System.out.println(new Gson().fromJson(s,
//				new TypeToken<List<BlockPos>>() {
//				}.getType()));
	}

	// @SubscribeEvent
	// public void onKey(InputEvent.KeyInputEvent e) {
	// if (last == 0) {
	// ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	// }
	// if (light.isPressed()
	// && Minecraft.getMinecraft().theWorld.getTotalWorldTime() - last >= 30) {
	// ItemFinder.INSTANCE.sendToServer(new KeyMessage());
	// last = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	// }
	// }

}
