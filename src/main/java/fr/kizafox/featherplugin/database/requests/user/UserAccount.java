package fr.kizafox.featherplugin.database.requests.user;

import fr.kizafox.featherplugin.FeatherPlugin;
import fr.kizafox.featherplugin.database.requests.DBQuery;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 11/05/2023
 * @project : FeatherPlugin
 */
public class UserAccount extends User{

    protected final FeatherPlugin instance;

    private final Player player;
    private final String uuid;

    private final String TABLE = "users_info";

    public UserAccount(final FeatherPlugin instance, final Player player) {
        this.instance = instance;

        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public static UserAccount getUserAccount(final Player player){
        return FeatherPlugin.get().getUserAccounts().stream().filter(userAccount -> userAccount.player == player).findFirst().orElse(null);
    }

    public static void fetchUsers() {
        new DBQuery(FeatherPlugin.get().getDbHandler().pool().getDataSource()).query(((resultSet, throwables) -> {
            if(throwables != null){
                FeatherPlugin.sendLog(ChatColor.RED + "An error occurred: " + ChatColor.BLUE + throwables.getMessage());
                return;
            }

            try {
                while(resultSet.next()){
                    final int id = resultSet.getInt("#");
                    final String uuid = resultSet.getString("uuid");
                    final String name = resultSet.getString("name");
                    final int coins = resultSet.getInt("coins");

                    FeatherPlugin.sendLog(ChatColor.RED + "UserAccount{" +
                            "id='" + id + '\'' +
                            ", uuid='" + uuid + '\'' +
                            ", name='" + name + '\'' +
                            ", coins='" + coins + '\'' +
                            '}');
                }
            }catch (final SQLException e){
                FeatherPlugin.sendLog(ChatColor.RED + "An error occurred while processing the result set: " + e.getMessage());
            }
        }), "SELECT * FROM users_info");
    }

    @Override
    public void initialize() {
        this.instance.getUserAccounts().add(this);

        new DBQuery(this.instance.getDbHandler().pool().getDataSource()).query(((resultSet, throwables) -> {
            if(throwables != null){
                FeatherPlugin.sendLog(ChatColor.RED + "An error occurred: " + throwables.getMessage());
                return;
            }

            try {
                if(!resultSet.next()){
                    new DBQuery(this.instance.getDbHandler().pool().getDataSource()).update("INSERT INTO " + TABLE + " (uuid, name, coins) VALUES ('" + this.uuid + "', '" + player.getName() + "', '0')");
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Your account has been created successfully!");
                }else{
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "Your account has been loaded successfully!");
                }
            }catch (final SQLException e){
                FeatherPlugin.sendLog(ChatColor.RED + "An error occurred while processing the result set: " + e.getMessage());
            }
        }), "SELECT * FROM " + TABLE + " WHERE uuid=?", uuid);
    }

    @Override
    public void delete() {
        this.instance.getUserAccounts().remove(this);
    }

    @Override
    public int getCoins() {
        return (int) new DBQuery(this.instance.getDbHandler().pool().getDataSource()).query((resultSet -> {
            try {
                if(resultSet.next()){
                    return resultSet.getInt("coins");
                }
            } catch (SQLException e) {
                FeatherPlugin.sendLog(ChatColor.RED + "An error occurred while processing the result set: " + e.getMessage());
            }
            return 0;
        }), "SELECT * FROM " + TABLE + " WHERE uuid='" + uuid + "'");
    }

    @Override
    public void setCoins(int amount) {
        new DBQuery(this.instance.getDbHandler().pool().getDataSource()).update("UPDATE " + TABLE + " SET coins='" + amount + "' WHERE uuid='" + uuid + "'");
    }

    @Override
    public void addCoins(int amount) {
        this.setCoins(this.getCoins() + amount);
    }

    @Override
    public void removeCoins(int amount) {
        this.setCoins(this.getCoins() < amount ? 0 : this.getCoins() - amount);
    }
}
