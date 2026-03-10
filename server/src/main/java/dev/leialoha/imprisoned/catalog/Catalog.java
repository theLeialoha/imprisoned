package dev.leialoha.imprisoned.catalog;

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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import dev.leialoha.imprisoned.registration.RegistrationProvider;

abstract class Catalog {
    
    private static final Gson GSON = new Gson();

    public static <T> void register(RegistrationProvider<T> provider, Codec<T> codec, String folder) {
        final Plugin PLUGIN = ImprisonedPlugin.getPlugin(ImprisonedPlugin.class);
        final File DATA_FOLDER = new File(PLUGIN.getDataFolder(), folder);

        if (DATA_FOLDER.mkdirs())
            copyJarContents(DATA_FOLDER, folder + "/");

        if (DATA_FOLDER.exists() && !DATA_FOLDER.isDirectory()) return;
            loadFolder(provider, codec, DATA_FOLDER);
    }

    protected static <T> void loadFolder(RegistrationProvider<T> provider, Codec<T> codec, File folder) {
        for (File file : folder.listFiles()) {
            if (isJsonFile(file)) {
                try ( FileReader reader = new FileReader(file) ) {
                    JsonElement element = GSON.fromJson(reader, JsonElement.class);
                    DataResult<T> result = codec.parse(JsonOps.INSTANCE, element);
        
                    reader.close();
    
                    T entry = result.getOrThrow();
                    String fileName = file.getName()
                        .replaceAll("\\.json$", "");
    
                    provider.register(fileName, entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                loadFolder(provider, codec, file);
            }
        }
    }

    protected static void copyJarContents(File target, String folder) {
        try {
            ProtectionDomain domain = Catalog.class.getProtectionDomain();
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

    private static boolean isJsonFile(File file) {
        return file.isFile() && file.getName().endsWith(".json");
    }

}
