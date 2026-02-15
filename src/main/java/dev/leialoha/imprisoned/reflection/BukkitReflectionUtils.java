package dev.leialoha.imprisoned.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitReflectionUtils {
    
    private static final String VERSION;
	// private static final boolean NEW_NMS = PluginAJar.getMinecraftVersion().isEqualOrAfter(new Version(1, 17));

	public static Class<?> getOBCClass(String obcClassString) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + VERSION + obcClassString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getNMSClass(String nmsClass, String nmsPackage) {
        try {
            return Class.forName(nmsPackage + "." + nmsClass);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("net.minecraft.server." + VERSION + nmsClass);
            } catch (ClassNotFoundException e2) {
                e2.addSuppressed(e);
                e2.printStackTrace();
                return null;
            }
		}
	}

	public static Optional<Object> getConnection(Player player) {
        Class<?> entityPlayerClass = BukkitReflectionUtils.getNMSClass("EntityPlayer", "net.minecraft.server.level");
		Class<?> playerConnectionClass = BukkitReflectionUtils.getNMSClass("PlayerConnection", "net.minecraft.server.network");
        
        return getHandle(player)
            .map(h -> Reflection.getFieldByType(h, entityPlayerClass, playerConnectionClass));
	}

	public static Optional<Object> getHandle(Player player) {
		Class<?> craftPlayerClass = BukkitReflectionUtils.getOBCClass("entity.CraftPlayer");

        return Optional.of(player)
            .map(p -> Reflection.cast(p, craftPlayerClass))
            .map(p -> Reflection.callMethod(p, "getHandle"));
	}

	public static Object getNMSItem(ItemStack item) {
		try {
			Class<?> craftItemStackClass = BukkitReflectionUtils.getOBCClass("inventory.CraftItemStack");
			Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			return asNMSCopyMethod.invoke(null, item);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public static ItemStack getBukkitItem(Object item) {
		// if (!item.getClass().equals(ReflectionUtils.getNMSClass("ItemStack", "net.minecraft.world.item"))) return null;
		try {
			// org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
			Class<?> nmsItemStackClass = BukkitReflectionUtils.getNMSClass("ItemStack", "net.minecraft.world.item");
			Class<?> craftItemStackClass = BukkitReflectionUtils.getOBCClass("inventory.CraftItemStack");
			Method asBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
			return (ItemStack) asBukkitCopyMethod.invoke(null, item);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	public static Object getIBlockData(Material material) throws Exception {
		Class<?> craftBlockDataClass = BukkitReflectionUtils.getOBCClass("block.data.CraftBlockData");
		Method newDataMethod = craftBlockDataClass.getMethod("newData", Material.class, String.class);
		Method getStateMethod = craftBlockDataClass.getMethod("getState");

		Object craftBlockData = newDataMethod.invoke(null, material, null);
		Object iBlockData = getStateMethod.invoke(craftBlockData);
		
		return iBlockData;
	}

	public static Object cloneChunk (Object loadedChunk) throws Exception {
		Class<?> chunkClass = BukkitReflectionUtils.getNMSClass("Chunk", "net.minecraft.world.level.chunk");
		Class<?> worldClass = BukkitReflectionUtils.getNMSClass("World", "net.minecraft.world.level");
		Class<?> chunkCoordClass = BukkitReflectionUtils.getNMSClass("ChunkCoordIntPair", "net.minecraft.world.level");
		// Class<?> cChunkClass = ReflectionUtils.getNMSClass("Chunk$c", "net.minecraft.world.level.chunk");
		// Class<?> chunkConverterClass = ReflectionUtils.getNMSClass("ChunkConverter", "net.minecraft.world.level.chunk");
		// Class<?> levelChunkTickClass = ReflectionUtils.getNMSClass("LevelChunkTicks", "net.minecraft.world.ticks");
		// Class<?> chunkSectionClass = ReflectionUtils.getNMSClass("ChunkSection", "net.minecraft.world.level.chunk");
		// Class<?> chunkSectionArrayClass = Reflection.toArrayClass(chunkSectionClass);
		// Class<?> blendingDataClass = ReflectionUtils.getNMSClass("BlendingData", "net.minecraft.world.level.levelgen.blending");
		
		Method getWorldMethod = chunkClass.getMethod("D");
		Method getPosMethod = chunkClass.getMethod("f");
		// Method getChunkConverterMethod = chunkClass.getMethod("r");
		// Method getInhabitedTime = chunkClass.getMethod("u");
		// Method getSections = chunkClass.getMethod("d");
		
		Object world = getWorldMethod.invoke(loadedChunk);
		Object chunkcoordintpair = getPosMethod.invoke(loadedChunk);
		// Object chunkconverter = getChunkConverterMethod.invoke(loadedChunk);
		// Object levelchunkticks = Reflection.getField("u", chunkClass, loadedChunk);
		// Object levelchunkticks1 = Reflection.getField("v", chunkClass, loadedChunk);
		// long i = (long) getInhabitedTime.invoke(loadedChunk);
		// Object achunksection = getSections.invoke(loadedChunk);
	
		Constructor<?> chunkConstructor = chunkClass.getConstructor(worldClass, chunkCoordClass);
		return chunkConstructor.newInstance(world, chunkcoordintpair);
	}



	// public static void listVariables(Player player) throws Exception {
	// 	Class<?> entityPlayerClass = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");

	// 	Method getPrevXMethod = entityPlayerClass.getMethod("dg");
	// 	Method getPrevYMethod = entityPlayerClass.getMethod("di");
	// 	Method getPrevZMethod = entityPlayerClass.getMethod("dm");
	// 	Method getPrevYawMethod = entityPlayerClass.getMethod("dr");
	// 	Method getPrevPitchMethod = entityPlayerClass.getMethod("dt");

	// 	Object playerConnection = ReflectionUtils.getConnection(player);
	// 	Object playerEntity = ReflectionUtils.getHandle(player);

	// 	double d0 = a(packetplayinflying.a(this.c.dg()));
	// 	double d1 = b(packetplayinflying.b(this.c.di()));
	// 	double d2 = a(packetplayinflying.c(this.c.dm()));

	// 	double prevX = (double) getPrevXMethod.invoke(playerEntity);
    //     double prevY = (double) getPrevYMethod.invoke(playerEntity);
    //     double prevZ = (double) getPrevZMethod.invoke(playerEntity);
    //     float prevYaw = (float) getPrevYawMethod.invoke(playerEntity);
    //     float prevPitch = (float) getPrevPitchMethod.invoke(playerEntity);
    //     double d3 = (double) getPrevXMethod.invoke(playerEntity);
    //     double d4 = (double) getPrevYMethod.invoke(playerEntity);
    //     double d5 = (double) getPrevZMethod.invoke(playerEntity);
    //     double d6 = (double) getPrevYMethod.invoke(playerEntity);
    //     double d7 = d0 - this.o;
    //     double d8 = d1 - this.p;
    //     double d9 = d2 - this.q;
    //     double d10 = this.c.de().g();
    //     double d11 = d7 * d7 + d8 * d8 + d9 * d9;
	// }

    static {
        String[] packages = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        VERSION = (packages.length > 3) ? (packages[3] + ".") : "";
    }
}
