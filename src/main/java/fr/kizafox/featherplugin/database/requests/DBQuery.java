package fr.kizafox.featherplugin.database.requests;

import fr.kizafox.featherplugin.FeatherPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public class DBQuery {

    private final DataSource source;
    private final ExecutorService executorService;

    private final List<Connection> connections;
    private final List<PreparedStatement> statements;

    public DBQuery(final DataSource source){
        this.source = source;
        this.executorService = Executors.newFixedThreadPool(10);

        this.connections = new ArrayList<>();
        this.statements = new ArrayList<>();
    }

    public void initializeDB(){
        this.update("CREATE TABLE IF NOT EXISTS users_info (" +
                "`#` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "uuid VARCHAR(255), " +
                "name VARCHAR(255), " +
                "coins BIGINT)");
    }

    public void update(final String query){
        try (Connection connection = this.source.getConnection()){
            final PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void query(final BiConsumer<ResultSet, SQLException> resultConsumer, final String query, final String... params){
        this.executorService.submit(() -> {
            FeatherPlugin.sendLog(ChatColor.DARK_GRAY + "============================================================================================");
            FeatherPlugin.sendLog(ChatColor.RED + "An SQL query has been requested... wait a moment please.");

            long start = System.currentTimeMillis();

            try (final Connection connection = this.source.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(query)){

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    resultConsumer.accept(resultSet, null);
                }

                this.connections.add(connection);
                this.statements.add(statement);

                Bukkit.getOnlinePlayers().forEach(players -> {
                    players.sendMessage(ChatColor.DARK_GRAY + "==========================================");
                    players.sendMessage(" ");
                    players.sendMessage(ChatColor.DARK_GRAY + "[SQL-Query@Default] " + ChatColor.GRAY + "An SQL query has been requested... wait a moment please.");
                    players.sendMessage("");
                    players.sendMessage(ChatColor.DARK_GREEN + "[✔]" + ChatColor.GREEN + " The request has just been completed in " + ChatColor.DARK_GREEN + ChatColor.BOLD + (System.currentTimeMillis() - start) + "ms" + ChatColor.RESET + ChatColor.GREEN + " and has not encountered any problems!");
                    players.sendMessage(" ");
                    players.sendMessage(ChatColor.DARK_GRAY + "==========================================");
                });

                FeatherPlugin.sendLog(ChatColor.GREEN + "The request has just been completed in " + ChatColor.DARK_GREEN + (System.currentTimeMillis() - start) + "ms" + ChatColor.GREEN + " and has not encountered any problems!");
            }catch (final SQLException e){
                resultConsumer.accept(null, e);
            }finally {
                if(this.connections.get(0) != null){
                    try {
                        this.connections.get(0).close();
                        this.statements.get(0).close();
                        this.connections.remove(0);
                        this.statements.remove(0);

                        executorService.shutdown();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                FeatherPlugin.sendLog(ChatColor.DARK_GRAY + "============================================================================================");
            }
        });
    }

    public Object query(Function<ResultSet, Object> consumer, String query){
        FeatherPlugin.sendLog(ChatColor.DARK_GRAY + "============================================================================================");
        FeatherPlugin.sendLog(ChatColor.RED + "An SQL query (object) has been requested... wait a moment please.");

        long start = System.currentTimeMillis();

        try (final Connection connection = this.source.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query);
             final ResultSet resultSet = statement.executeQuery()){

            final Future<Object> resultFuture = executorService.submit(() -> consumer.apply(resultSet));

            Bukkit.getOnlinePlayers().forEach(players -> {
                players.sendMessage(ChatColor.DARK_GRAY + "==========================================");
                players.sendMessage(" ");
                players.sendMessage(ChatColor.DARK_GRAY + "[SQL-Query@Object] " + ChatColor.GRAY + "An SQL query has been requested... wait a moment please.");
                players.sendMessage("");
                players.sendMessage(ChatColor.DARK_GREEN + "[✔]" + ChatColor.GREEN + " The request has just been completed in " + ChatColor.DARK_GREEN + ChatColor.BOLD + (System.currentTimeMillis() - start) + "ms" + ChatColor.RESET + ChatColor.GREEN + " and has not encountered any problems!");
                players.sendMessage(" ");
                players.sendMessage(ChatColor.DARK_GRAY + "==========================================");
            });
            FeatherPlugin.sendLog(ChatColor.GREEN + "The request has just been completed in " + ChatColor.DARK_GREEN + (System.currentTimeMillis() - start) + "ms " + ChatColor.GREEN + " and has not encountered any problems!");


            return resultFuture.get();
        }catch (final SQLException | InterruptedException | ExecutionException e){
            throw new IllegalStateException(e.getMessage());
        }finally {
            executorService.shutdown();
            FeatherPlugin.sendLog(ChatColor.DARK_GRAY + "============================================================================================");
        }
    }
}
