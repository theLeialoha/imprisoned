package dev.leialoha.imprisoned.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class MinecraftUtils {

    private MinecraftUtils() {
        throw new IllegalAccessError("Class doesn't need to be initalized");
    }

    public static ServerGamePacketListenerImpl getPacketListener(Player player) {
        ServerPlayer serverPlayer = getHandle(player);
        return serverPlayer.connection;
    }

    public static ServerPlayer getHandle(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle();
    }

    public static net.minecraft.world.item.ItemStack getNMSItem(ItemStack item) {
        return CraftItemStack.asNMSCopy(item);
    }

    public static ItemStack getBukkitItem(net.minecraft.world.item.ItemStack item) {
        return CraftItemStack.asBukkitCopy(item);
    }

    public static BlockState getIBlockData(Material material) throws Exception {
        CraftBlockData blockData = CraftBlockData.newData(material.asBlockType(), null);
        return blockData.getState();
    }

    public static LevelChunk cloneChunk(LevelChunk chunk) throws Exception {
        Level level = chunk.getLevel();
        ChunkPos pos = chunk.getPos();

        return new LevelChunk(level, pos);
    }

    public static Location getLocation(BlockPos blockPos, World world) {
        return new Location(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos getBlockPos(Location location) {
        return new BlockPos(location.blockX(), location.blockY(), location.blockZ());
    }


    // public static void listVariables(Player player) throws Exception {
    //     Class<?> entityPlayerClass = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");

    //     Method getPrevXMethod = entityPlayerClass.getMethod("dg");
    //     Method getPrevYMethod = entityPlayerClass.getMethod("di");
    //     Method getPrevZMethod = entityPlayerClass.getMethod("dm");
    //     Method getPrevYawMethod = entityPlayerClass.getMethod("dr");
    //     Method getPrevPitchMethod = entityPlayerClass.getMethod("dt");

    //     Object playerConnection = ReflectionUtils.getConnection(player);
    //     Object playerEntity = ReflectionUtils.getHandle(player);

    //     double d0 = a(packetplayinflying.a(this.c.dg()));
    //     double d1 = b(packetplayinflying.b(this.c.di()));
    //     double d2 = a(packetplayinflying.c(this.c.dm()));

    //     double prevX = (double) getPrevXMethod.invoke(playerEntity);
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

    public static void sendPacket(Packet<?> packet, Player player) {
        ServerGamePacketListenerImpl connection = getPacketListener(player);
        connection.send(packet);
    }

    public static void sendPacketToAll(Packet<?> packet) {
        Bukkit.getOnlinePlayers()
            .forEach(p -> sendPacket(packet, p));
    }

    public static void sendPacketToNearby(Packet<?> packet, Location pos) {
        sendPacketToNearby(packet, pos, 60);
    }

    public static void sendPacketToNearby(Packet<?> packet, Location pos, int i) {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getLocation().distance(pos) <= i)
            .forEach(p -> sendPacket(packet, p));
    }

    public static Function<String, Collection<String>> splitString(int size) {
        return (string) -> {
            if (string == null || string.isBlank())
                return List.of();

            List<String> out = new ArrayList<>();

            int currentPixels = 0;
            StringBuilder current = new StringBuilder();

            List<BiComponent<String, Integer>> wordSizes = Stream.of(
                string.split("(?<=\\s)")).map(word -> 
                    new BiComponent<>(word, word.chars())
                        .mapSecond(s -> 
                            s.mapToObj(c -> (char) c)
                                .mapToInt(FontSpacing::getSize)
                                .sum()
                        )
            ).toList();

            for (BiComponent<String,Integer> component : wordSizes) {
                String word = component.getFirst();
                int pixels = component.getSecond() + word.length();

                if (currentPixels + pixels > size) {
                    currentPixels = 0;
                    out.add(current.toString());
                    current.setLength(0);
                }

                currentPixels += pixels;
                current.append(word);
            }

            String currentBuffer = current.toString();
            if (!currentBuffer.isBlank()) out.add(currentBuffer);
        
            return out;
        };
    }



    enum FontSpacing {
        SPACE(' ', 3), EXCLAMATION_POINT('!', 1), DOUBLE_QUOTE('"', 3), OCTOTHORPE('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), AMPERSAND('&', 5), SINGLE_QUOTE('\'', 1), OPEN_PARENTHESES('(', 3), CLOSED_PARENTHESES(')', 3), ASTERISK('*', 3), PLUS_SIGN('+', 5), COMMA(',', 2), MINUS_SIGN('-', 5), PERIOD('.', 1), FORWARD_SLASH('/', 5),
        ZERO('0', 5), ONE('1', 5), TWO('2', 5), THREE('3', 5), FOUR('4', 5), FIVE('5', 5), SIX('6', 5), SEVEN('7', 5), EIGHT('8', 5), NINE('9', 5), COLON(':', 1), SIMICOLON(';', 1), OPEN_BRACKET('<', 5), EQUAL_SIGN('=', 5), CLOSED_BRACKET('>', 5), QUESTION_MARK('?', 5),
        AT_SIGN('@', 5), UPPER_A('A', 5), UPPER_B('B', 5), UPPER_C('C', 5), UPPER_D('D', 5), UPPER_E('E', 5), UPPER_F('F', 5), UPPER_G('G', 5), UPPER_H('H', 5), UPPER_I('I', 3), UPPER_J('J', 5), UPPER_K('K', 5), UPPER_L('L', 5), UPPER_M('M', 5), UPPER_N('N', 5), UPPER_O('O', 5),
        UPPER_P('P', 5), UPPER_Q('Q', 5), UPPER_R('R', 5), UPPER_S('S', 5), UPPER_T('T', 5), UPPER_U('U', 5), UPPER_V('V', 5), UPPER_W('W', 5), UPPER_X('X', 5), UPPER_Y('Y', 5), UPPER_Z('Z', 5), OPEN_SQUARE_BRACKET('[', 3), BACK_SLASH('\\', 5), CLOSED_SQUARE_BRACKET(']', 3), CARET('^', 5), UNDERSCORE('_', 5),
        GRAVE('`', 2), LOWER_A('a', 5), LOWER_B('b', 5), LOWER_C('c', 5), LOWER_D('d', 5), LOWER_E('e', 5), LOWER_F('f', 4), LOWER_G('g', 5), LOWER_H('h', 5), LOWER_I('i', 1), LOWER_J('j', 5), LOWER_K('k', 4), LOWER_L('l', 2), LOWER_M('m', 5), LOWER_N('n', 5), LOWER_O('o', 5),
        LOWER_P('p', 5), LOWER_Q('q', 5), LOWER_R('r', 5), LOWER_S('s', 5), LOWER_T('t', 3), LOWER_U('u', 5), LOWER_V('v', 5), LOWER_W('w', 5), LOWER_X('x', 5), LOWER_Y('y', 5), LOWER_Z('z', 5), OPEN_SQUIGGLY_BRACKET('{', 3), VERTICAL_BAR('|', 1), CLOSED_SQUIGGLY_BRACKET('}', 3), TILDE('~', 5),

        DEFAULT('a', 5);

        public final char character;
        public final int size;

        FontSpacing(char character, int size) {
            this.character = character;
            this.size = size;
        }

        public static int getSize(char character) {
            return get(character).size;
        }

        public static FontSpacing get(char character) {
            for (FontSpacing spacing : values())
                if (spacing.character == character)
                    return spacing;

            return DEFAULT;
        }
    }

}
