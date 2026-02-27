package dev.leialoha.imprisoned.compat;

import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.association.RegionOverlapAssociation;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import dev.leialoha.imprisoned.mines.world.BlockLocation;

public class WorldGuardCompat {

    private static boolean IS_ENABLED = false;

    public static final StateFlag BLOCK_MINING = new StateFlag("imprisoned-mining", true);
    public static final StateFlag BLOCK_SPREAD = new StateFlag("imprisoned-spread", true);
    public static final StateFlag BLOCK_TICK = new StateFlag("imprisoned-tick", true);

    public static void registerFlags(Server server) {
        Plugin worldGuardPlugin = server.getPluginManager().getPlugin("WorldGuard");
        IS_ENABLED = worldGuardPlugin != null;
        if (!IS_ENABLED) return;

        WorldGuard worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();

        registry.registerAll(List.of(
            BLOCK_MINING,
            BLOCK_SPREAD,
            BLOCK_TICK
        ));
    }

    private static ApplicableRegionSet getRegion(BlockLocation pos) {
        World world = BukkitAdapter.adapt(pos.world());
        BlockVector3 blockVector = new BlockVector3(pos.x(), pos.y(), pos.z());

        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        return container.get(world).getApplicableRegions(blockVector);
    }

    public static boolean isMiningAllowed(BlockLocation pos, Player player) {
        return !IS_ENABLED || testState(pos, player, BLOCK_MINING);
    }

    public static boolean allowInfectionSpread(BlockLocation pos) {
        return IS_ENABLED && testState(pos, BLOCK_SPREAD);
    }

    private static boolean testState(BlockLocation pos, Player player, StateFlag... flags) {
        ApplicableRegionSet set = getRegion(pos);
        LocalPlayer association = WorldGuardPlugin.inst().wrapPlayer(player);
        return testState(set, association, flags);
    }

    private static boolean testState(BlockLocation pos, StateFlag... flags) {
        ApplicableRegionSet set = getRegion(pos);
        RegionOverlapAssociation association = new RegionOverlapAssociation(set.getRegions());
        return testState(set, association, flags);
    }

    private static boolean testState(ApplicableRegionSet set, RegionAssociable association, StateFlag... flags) {
        return set.testState(association, flags);
    }

}
