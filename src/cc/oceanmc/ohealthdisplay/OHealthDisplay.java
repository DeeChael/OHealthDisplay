package cc.oceanmc.ohealthdisplay;

import cc.oceanmc.ohealthdisplay.util.Cooldown;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class OHealthDisplay extends JavaPlugin {

    private static OHealthDisplay instance;
    private static BukkitTask task;

    private static Map<Entity, BossBar> bars;
    private static Multimap<Player, Entity> entities;
    private static Map<Entity, Cooldown> cds;

    private static List<Cooldown> waitForStart;

    @Override
    public void onEnable() {
        instance = this;
        bars = new HashMap<>();
        entities = LinkedHashMultimap.create();
        cds = new HashMap<>();
        waitForStart = new ArrayList<>();
        this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (entities.size() > 0) {
                    for (Player player : entities.keySet()) {
                        if (entities.get(player).size() > 0) {
                            List<Entity> entities = new ArrayList<>();
                            entities.addAll(OHealthDisplay.entities.get(player));
                            for (Entity entity : entities) {
                                if (cds.get(entity).isEnd()) {
                                    bars.remove(entity).removePlayer(player);
                                    cds.remove(entity).stop();
                                    OHealthDisplay.entities.get(player).remove(entity);
                                }
                            }
                        }
                    }
                }
                if (waitForStart.size() > 0) {
                    for (Cooldown cd : waitForStart) {
                        if (!cd.isStart()) {
                            cd.start();
                        }
                    }
                }
                waitForStart.clear();
            }
        }.runTaskTimerAsynchronously(this, 20L, 20L);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bars.size() > 0) {
                    List<Entity> entities = new ArrayList<>();
                    entities.addAll(bars.keySet());
                    for (Entity entity : entities) {
                        if (!(entity instanceof LivingEntity) || entity.isDead()) return;
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (bars.containsKey(entity)) {
                            if (bars.get(entity) != null)
                            bars.get(entity).setProgress(livingEntity.getHealth() / livingEntity.getMaxHealth());
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 1L, 1L);
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

    public static List<Cooldown> getWaitForStart() {
        return waitForStart;
    }
}
