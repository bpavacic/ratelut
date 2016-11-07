package com.ratelut.apiserver.storage;

import com.google.common.base.Joiner;
import com.ratelut.apiserver.common.CurrencyPair;
import com.ratelut.apiserver.common.ExchangeRate;
import com.ratelut.apiserver.common.ExchangeRateProvider;
import com.ratelut.apiserver.common.Interval;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link Storage} using SQL database as a backend.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Singleton
public class SqlStorage implements Storage {
    private static final java.lang.String CREATE_TABLE_SQL = String.format(
            "CREATE TABLE IF NOT EXISTS rates (" +
                    "timestamp DATETIME NOT NULL," +
                    "provider VARCHAR(20) NOT NULL," +
                    "currency_pair VARCHAR(6) NOT NULL," +
                    "rate DECIMAL(10, 10) NOT NULL," +
                    "PRIMARY KEY(timestamp, provider, currency_pair))");
    private final Connection connection;

    @Inject
    public SqlStorage(SqlStorageConfig storageConfig) throws SQLException {
        String jdbcUrl = storageConfig.getJdbcUrl();
        System.out.println("Using JDBC URL " + jdbcUrl);
        this.connection = DriverManager.getConnection(jdbcUrl);
        createTables();
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(CREATE_TABLE_SQL);
        statement.close();
    }

    @Override
    public void saveExchangeRate(ExchangeRate exchangeRate) throws StorageException {
        try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT into rates (timestamp, provider, currency_pair, rate) " +
                            "VALUES (?, ?, ?, ?)")) {
            insertStatement.setTimestamp(1, Timestamp.from(exchangeRate.getTimestamp()));
            insertStatement.setString(2, exchangeRate.getProvider().name());
            insertStatement.setString(3, exchangeRate.getCurrencyPair().toString());
            insertStatement.setBigDecimal(4, exchangeRate.getExchangeRate());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Iterable<ExchangeRate> getExchangeRates(Interval timeInterval,
            Optional<ExchangeRateProvider> exchangeRateProvider,
            Optional<CurrencyPair> currencyPair) throws StorageException {
        List<String> whereParts = new ArrayList<>();
        whereParts.add("timestamp >= ?");
        whereParts.add("timestamp < ?");
        if (exchangeRateProvider.isPresent()) {
            whereParts.add("provider = ?");
        }
        if (currencyPair.isPresent()) {
            whereParts.add("currency_pair = ?");
        }
        String whereClause = Joiner.on(" AND ").join(whereParts);
        try {
            try (PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT timestamp, provider, currency_pair, rate FROM rates WHERE "
                            + whereClause + " ORDER BY timestamp, provider, currency_pair")) {
                int fieldIndex = 1;
                selectStatement.setTimestamp(fieldIndex++, Timestamp.from(timeInterval.getStart()));
                selectStatement.setTimestamp(fieldIndex++, Timestamp.from(timeInterval.getEnd()));
                if (exchangeRateProvider.isPresent()) {
                    selectStatement.setString(fieldIndex++, exchangeRateProvider.get().name());
                }
                if (currencyPair.isPresent()) {
                    selectStatement.setString(fieldIndex++, currencyPair.get().toString());
                }
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    List<ExchangeRate> resultList = new ArrayList<>();
                    while (resultSet.next()) {
                        resultList.add(ExchangeRate.of(
                                ExchangeRateProvider.valueOf(resultSet.getString(2)),
                                CurrencyPair.from(resultSet.getString(3)),
                                resultSet.getTimestamp(1).toInstant(),
                                resultSet.getBigDecimal(4)));
                    }
                    return resultList;
                }
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }
}
