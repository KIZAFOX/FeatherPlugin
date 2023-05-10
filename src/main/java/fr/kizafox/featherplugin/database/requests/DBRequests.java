package fr.kizafox.featherplugin.database.requests;

import fr.kizafox.featherplugin.FeatherPlugin;
import fr.kizafox.featherplugin.database.hikari.HikariPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public class DBRequests {

    protected final FeatherPlugin instance;

    private final HikariPool pool;
    private final DataSource source;
    private final Connection connection;
    private final Statement statement;

    public DBRequests(final FeatherPlugin instance) throws SQLException {
        this.instance = instance;

        this.pool = this.instance.getDbHandler().pool();
        this.source = pool.getDataSource();
        this.connection = source.getConnection();
        this.statement = connection.createStatement();
    }

    public void fetchUsers() throws SQLException {
        try {
            final ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while (resultSet.next()){
                final int id = resultSet.getInt("id");
                final String name = resultSet.getString("name");
                final double wallet = resultSet.getDouble("wallet");

                this.instance.getLogger().info("DBRequests{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", wallet='" + wallet + '\'' +
                        '}');
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            this.connection.close();
        }
    }
}
