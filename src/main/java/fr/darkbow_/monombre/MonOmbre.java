package fr.darkbow_.monombre;

import fr.darkbow_.monombre.traits.MonOmbreMalefique;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class MonOmbre extends JavaPlugin {
    public static BukkitTask task;
    private MonOmbre instance;
    public NPC ombre;

    public MonOmbre getInstance() {
        return this.instance;
    }

    public NPC getMonOmbre() {
        return this.ombre;
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new MonOmbreListeners(this), this);
        //MonOmbre.task = new MonOmbreTask(instance).runTaskTimer((Plugin)this.instance, 1L, 1L);
        if(getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ombre = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§cDarkBow_");
        ombre.data().set(NPC.PLAYER_SKIN_UUID_METADATA, "DarkBow_");
        ombre.spawn(new Location(Bukkit.getWorld("world"), -248, 76, 49));
        ombre.getEntity().setInvulnerable(true);

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(MonOmbreMalefique.class).withName("MonOmbre"));
        System.out.println("[MonOmbre] Votre Ombre vient de devenir MAlÉFIQUE !!");
    }

    @Override
    public void onDisable() {
        ombre.despawn();
        System.out.println("[MonOmbre] Votre Ombre n'est plus Maléfique !");
    }
}