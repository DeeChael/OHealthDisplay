package cc.oceanmc.ohealthdisplay;

import cc.oceanmc.ohealthdisplay.util.Cooldown;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public final class EntityListener implements Listener {

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER) return;
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) return;
        if (e.getEntity().getType() == EntityType.WITHER) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;;
        Player player = (Player) e.getDamager();
        LivingEntity livingEntity = (LivingEntity) e.getEntity();
        double pct = livingEntity.getHealth() / livingEntity.getMaxHealth();
        if (OHealthDisplay.getEntities().containsKey(player) && isContainsValue(player, livingEntity)) {
            BossBar bossBar = OHealthDisplay.getBars().get(livingEntity);
            bossBar.setProgress(pct);
        } else {
            BossBar bossBar = (livingEntity.getCustomName() == null) ? Bukkit.createBossBar(livingEntity.getName(), BarColor.PINK, BarStyle.SOLID) : Bukkit.createBossBar(livingEntity.getCustomName(), BarColor.PINK, BarStyle.SOLID);
            bossBar.setProgress(pct);
            bossBar.addPlayer(player);
            OHealthDisplay.getEntities().put(player, livingEntity);
            OHealthDisplay.getBars().put(livingEntity, bossBar);
            Cooldown cd = new Cooldown(5);
            cd.start();
            OHealthDisplay.getCds().put(livingEntity, cd);
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent e) {
        if (OHealthDisplay.getEntities().size() > 0) {
            if (OHealthDisplay.getEntities().containsValue(e.getEntity()) && e.getEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) e.getEntity();
                OHealthDisplay.getBars().get(e.getEntity()).setProgress(livingEntity.getHealth() / livingEntity.getMaxHealth());
            }
        }
    }

    @EventHandler
    public void onEntityDead(EntityDeathEvent e) {
        if (OHealthDisplay.getEntities().size() > 0) {
            if (OHealthDisplay.getEntities().containsValue(e.getEntity())) {
                for (Player player : OHealthDisplay.getEntities().keySet()) {
                    for (Entity entity : OHealthDisplay.getEntities().get(player)) {
                        if (e.getEntity() == entity) {
                            OHealthDisplay.getEntities().get(player).remove(entity);
                            OHealthDisplay.getBars().remove(entity).removePlayer(player);
                            OHealthDisplay.getCds().remove(entity).stop();
                        }
                    }
                }
            }
        }
    }

    public boolean isContainsValue(Player player, Entity entity) {
        if (OHealthDisplay.getEntities().size() > 0) {
            if (OHealthDisplay.getEntities().containsKey(player)) {
                for (Entity e : OHealthDisplay.getEntities().get(player)) {
                    if (e.equals(entity)) return true;
                }
            }
        }
        return false;
    }

}
