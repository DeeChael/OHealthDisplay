package cc.oceanmc.ohealthdisplay;

import cc.oceanmc.ohealthdisplay.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public final class EntityListener implements Listener {

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        Player player = null;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile projectile = (Projectile) e.getDamager();
            if (!(projectile.getShooter() instanceof Player)) return;
            player = (Player) projectile.getShooter();
        } else {
            if (e.getDamager().getType() != EntityType.PLAYER) return;
            player = (Player) e.getDamager();
        }
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) return;
        if (e.getEntity().getType() == EntityType.WITHER) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) e.getEntity();
        if (isContainsValue(player, livingEntity)) {
            OHealthDisplay.getCds().get(livingEntity).reset();
            OHealthDisplay.getWaitForStart().add(OHealthDisplay.getCds().get(livingEntity));
        } else {
            BossBar bossBar = (livingEntity.getCustomName() == null) ? Bukkit.createBossBar(livingEntity.getName(), BarColor.PINK, BarStyle.SOLID) : Bukkit.createBossBar(livingEntity.getCustomName(), BarColor.PINK, BarStyle.SOLID);
            bossBar.addPlayer(player);
            OHealthDisplay.getEntities().put(player, livingEntity);
            OHealthDisplay.getBars().put(livingEntity, bossBar);
            Cooldown cd = new Cooldown(5);
            OHealthDisplay.getWaitForStart().add(cd);
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
                    if (OHealthDisplay.getEntities().get(player).size() > 0) {
                        if (OHealthDisplay.getEntities().get(player).contains(e.getEntity())) {
                            OHealthDisplay.getEntities().get(player).remove(e.getEntity());
                            OHealthDisplay.getBars().remove(e.getEntity()).removePlayer(player);
                            OHealthDisplay.getCds().remove(e.getEntity()).stop();
                        }
                    }
                }
            }
        }
    }

    public boolean isContainsValue(Player player, Entity entity) {
        if (OHealthDisplay.getEntities().size() > 0) {
            if (OHealthDisplay.getEntities().containsKey(player)) {
                return OHealthDisplay.getEntities().get(player).contains(entity);
            }
        }
        return false;
    }

}
