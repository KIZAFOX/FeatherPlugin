package fr.kizafox.featherplugin;

import fr.kizafox.featherplugin.database.DBHandler;
import fr.kizafox.featherplugin.database.requests.DBRequests;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

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

    @Override
    public void onEnable() {
        instance = this;

        this.dbHandler = new DBHandler.Builder().addURL("test").build();
        this.getLogger().info(dbHandler.toString());

        try {
            new DBRequests(this).fetchUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        this.dbHandler.pool.closePool();
    }

    public static FeatherPlugin get() {
        return instance;
    }

    public DBHandler getDbHandler() {
        return dbHandler;
    }
}
