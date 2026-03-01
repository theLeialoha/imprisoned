package dev.leialoha.imprisoned.changelog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import dev.leialoha.imprisoned.data.ResourceKey;

public class ChangelogHandler {

    private static final Map<ResourceKey, Changelog> CHANGELOGS;

    public static void loadChangelogs(File external) {
        loadInternal();
        loadExternal(external);
    }

    public static Map<ResourceKey, Changelog> getChangelogs() {
        return Map.copyOf(CHANGELOGS);
    }

    private static void loadExternal(File external) {
        try {
            if (!external.exists()) external.mkdirs();
    
            Files.find(external.toPath(), Integer.MAX_VALUE,
                (path, attributes) -> attributes.isRegularFile()
            ).forEach(ChangelogHandler::loadChangelog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadInternal() {
        ProtectionDomain domain = ChangelogHandler.class.getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        if (source == null) return;

        URL urlLocation = source.getLocation();
        String fileLocation = urlLocation.getPath();
        String location = URLDecoder.decode(fileLocation, StandardCharsets.UTF_8);

        try (JarFile jar = new JarFile(location)) {
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements())
                loadChangelog(jar, entries.nextElement());

            jar.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(JarFile jar, JarEntry entry) {
        try (InputStream entryStream = jar.getInputStream(entry)) {
            String entryName = entry.getName();
            
            if (entryName.matches("^changelogs/.+\\.changediff"))
                loadChangelog(entryStream, ResourceKey.fromNamespaceAndPath("internal", entryName));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(Path path) {
        try (InputStream fileStream = Files.newInputStream(path)) {
            String fileName = path.getFileName().toString();
            loadChangelog(fileStream, ResourceKey.fromNamespaceAndPath("external", fileName));
    
            fileStream.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void loadChangelog(InputStream stream, ResourceKey identifier) throws IOException, NoSuchAlgorithmException {
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
    }

}
