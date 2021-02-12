package cc.oceanmc.ohealthdisplay.util;

import cc.oceanmc.ohealthdisplay.OHealthDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Cooldown {

    private int startTime;
    private int time;
    private boolean isStart = false;
    private BukkitTask bukkitTask;

    public Cooldown(int startTime) {
        this.startTime = startTime;
        this.time = startTime;
    }

    public void start() {
        if (this.isStart) {
            throw new Error("Cooldown has been started");
        } else {
            this.isStart = true;
            this.bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!(Cooldown.this.time <= 0)) {
                        Cooldown.this.time -= 1;
                    } else {
                        Cooldown.this.isStart = false;
                        this.cancel();
                    }
                }
            }.runTaskTimerAsynchronously(OHealthDisplay.getInstance(), 20L, 20L);
        }
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isEnd() {
        return this.time == 0 && this.bukkitTask.isCancelled();
    }

    public void stop() {
        if (isStart && !this.bukkitTask.isCancelled() && this.bukkitTask != null) {
            this.isStart = false;
            this.bukkitTask.cancel();
        }
    }

    public void reset() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
        this.time = startTime;
        this.bukkitTask = null;
        this.isStart = false;
    }

    public int getTime() {
        return this.time;
    }

    public int getStartTime() {
        return this.startTime;
    }

}
