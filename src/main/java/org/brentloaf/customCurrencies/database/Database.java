package org.brentloaf.customCurrencies.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.brentloaf.customCurrencies.services.account.Account;
import org.brentloaf.customCurrencies.services.bank.Bank;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class Database {

    private static Connection connection;
    private static final Gson gson = new Gson();

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            AccountQuery.init(statement);
            BankQuery.init(statement);
        }
    }

    public static class AccountQuery {

        private static final String TABLE_NAME = "accountTable";

        private static void init(Statement statement) throws SQLException {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS accountTable (
                    ownerId TEXT NOT NULL,
                    balances TEXT NOT NULL,
                    PRIMARY KEY (ownerId)
                )
            """);
        }

        public static @NotNull Account get(UUID ownerId) {
            String sql = "SELECT balances FROM " + TABLE_NAME + " WHERE ownerId = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ownerId.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return new Account(ownerId, fromJson(result.getString()));
                    } else {
                        add(ownerId);
                        return new Account(ownerId, new HashMap<>());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void add(UUID ownerId) {
            String sql = "INSERT OR IGNORE INTO " + TABLE_NAME + " (ownerId, balances) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ownerId.toString());
                statement.setString(2, "{}");

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static HashMap<UUID, Integer> getBalance(UUID ownerId) {
            String sql = "SELECT balances FROM " + TABLE_NAME + " WHERE ownerId = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ownerId.toString());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return fromJson(result.getString(1));
                    } else {
                        add(ownerId);
                        return new HashMap<>();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static void setBalance(UUID ownerId, HashMap<UUID, Integer> balances) {
            String sql = "UPDATE " + TABLE_NAME + " SET balances = ? WHERE ownerId = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, toJson(balances));
                statement.setString(2, ownerId.toString());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private static HashMap<UUID, Integer> fromJson(String json) {
            if (json == null || json.isBlank()) return new HashMap<>();
            Type type = new TypeToken<HashMap<UUID, Integer>>(){}.getType();

            return new HashMap<>(gson.fromJson(json, type));
        }

        public static String toJson(HashMap<UUID, Integer> balances) {
            if (balances == null || balances.isEmpty()) return "{}";
            return gson.toJson(balances);
        }
    }

    public static class BankQuery {

        private static final String TABLE_NAME = "bankTable";

        private static void init(Statement statement) throws SQLException {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS bankTable (
                    id TEXT NOT NULL,
                    bankName TEXT NOT NULL,
                    ownerId TEXT NOT NULL,
                    currenciesIds TEXT NOT NULL,
                    PRIMARY KEY (id)
                )
            """);
        }

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
            String updatedJson = gson.toJson(currencies);

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

        public static @Nullable Bank getFromId(UUID bankId) {
            String sql = "SELECT bankName, ownerId, id, currenciesIds FROM " + TABLE_NAME + " WHERE id = ?";

            Bank bank = null;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, bankId.toString());

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

        private static List<UUID> fromJson(String json) {
            if (json == null || json.isBlank()) return new ArrayList<>();
            Type type = new TypeToken<List<UUID>>(){}.getType();

            return new ArrayList<>(gson.fromJson(json, type));
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}
