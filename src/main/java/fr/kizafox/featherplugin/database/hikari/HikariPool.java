package fr.kizafox.featherplugin.database.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public class HikariPool {

    private final DataSource dataSource;
    private final HikariConfig config;

    public HikariPool(final HikariConfig config){
        this.config = config;
        this.dataSource = this.getHikariPool();
    }

    private DataSource getHikariPool() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());

        hikariConfig.setMaximumPoolSize(10); // default 4
        hikariConfig.setMaxLifetime(600000L); //10min
        hikariConfig.setIdleTimeout(300000L); //5min
        hikariConfig.setLeakDetectionThreshold(300000L); //5min
        hikariConfig.setConnectionTimeout(10000L); //10sec

        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        return new HikariDataSource(hikariConfig);
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void closePool() {
        ((HikariDataSource) this.dataSource).close();
    }
}
