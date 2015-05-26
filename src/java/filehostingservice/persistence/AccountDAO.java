package filehostingservice.persistence;

import filehostingservice.entities.Account;
import filehostingservice.utils.SecurityUtil;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zetro on 20.05.2015.
 */
public class AccountDAO {

    private static String deleteAccountQuery = "DELETE FROM accounts WHERE login = ?";
    private static String saveAccountChangesQuery = "UPDATE accounts SET login = ?, password = ? WHERE accounts_id = ?";
    private static String createNewAccountQuery = "INSERT INTO accounts (login, password) VALUES (?, ?)";
    private static String checkAccountQuery = "SELECT accounts_id, uploads, downloads FROM accounts INNER JOIN statistics ON accounts_id = statistics.account_id AND accounts.login = ? AND accounts.password = ?";
    private static String getAllAccountsQuery = "SELECT accounts_id, login, uploads, downloads FROM accounts INNER JOIN statistics ON accounts_id = statistics.account_id";

    private Connection connection;

    public AccountDAO() {
        connection = ConnectionManager.getConnection();
    }

    public boolean deleteAccount(Account account) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(deleteAccountQuery);
            preparedStatement.setString(1, account.getLogin());
            preparedStatement.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveAccountChanges(Account account) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(saveAccountChangesQuery);
            preparedStatement.setString(1, account.getLogin());
            preparedStatement.setBytes(2, SecurityUtil.getHash(account.getPassword()));
            preparedStatement.setLong(3, account.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean createNewAccount(Account account) {
        PreparedStatement accountPreparedStatement;
        try {
            accountPreparedStatement = connection.prepareStatement(createNewAccountQuery);
            accountPreparedStatement.setString(1, account.getLogin());
            accountPreparedStatement.setBytes(2, SecurityUtil.getHash(account.getPassword()));
            accountPreparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean checkAccount(Account account) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(checkAccountQuery);
            preparedStatement.setString(1, account.getLogin());
            preparedStatement.setBytes(2, SecurityUtil.getHash(account.getPassword()));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            account.setId(resultSet.getLong("accounts_id"));
            account.setUploads(resultSet.getLong("uploads"));
            account.setDownloads(resultSet.getLong("downloads"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Account> getAllAccounts() {
        LinkedList<Account> accountsList = new LinkedList<>();
        try {
            ResultSet resultSet = connection.prepareStatement(getAllAccountsQuery).executeQuery();
            while (resultSet.next()) {
                Account account = new Account();
                account.setId(Long.parseLong(resultSet.getString("accounts_id")));
                account.setLogin(resultSet.getString("login"));
                account.setDownloads(resultSet.getLong("downloads"));
                account.setUploads(resultSet.getLong("uploads"));
                accountsList.add(account);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return accountsList;
    }
}
