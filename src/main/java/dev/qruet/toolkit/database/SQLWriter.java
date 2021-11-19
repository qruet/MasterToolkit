package dev.qruet.toolkit.database;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Logger;

// TODO Database Builder

/**
 * Manages I/O operations with SQL database
 *
 * @author Qruet
 * @version 1.2.0
 */
public class SQLWriter {

    private final String table, database, address, username, password;
    private final short port;

    private Connection connection;

    private Logger logger;

    public SQLWriter(final String table, final String database, final String address, final short port, final String username, final String password) {
        this.table = table;
        this.database = database;
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void enableLogs(Logger logger) {
        this.logger = logger;
    }

    public boolean init() {
        String url = "jdbc:mysql://" + address + ":" + port + "/" + database;
        long time = System.currentTimeMillis();

        try {
            connection = DriverManager.getConnection(url, username, password);
            update("CREATE TABLE IF NOT EXISTS " + table + " (id varchar(36), logs integer, PRIMARY KEY (id));");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(logger != null)
            logger.info("Connected to database " + url + " in " + (System.currentTimeMillis() - time) + "ms.");
        return true;
    }

    public void close() {
        try {
            if (connection == null || connection.isClosed())
                return;

            if(logger != null)
                logger.warning("Closing SQL connection.");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(Player player) {
        UUID id = player.getUniqueId();
        try {
            update("DELETE FROM " + table + " WHERE id='" + id + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**public void save(LoggerProfile profile) {
        UUID id = profile.getPlayer().getUniqueId();
        int penalties = profile.getPenalties();
        try {
            update("INSERT INTO " + TABLE_NAME + " (id, logs) " +
                    "VALUES('" + id + "', '" + penalties + "') ON DUPLICATE KEY UPDATE logs='" + penalties + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LoggerProfile load(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        AtomicReference<LoggerProfile> profile = new AtomicReference<>();
        query("SELECT * FROM " + TABLE_NAME + " WHERE id='" + id + "'", (set) -> {
            try {
                if (set.next()) {
                    int penalties = set.getInt("logs");
                    profile.set(LoggerProfile.create(player, penalties));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return profile.get();
    }**/

    private void update(String update) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(update);
        stmt.executeUpdate();
        stmt.close();
    }

    private void query(String command, Consumer<ResultSet> consumer) {
        try {
            PreparedStatement stmt = connection.prepareStatement(command);
            ResultSet set = stmt.executeQuery(command);
            consumer.accept(set);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
