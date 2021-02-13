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
        this.isStart = false;
        if (this.bukkitTask != null)
            this.bukkitTask.cancel();
    }

    public void reset() {
        stop();
        this.time = startTime;
        this.bukkitTask = null;
    }

    public void removeOneSecond() {
        this.time -= 1;
    }

    public int getTime() {
        return this.time;
    }

    public int getStartTime() {
        return this.startTime;
    }

}
