package fr.kizafox.featherplugin.database;

import fr.kizafox.featherplugin.database.hikari.HikariPool;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 10/05/2023
 * @project : FeatherPlugin
 */
public abstract class Database {

    abstract public String username();

    abstract public String password();

    abstract public String host();

    abstract public int port();

    abstract public String database();

    abstract public HikariPool pool();

}
