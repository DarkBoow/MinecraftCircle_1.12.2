package fr.darkbow_.sampleplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SamplePlugin extends JavaPlugin {

    private File savesFile;
    private FileConfiguration savesConfig;
    private BukkitTask teleportTask;

    @Override
    public void onEnable() {
        createSavesFile();
        generateWaypoints();
        Objects.requireNonNull(this.getCommand("tstart")).setExecutor(new StartCommand());
        Objects.requireNonNull(this.getCommand("tstop")).setExecutor(new StopCommand());
    }

    private void createSavesFile() {
        savesFile = new File(getDataFolder(), "saves.yml");
        if (!savesFile.exists()) {
            savesFile.getParentFile().mkdirs();
            saveResource("saves.yml", false);
        }
        savesConfig = new YamlConfiguration();
        try {
            savesConfig.load(savesFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /*private void generateWaypoints() {
        double centerX = 0.5;
        double centerY = 95.5;
        double centerZ = 0.5;

        double radius = 10.0;
        int numberOfWaypoints = 64;
        double angleIncrement = 360.0 / numberOfWaypoints;
        double heightIncrement = (108.0 - 92.0) / numberOfWaypoints;

        for (int i = 0; i < numberOfWaypoints; i++) {
            double angle = Math.toRadians(i * angleIncrement);
            double x = centerX + radius * Math.cos(angle);
            double y = 92.0 + i * heightIncrement;
            double z = centerZ + radius * Math.sin(angle);

            // Calcul du yaw pour regarder toujours vers le point (0, 0)
            double deltaYaw = centerX - x;
            double deltaZaw = centerZ - z;
            double yaw = -1 * Math.toDegrees(Math.atan2(deltaYaw, deltaZaw));

            // Calcul du pitch pour regarder toujours vers le point (centerX, centerY, centerZ)
            double deltaX = centerX - x;
            double deltaY = centerY - y;
            double deltaZ = centerZ - z;
            double distanceToCenter = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            double pitch = -1 * Math.toDegrees(Math.asin(deltaY / distanceToCenter));

            savesConfig.set("waypoints." + i + ".x", x);
            savesConfig.set("waypoints." + i + ".y", y);
            savesConfig.set("waypoints." + i + ".z", z);
            savesConfig.set("waypoints." + i + ".yaw", yaw);
            savesConfig.set("waypoints." + i + ".pitch", pitch);
        }

        try {
            savesConfig.save(savesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void generateWaypoints() {
        double centerX = 0.0;
        double centerY = 94.5;
        double centerZ = 0.0;

        int numberOfWaypoints = 64;
        double angleIncrement = 360.0 / numberOfWaypoints;
        double initialheight = 94.5;
        double finalheight = 118.0;
        double heightIncrement = (finalheight - initialheight) / numberOfWaypoints;

        double radius;
        double initialRadius = 5.0;
        double finalRadius = 25.0;
        double radiusIncrement = (finalRadius - initialRadius) / (numberOfWaypoints - 1);

        long totalTimeMs = 2 * 60 * 1000 + 26 * 1000 + 352;
        long timeIncrementMs = totalTimeMs / (numberOfWaypoints - 1);

        for (int i = 0; i < 2 * numberOfWaypoints; i++) {
            if (i < numberOfWaypoints) {
                radius = initialRadius + i * radiusIncrement;
            } else {
                radius = finalRadius - (i - numberOfWaypoints) * radiusIncrement;
            }

            double angle = Math.toRadians((i % numberOfWaypoints) * angleIncrement);
            double x = centerX + radius * Math.cos(angle);

            // Calcul de la hauteur en fonction de l'index du waypoint
            double y;
            if (i < numberOfWaypoints) {
                y = initialheight + i * heightIncrement;
            } else {
                y = finalheight - (i - numberOfWaypoints) * heightIncrement;
            }

            //y = 108.0;

            double z = centerZ + radius * Math.sin(angle);

            // Calcul du yaw pour regarder toujours vers le point (0, 0)
            double deltaYaw = centerX - x;
            double deltaZaw = centerZ - z;
            double yaw = -1 * Math.toDegrees(Math.atan2(deltaYaw, deltaZaw));

            // Calcul du pitch pour regarder toujours vers le point (0, 0, centerY)
            double deltaX = centerX - x;
            double deltaY = centerY - y;
            double deltaZ = centerZ - z;
            double distanceToCenter = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            double pitch = -1 * Math.toDegrees(Math.asin(deltaY / distanceToCenter));

            long currentTimeMs = i * timeIncrementMs;
            int minutes = (int) (currentTimeMs / (60 * 1000));
            int seconds = (int) ((currentTimeMs % (60 * 1000)) / 1000);
            int milliseconds = (int) (currentTimeMs % 1000);
            String timeString = String.format("%dmin %dsec %dms", minutes, seconds, milliseconds);

            savesConfig.set("waypoints." + i + ".time", timeString);
            savesConfig.set("waypoints." + i + ".x", x);
            savesConfig.set("waypoints." + i + ".y", y);
            savesConfig.set("waypoints." + i + ".z", z);
            savesConfig.set("waypoints." + i + ".yaw", yaw);
            savesConfig.set("waypoints." + i + ".pitch", pitch);
        }

        try {
            savesConfig.save(savesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class StartCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                if (teleportTask == null || teleportTask.isCancelled()) {
                    ((Player) sender).setAllowFlight(true);
                    ((Player) sender).setFlying(true);
                    AtomicInteger currentWaypoint = new AtomicInteger();
                    int numberOfWaypoints = Objects.requireNonNull(savesConfig.getConfigurationSection("waypoints")).getKeys(false).size();
                    teleportTask = Bukkit.getScheduler().runTaskTimer(SamplePlugin.this, () -> {
                        double x = savesConfig.getDouble("waypoints." + currentWaypoint + ".x");
                        double y = savesConfig.getDouble("waypoints." + currentWaypoint + ".y");
                        double z = savesConfig.getDouble("waypoints." + currentWaypoint + ".z");
                        float yaw = (float) savesConfig.getDouble("waypoints." + currentWaypoint + ".yaw");
                        float pitch = (float) savesConfig.getDouble("waypoints." + currentWaypoint + ".pitch");
                        Location location = new Location(((Player) sender).getWorld(), x, y, z, yaw, pitch);
                        ((Player) sender).teleport(location);

                        if(currentWaypoint.get() + 1 >= numberOfWaypoints){
                            currentWaypoint.set(0);
                        } else {
                            currentWaypoint.set(currentWaypoint.intValue() + 1);
                        }
                    }, 0, 1);
                    sender.sendMessage("Timelapse started.");
                } else {
                    sender.sendMessage("Timelapse already running.");
                }
            }
            return true;
        }
    }

    public class StopCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (teleportTask != null && !teleportTask.isCancelled()) {
                teleportTask.cancel();
                sender.sendMessage("Timelapse stopped.");
                if(sender instanceof Player){
                    ((Player) sender).teleport(new Location(Bukkit.getWorlds().get(0), 0, -60.5, 0));
                    ((Player) sender).setAllowFlight(false);
                    ((Player) sender).setFlying(false);
                }
            } else {
                sender.sendMessage("Timelapse is not running.");
            }
            return true;
        }
    }
}