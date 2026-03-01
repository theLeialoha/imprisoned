package dev.leialoha.imprisoned.compat;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;

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
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.task.TaskHandler;
import dev.leialoha.imprisoned.task.TaskManager;
import dev.leialoha.imprisoned.task.impl.StartMiningBlockTask;
import dev.leialoha.imprisoned.utils.BukkitConversion;

class WorldGuardCompat implements Compatability {

    public final StateFlag BLOCK_MINING = new StateFlag("imprisoned-mining", true);
    public final StateFlag BLOCK_SPREAD = new StateFlag("imprisoned-spread", true);
    public final StateFlag BLOCK_TICK = new StateFlag("imprisoned-tick", true);

    public WorldGuardCompat() {}

    public void register(Server server) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();

        registry.registerAll(List.of(
            BLOCK_MINING,
            BLOCK_SPREAD,
            BLOCK_TICK
        ));

        // Register localized tasks
        TaskManager.INSTANCE.register(this);
    }

    public ApplicableRegionSet getRegion(IntLocation pos) {
        ResourceKey key = pos.world();
        NamespacedKey namespacedKey = BukkitConversion.to(key);
        org.bukkit.World bukkitWorld = Bukkit.getWorld(namespacedKey);

        World world = BukkitAdapter.adapt(bukkitWorld);
        BlockVector3 blockVector = BlockVector3.at(pos.x(), pos.y(), pos.z());

        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionContainer container = platform.getRegionContainer();
        RegionManager manager = container.get(world);

        if (manager == null) return null;
        return manager.getApplicableRegions(blockVector);
    }

    @TaskHandler
    public void onBlockMine(StartMiningBlockTask task) {
        boolean cancelled = !testState(task.pos, task.player, BLOCK_MINING);
        task.setCancelled(cancelled);
    }

    // public boolean allowInfectionSpread(IntLocation pos) {
    //     return IS_ENABLED && testState(pos, BLOCK_SPREAD);
    // }

    public boolean testState(IntLocation pos, Player player, StateFlag... flags) {
        ApplicableRegionSet set = getRegion(pos);
        if (set == null) return false;

        LocalPlayer association = WorldGuardPlugin.inst().wrapPlayer(player);
        return testState(set, association, flags);
    }

    public boolean testState(IntLocation pos, StateFlag... flags) {
        ApplicableRegionSet set = getRegion(pos);
        if (set == null) return false;

        RegionOverlapAssociation association = new RegionOverlapAssociation(set.getRegions());
        return testState(set, association, flags);
    }

    private static boolean testState(ApplicableRegionSet set, RegionAssociable association, StateFlag... flags) {
        return set.testState(association, flags);
    }

}
