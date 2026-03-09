package dev.leialoha.imprisoned.catalog;

import dev.leialoha.imprisoned.drop.DropTable;
import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class Drops extends Catalog {

    private static final RegistrationProvider<DropTable> PROVIDER = RegistrationProvider.of(RegistryKeys.DROPS, "imprisoned");

    public static void init() {
        register(PROVIDER, DropTable.CODEC, "drops");
    }
    
}
