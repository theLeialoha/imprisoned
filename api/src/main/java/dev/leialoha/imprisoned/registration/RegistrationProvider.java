package dev.leialoha.imprisoned.registration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import dev.leialoha.imprisoned.data.ResourceKey;

public final class RegistrationProvider<T> {

    private static final Set<RegistrationProvider<?>> PROVIDERS = new HashSet<>();

    private final RegistryKey<T> registeryKey;
    private final String namespace;

    private RegistrationProvider(RegistryKey<T> registeryKey, String namespace) {
        this.registeryKey = registeryKey;
        this.namespace = namespace;
    }

    @SuppressWarnings("unchecked")
    public static <T> RegistrationProvider<T> of(RegistryKey<T> registeryKey, String namespace) {
        Optional<RegistrationProvider<?>> filteredProviders = PROVIDERS.stream()
            .filter(p -> p.getNamespace().equalsIgnoreCase(namespace))
            .filter(p -> p.registeryKey.equals(registeryKey))
            .findFirst();

        if (filteredProviders.isPresent())
            return (RegistrationProvider<T>) filteredProviders.get();

        RegistrationProvider<T> provider = new RegistrationProvider<>(registeryKey, namespace);
        PROVIDERS.add(provider);
        return provider;
    }

    public RegistryEntry<T> register(String entryName, T entry) {
        ResourceKey resourceKey = ResourceKey.fromNamespaceAndPath(namespace, entryName.toLowerCase());
        return getRegistry().register(resourceKey, entry);
    }

    protected Registry<T> getRegistry() {
        return registeryKey.getRegistry();
    }

    public RegistryKey<T> getRegisteryKey() {
        return registeryKey;
    }

    public Collection<RegistryEntry<T>> getEntries() {
        return getRegistry().getEntries().stream()
            .filter(entry -> entry.getKey().getNamespace().equals(namespace))
            .toList();
    }

    public String getNamespace() {
        return namespace;
    }

}
