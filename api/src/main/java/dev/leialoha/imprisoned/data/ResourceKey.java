package dev.leialoha.imprisoned.data;

import io.netty.buffer.ByteBufUtil;

import java.util.function.UnaryOperator;

public class ResourceKey implements Comparable<ResourceKey> {

    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    private final String namespace;
    private final String path;

    private ResourceKey(String namespace, String path) {
        assert isValidNamespace(namespace);

        assert isValidPath(path);

        final String resourceKey = namespace + ":" + path;
        if (resourceKey.length() > Short.MAX_VALUE || ByteBufUtil.utf8MaxBytes(resourceKey) > 2 * Short.MAX_VALUE + 1) {
            throw new IllegalArgumentException("Resource key too long: " + resourceKey);
        }

        this.namespace = namespace;
        this.path = path;
    }

    private static ResourceKey createUntrusted(String namespace, String path) {
        return new ResourceKey(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    public static ResourceKey fromNamespaceAndPath(String namespace, String path) {
        return createUntrusted(namespace, path);
    }

    public static ResourceKey parse(String key) {
        return bySeparator(key, ':');
    }

    public static ResourceKey tryParse(String key) {
        return tryBySeparator(key, ':');
    }

    public static ResourceKey tryBuild(String namespace, String path) {
        return isValidNamespace(namespace) && isValidPath(path) ? new ResourceKey(namespace, path) : null;
    }

    public static ResourceKey bySeparator(String key, char separator) {
        int index = key.indexOf(separator);
        if (index >= 0) {
            String sub = key.substring(index + 1);
            if (index != 0) {
                String sub1 = key.substring(0, index);
                return createUntrusted(sub1, sub);
            } else {
                return createUntrusted(DEFAULT_NAMESPACE, sub);
            }
        } else {
            return createUntrusted(DEFAULT_NAMESPACE, key);
        }
    }

    public static ResourceKey tryBySeparator(String key, char separator) {
        int index = key.indexOf(separator);
        if (index >= 0) {
            String sub = key.substring(index + 1);
            if (!isValidPath(sub)) {
                return null;
            } else if (index != 0) {
                String sub1 = key.substring(0, index);
                return isValidNamespace(sub1) ? new ResourceKey(sub1, sub) : null;
            } else {
                return new ResourceKey(DEFAULT_NAMESPACE, sub);
            }
        } else {
            return isValidPath(key) ? new ResourceKey(DEFAULT_NAMESPACE, key) : null;
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ResourceKey withPath(String path) {
        return new ResourceKey(this.namespace, assertValidPath(this.namespace, path));
    }

    public ResourceKey withPath(UnaryOperator<String> pathOperator) {
        return this.withPath(pathOperator.apply(this.path));
    }

    public ResourceKey withPrefix(String pathPrefix) {
        return this.withPath(pathPrefix + this.path);
    }

    public ResourceKey withSuffix(String pathSuffix) {
        return this.withPath(this.path + pathSuffix);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ResourceKey ResourceKey && this.namespace.equals(ResourceKey.namespace) && this.path.equals(ResourceKey.path);
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(ResourceKey other) {
        int i = this.path.compareTo(other.path);
        if (i == 0) i = this.namespace.compareTo(other.namespace);
        return i;
    }

    public String toDebugFileName() {
        return this.toString().replace('/', '_').replace(':', '_');
    }

    public String toLanguageKey() {
        return this.namespace + "." + this.path;
    }

    public String toShortLanguageKey() {
        return this.namespace.equals(DEFAULT_NAMESPACE) ? this.path : this.toLanguageKey();
    }

    public String toShortString() {
        return this.namespace.equals(DEFAULT_NAMESPACE) ? this.path : this.toString();
    }

    public String toLanguageKey(String type) {
        return type + "." + this.toLanguageKey();
    }

    public String toLanguageKey(String type, String key) {
        return type + "." + this.toLanguageKey() + "." + key;
    }

    public static boolean isAllowedInResourceKey(char character) {
        return character >= '0' && character <= '9'
            || character >= 'a' && character <= 'z'
            || character == '_' || character == ':'
            || character == '/' || character == '.'
            || character == '-';
    }

    public static boolean isValidPath(String path) {
        for (int i = 0; i < path.length(); i++)
            if (!validPathChar(path.charAt(i)))
                return false;
        return true;
    }

    public static boolean isValidNamespace(String namespace) {
        for (int i = 0; i < namespace.length(); i++)
            if (!validNamespaceChar(namespace.charAt(i)))
                return false;
        return true;
    }

    private static String assertValidPath(String namespace, String path) {
        if (!isValidPath(path)) {
            throw new IllegalArgumentException("Non [a-z0-9_./-] character in path of key: " + normalizeSpace(namespace) + ":" + normalizeSpace(path));
        } else {
            return path;
        }
    }

    private static String assertValidNamespace(String namespace, String path) {
        if (!isValidNamespace(namespace)) {
            throw new IllegalArgumentException("Non [a-z0-9_.-] character in namespace of key: " + normalizeSpace(namespace) + ":" + normalizeSpace(path));
        } else {
            return namespace;
        }
    }

    private static String normalizeSpace(String string) {
        return string.trim().replaceAll("\\s+", " ");
    }

    public static boolean validPathChar(char pathChar) {
        return pathChar == '_' || pathChar == '-'
            || pathChar >= 'a' && pathChar <= 'z'
            || pathChar >= '0' && pathChar <= '9'
            || pathChar == '/' || pathChar == '.';
    }

    private static boolean validNamespaceChar(char namespaceChar) {
        return namespaceChar == '_' || namespaceChar == '-'
            || namespaceChar >= 'a' && namespaceChar <= 'z'
            || namespaceChar >= '0' && namespaceChar <= '9'
            || namespaceChar == '.';
    }

}
