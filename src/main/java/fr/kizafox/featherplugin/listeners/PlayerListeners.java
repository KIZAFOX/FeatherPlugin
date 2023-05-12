package fr.kizafox.featherplugin.listeners;

import fr.kizafox.featherplugin.FeatherPlugin;
import fr.kizafox.featherplugin.database.requests.user.UserAccount;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Random;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public class PlayerListeners implements Listener {

    protected final FeatherPlugin instance;

    public PlayerListeners(final FeatherPlugin instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final UserAccount userAccount = new UserAccount(this.instance, player);

        userAccount.initialize();
        player.sendMessage(ChatColor.YELLOW + "Hello, " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " you have " + ChatColor.AQUA + userAccount.getCoins() + ChatColor.YELLOW + "€ in your balance!");
    }

    @EventHandler
    public void onLogout(final PlayerQuitEvent event){
        final Player player = event.getPlayer();
        UserAccount.getUserAccount(player).delete();
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event){
        final Player player = event.getPlayer();
        final UserAccount userAccount = UserAccount.getUserAccount(player);

        userAccount.addCoins(new Random().nextInt(10));
        player.sendMessage(ChatColor.DARK_GRAY + "[SQL-Query@Listener] " + ChatColor.BLUE + "Coins column updated to " + ChatColor.AQUA + userAccount.getCoins() + ChatColor.BLUE + " !");
    }
}
