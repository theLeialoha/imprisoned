package dev.leialoha.imprisoned.block;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class Blocks {
    
    private static final Gson GSON = new Gson();
    private static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.of(RegistryKeys.BLOCKS, "imprisoned");


    // public static final RegistryEntry<Block> DIRT = register(Material.DIRT)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> STONE = register(Material.STONE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> COBBLESTONE = register(Material.COBBLESTONE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> ANDESITE = register(Material.ANDESITE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> IRON_ORE = register(Material.IRON_ORE)
    //     .with(maxHealth(100));






    // private static RegistryEntry<Block> register(Material material) {
    //     ResourceKey key = BukkitConversion.from(material.getKey());
    //     BlockData data = new BlockData(key);

    //     String safe = key.toShortString().replace(':', '.');
    //     return register(safe, new Block(data));
    // }

    // private static RegistryEntry<Block> register(String name, Block entry) {
    //     return PROVIDER.register(name, entry);
    // }

    // private static Consumer<Block> maxHealth(int maxHealth) {
    //     return (block) -> block.setMaxHealth(maxHealth);
    // }


    public static void init() {
        final Plugin PLUGIN = ImprisonedPlugin.getPlugin(ImprisonedPlugin.class);
        final File BLOCKS_FOLDER = new File(PLUGIN.getDataFolder(), "blocks");

        if (BLOCKS_FOLDER.mkdirs())
            copyJarContents(BLOCKS_FOLDER, "blocks/");

        if (BLOCKS_FOLDER.exists() && !BLOCKS_FOLDER.isDirectory()) return;

        for (File file : BLOCKS_FOLDER.listFiles(File::isFile)) {
            try ( FileReader reader = new FileReader(file) ) {
                JsonElement element = GSON.fromJson(reader, JsonElement.class);
                DataResult<Block> result = Block.CODEC.parse(JsonOps.INSTANCE, element);
    
                reader.close();
                
                Block block = result.getOrThrow();
                String fileName = file.getName()
                    .replaceAll("\\.json$", "");

                PROVIDER.register(fileName, block);
                System.out.println("Added block: " + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void copyJarContents(File target, String folder) {
        try {
            ProtectionDomain domain = Blocks.class.getProtectionDomain();
            CodeSource source = domain.getCodeSource();
            URL url = source.getLocation();
            File file = new File(url.toURI());
    
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                try {
                    JarEntry entry = entries.nextElement();
                    if (entry.isDirectory()) continue;
                    if (!entry.getName().startsWith(folder)) continue;
    
                    String fileName = entry.getName().replaceFirst(folder, "");
                    File targetFile = new File(target, fileName);

                    File parentFile = targetFile.getParentFile();
                    if (!parentFile.exists()) parentFile.mkdirs();
    
                    InputStream inputStream = jar.getInputStream(entry);
                    OutputStream outputStream = new FileOutputStream(targetFile);
    
                    inputStream.transferTo(outputStream);
    
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            jar.close();
        } catch (Exception e) {}
    }
    
}
