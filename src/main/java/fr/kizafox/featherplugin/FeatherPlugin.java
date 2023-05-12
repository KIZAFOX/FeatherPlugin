package fr.kizafox.featherplugin;

import fr.kizafox.featherplugin.database.DBHandler;
import fr.kizafox.featherplugin.database.requests.DBQuery;
import fr.kizafox.featherplugin.database.requests.user.User;
import fr.kizafox.featherplugin.database.requests.user.UserAccount;
import fr.kizafox.featherplugin.listeners.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main class of the project.
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public final class FeatherPlugin extends JavaPlugin {

    private static FeatherPlugin instance;

    private DBHandler dbHandler;

    private List<UserAccount> userAccounts;

    @Override
    public void onEnable() {
        instance = this;

        this.dbHandler = new DBHandler.Builder().addURL("test").build();
        sendLog(ChatColor.DARK_PURPLE + dbHandler.toString());

        this.userAccounts = new ArrayList<>();

        new DBQuery(this.getDbHandler().pool().getDataSource()).initializeDB();

        UserAccount.fetchUsers();

        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
    }

    @Override
    public void onDisable() {
        this.dbHandler.pool.closePool();
    }

    public static void sendLog(final String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static FeatherPlugin get() {
        return instance;
    }

    public DBHandler getDbHandler() {
        return dbHandler;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }
}
