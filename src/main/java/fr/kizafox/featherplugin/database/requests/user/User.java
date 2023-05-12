package fr.kizafox.featherplugin.database.requests.user;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Change this line to a short description of the class
 *
 * @author : KIZAFOX
 * @date : 11/05/2023
 * @project : FeatherPlugin
 */
public abstract class User {

    abstract public void initialize();

    abstract public void delete();

    abstract public int getCoins();

    abstract public void setCoins(final int amount);

    abstract public void addCoins(final int amount);

    abstract public void removeCoins(final int amount);

}
