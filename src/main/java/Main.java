import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class Main extends JavaPlugin implements CommandExecutor, Listener {

    public static HashSet<String> hider = new HashSet<>();



    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (hider.contains(p.getName())){
                    for (Player _p : Bukkit.getOnlinePlayers()){
                        if (_p.equals(p)) { continue; }

                        _p.hidePlayer(this, p);
                    }
                }
            });
        }, 1, 20);
    }

    @Override
    public void onDisable(){
        for (String s : hider){
            Player player = Bukkit.getPlayer(s);
            if (player != null){
                for (Player _p : Bukkit.getOnlinePlayers()){
                    if (_p.equals(player)) { continue; }

                    _p.showPlayer(this, player);
                }
            }
        }

        hider.clear();
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String... args) {
        if (sender instanceof Player player) {
            if (cmd.getName().equalsIgnoreCase("hide")) {
                Player target;
                if (args.length == 0) { target = player; }
                else{
                    target = Bukkit.getPlayer(args[0]);
                    if (target == null){
                        player.sendMessage("그런 플레이어는 없습니다");
                        return false;
                    }
                }

                hider.add(target.getName());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(target)) {
                        continue;
                    }

                    p.hidePlayer(this, target);
                }

                player.sendMessage("이제 " + target.getName() + "은(는) 보이지 않습니다");
            }
            else if (cmd.getName().equalsIgnoreCase("show")) {
                Player target;
                if (args.length == 0) { target = player; }
                else{
                    target = Bukkit.getPlayer(args[0]);
                    if (target == null){
                        player.sendMessage("그런 플레이어는 없습니다");
                        return false;
                    }
                }

                hider.remove(target.getName());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.equals(target)) {
                        continue;
                    }

                    p.showPlayer(this, target);
                }

                player.sendMessage("다시 " + target.getName() + "을(를) 보이게 합니다");
            }
        }

        return true;
    }



    @EventHandler
    public void onPickup(EntityPickupItemEvent e){
        if (e.getEntity() instanceof Player player){
            if (hider.contains(player.getName())){
                e.setCancelled(true);

                player.getInventory().addItem(e.getItem().getItemStack());
                e.getItem().remove();
                e.getItem().getWorld().playSound(e.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
            }
        }
    }
}
