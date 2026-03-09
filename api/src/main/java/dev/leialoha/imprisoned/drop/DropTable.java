package dev.leialoha.imprisoned.drop;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DropTable {
    
    public static final Codec<DropTable> CODEC;

    public List<DropEntry> entries;

    public DropTable(List<DropEntry> entries) {
        this.entries = entries;
    }

    public List<DropEntry> getEntries() {
        return entries;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                DropEntry.CODEC.listOf().fieldOf("values").forGetter(DropTable::getEntries)
            ).apply(instance, DropTable::new)
        );
    }

}
