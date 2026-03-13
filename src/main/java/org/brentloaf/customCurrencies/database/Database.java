package org.brentloaf.customCurrencies.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.brentloaf.customCurrencies.services.bank.Bank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    private static Connection connection;

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS bankTable (
                    id TEXT NOT NULL,
                    bankName TEXT NOT NULL,
                    ownerId TEXT NOT NULL,
                    currenciesIds TEXT NOT NULL,
                    PRIMARY KEY (uuid, bankName, ownerUuid)
                )
            """);
        }
    }

    public static class BankQuery {

        public static final String TABLE_NAME = "bankTable";

        public static void add(UUID ownerId, String name) {
            String sql = "INSERT INTO " + TABLE_NAME + " (id, bankName, ownerId, currenciesIds) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, name.toLowerCase().replace(" ", "_"));
                statement.setString(3, ownerId.toString());
                statement.setString(4, "[]");

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void addCurrency(UUID id, UUID currencyId) {
            String sqlSelect = "SELECT currenciesIds FROM " + TABLE_NAME + " WHERE id = ?";
            List<UUID> currencies = new ArrayList<>();

            try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
                statement.setString(1, id.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        currencies = fromJson(result.getString("currenciesIds"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            currencies.add(currencyId);
            String updatedJson = new Gson().toJson(currencies);

            String sqlUpdate = "UPDATE " + TABLE_NAME + " SET currenciesIds = ? WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
                statement.setString(1, updatedJson);
                statement.setString(2, id.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static @Nullable Bank getFromPlayer(Player player) {
            UUID playerId = player.getUniqueId();
            String sql = "SELECT bankName, ownerId, id, currenciesIds FROM " + TABLE_NAME + " WHERE ownerId = ?";

            Bank bank = null;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        String name = result.getString(1);
                        UUID ownerId = UUID.fromString(result.getString(2));
                        UUID id = UUID.fromString(result.getString(3));
                        List<UUID> currencyIds = fromJson(result.getString(4));

                        bank = new Bank(name, ownerId, id, currencyIds);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return bank;
        }

        public static @Nullable Bank getFromName(String bankName) {
            String rawBankName = bankName.toLowerCase().replace(" ",  "_");
            String sql = "SELECT bankName, ownerId, id, currenciesIds FROM " + TABLE_NAME + " WHERE bankName = ?";

            Bank bank = null;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, rawBankName);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        String name = result.getString(1);
                        UUID ownerId = UUID.fromString(result.getString(2));
                        UUID id = UUID.fromString(result.getString(3));
                        List<UUID> currencyIds = fromJson(result.getString(4));

                        bank = new Bank(name, ownerId, id, currencyIds);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return bank;
        }

        private static final Gson gson = new Gson();

        private static List<UUID> fromJson(String json) {
            if (json == null || json.isBlank()) return new ArrayList<>();
            Type type = new TypeToken<List<String>>(){}.getType();

            return new ArrayList<>(gson.fromJson(json, type));
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}
