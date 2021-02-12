package cc.oceanmc.ohealthdisplay;

import cc.oceanmc.ohealthdisplay.util.Cooldown;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public final class OHealthDisplay extends JavaPlugin {

    private static OHealthDisplay instance;
    private static BukkitTask task;

    private static Map<Entity, BossBar> bars;
    private static Multimap<Player, Entity> entities;
    private static Map<Entity, Cooldown> cds;

    @Override
    public void onEnable() {
        instance = this;
        bars = new HashMap<>();
        entities = LinkedHashMultimap.create();
        cds = new HashMap<>();
        this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (entities.size() > 0) {
                    for (Player player : entities.keySet()) {
                        for (Entity entity : entities.get(player)) {
                            if (cds.containsKey(entity)) {
                                if (cds.get(entity).isEnd()) {
                                    bars.get(entity).removePlayer(player);
                                    bars.remove(entity);
                                    cds.remove(entity);
                                    entities.get(player).remove(entity);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);
    }

    public static OHealthDisplay getInstance() {
        return instance;
    }

    public static Map<Entity, BossBar> getBars() {
        return bars;
    }

    public static Map<Entity, Cooldown> getCds() {
        return cds;
    }

    public static Multimap<Player, Entity> getEntities() {
        return entities;
    }

}
