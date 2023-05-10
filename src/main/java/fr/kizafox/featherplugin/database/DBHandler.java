package fr.kizafox.featherplugin.database;

import com.zaxxer.hikari.HikariConfig;
import fr.kizafox.featherplugin.database.hikari.HikariPool;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public class DBHandler extends Database {

    private String username, password, host;
    private int port;
    private String database;

    public HikariPool pool;

    public static class Builder{
        private String username = "root", password, host = "localhost";
        private int port = 3306;
        private String database = "Undefined";

        public Builder addUsername(final String username){
            this.username = username;
            return this;
        }

        public Builder addPassword(final String password){
            this.password = password;
            return this;
        }

        public Builder addURL(final String host, final int port, final String database){
            this.host = host;
            this.port = port;
            this.database = database;
            return this;
        }

        public Builder addURL(final String host, final String database){
            this.addURL(host, this.port, database);
            return this;
        }

        public Builder addURL(final int port, final String database){
            this.addURL(this.host, port, database);
            return this;
        }

        public Builder addURL(final String database){
            this.addURL(this.host, this.port, database);
            return this;
        }

        public DBHandler build() {
            final DBHandler data = new DBHandler();
            data.username = username;
            data.password = password;
            data.host = host;
            data.port = port;
            data.database = database;

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
            config.setUsername(this.username);
            config.setPassword(this.password);
            config.setDriverClassName("com.mysql.jdbc.Driver");

            data.pool = new HikariPool(config);

            return data;
        }
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public String host() {
        return this.host;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public String database() {
        return this.database;
    }

    @Override
    public HikariPool pool() {
        return this.pool;
    }

    @Override
    public String toString() {
        return "DBHandler{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                '}';
    }
}