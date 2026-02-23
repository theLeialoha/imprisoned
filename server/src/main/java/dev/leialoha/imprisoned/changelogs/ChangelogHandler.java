package dev.leialoha.imprisoned.changelogs;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.ProtectionDomain;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.bukkit.plugin.Plugin;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import net.minecraft.resources.Identifier;

public class ChangelogHandler {
    
    private static final Map<Identifier, Changelog> CHANGELOGS;
    private static final File CHANGELOG_FOLDER;

    public static void loadChangelogs() {
        loadInternal();
        loadExternal();
    }

    public static Map<Identifier, Changelog> getChangelogs() {
        return Map.copyOf(CHANGELOGS);
    }

    private static void loadExternal() {
        try {
            if (!CHANGELOG_FOLDER.exists()) CHANGELOG_FOLDER.mkdirs();
    
            Files.find(CHANGELOG_FOLDER.toPath(), Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile()
            ).forEach(ChangelogHandler::loadChangelog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadInternal() {
        try {
            ProtectionDomain domain = ChangelogHandler.class.getProtectionDomain();
            CodeSource source = domain.getCodeSource();
            if (source == null) return;
    
            URL urlLocation = source.getLocation();
            String fileLocation = urlLocation.getPath();
            String location = URLDecoder.decode(fileLocation, StandardCharsets.UTF_8);

            JarFile jar = new JarFile(location);
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements())
                loadChangelog(jar, entries.nextElement());
    
            jar.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(JarFile jar, JarEntry entry) {
        try {
            InputStream entryStream = jar.getInputStream(entry);
            String entryName = entry.getName();
            
            if (entryName.matches("^changelogs/.+\\.changediff"))
                loadChangelog(entryStream, Identifier.tryBuild("internal", entryName));
    
            entryStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(Path path) {
        try {
            InputStream fileStream = Files.newInputStream(path);
            String fileName = path.getFileName().toString();
            loadChangelog(fileStream, Identifier.tryBuild("external", fileName));
    
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(InputStream stream, Identifier identifier) throws Exception {
        byte[] bytes = stream.readAllBytes();
        String content = new String(bytes, StandardCharsets.UTF_8);

        ChangelogEntry[] entries = Stream.of(content.split("\n"))
            .map(ChangelogEntry::create).toArray(ChangelogEntry[]::new);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(bytes);
        String hash = Base64.getEncoder().encodeToString(hashBytes);
        
        Changelog changelog = new Changelog(hash, entries);
        CHANGELOGS.put(identifier, changelog);
        // ChangelogEntry entry = ChangelogEntry.create(content);
    }

    static {
        CHANGELOGS = new HashMap<>();
    
        Plugin plugin = ImprisonedPlugin.getPlugin(ImprisonedPlugin.class);
        File dataFolder = plugin.getDataFolder();

        CHANGELOG_FOLDER = new File(dataFolder, "changelogs");
    }

}
