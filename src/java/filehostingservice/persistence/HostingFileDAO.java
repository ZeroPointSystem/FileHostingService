package filehostingservice.persistence;

import filehostingservice.entities.Account;
import filehostingservice.entities.HostingFile;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zetro on 20.05.2015.
 */
public class HostingFileDAO {

    private static String getFileListByAccountQuery = "SELECT files_id, category, login, title, upload_date, description, file_size, file_extension FROM categories INNER JOIN (SELECT files_id, login, title, upload_date, description, category_id, file_size, file_extension FROM accounts INNER JOIN files ON accounts.accounts_id = files.account_id) AS list ON categories.categories_id = list.category_id AND login = ?";
    private static String saveNewFileQuery = "INSERT INTO files (title, file_extension, file_size, upload_date, description, file_content, account_id, category_id) VALUES(?,?,?,?,?,?,?,(SELECT categories_id FROM categories WHERE category = ?))";
    private static String getTopLoadsQuery = "SELECT files_id, title, upload_date, description, category, login, file_size, file_extension FROM accounts INNER JOIN (SELECT files_id, title, upload_date, description, category, account_id, file_size, file_extension FROM categories INNER JOIN (SELECT files_id, title, upload_date, description, category_id, account_id, file_size, file_extension FROM files INNER JOIN files_statistics ON files.files_id = files_statistics.file_id ORDER BY files_statistics.downloads_count DESC  LIMIT 10) AS top ON categories.categories_id = top.category_id) AS goal ON accounts.accounts_id = goal.account_id";
    private static String getCategoryListQuery = "SELECT category FROM categories";
    private static String getFileContentQuery = "SELECT file_content FROM files WHERE files_id = ?";
    private static String deleteFileQuery = "DELETE FROM files WHERE files_id = ?";
    private static String incrementFileDownloadsCountQuery = "UPDATE files_statistics SET downloads_count = ((SELECT downloads_count FROM files_statistics WHERE file_id = ?) + 1) WHERE file_id = ?";
    private static String incrementAccountDownloadsCountQuery = "UPDATE statistics SET downloads = ((SELECT downloads FROM statistics WHERE account_id = ?) + 1) WHERE account_id = ?";

    private Connection connection;
    public HostingFileDAO() {
        connection = ConnectionManager.getConnection();
    }

    public boolean deleteFile(HostingFile file) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(deleteFileQuery);
            preparedStatement.setLong(1, file.getId());
            preparedStatement.execute();
            System.out.println("DELETED");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean incrementFileDownloadsCount(HostingFile file) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(incrementFileDownloadsCountQuery);
            preparedStatement.setLong(1, file.getId());
            preparedStatement.setLong(2, file.getId());
            preparedStatement.execute();
            System.out.println("FILE DOWNLOADS COUNT INCREMENTED");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveNewFile(Account account, HostingFile file) {
        PreparedStatement saveNewFilePreparedStatement;
        try {
            saveNewFilePreparedStatement = connection.prepareStatement(saveNewFileQuery);
            saveNewFilePreparedStatement.setString(1, file.getTitle());
            saveNewFilePreparedStatement.setString(2, file.getExtension());
            saveNewFilePreparedStatement.setLong(3, file.getSize());
            saveNewFilePreparedStatement.setDate(4, file.getUploadDate());
            saveNewFilePreparedStatement.setString(5, file.getDescription());
            saveNewFilePreparedStatement.setBytes(6, file.getContent());
            saveNewFilePreparedStatement.setLong(7, account.getId());
            saveNewFilePreparedStatement.setString(8, file.getCategory());
            saveNewFilePreparedStatement.execute();
            System.out.println("SAVED");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<HostingFile> getFileListByAccount(Account account) {
        LinkedList<HostingFile> hostingFiles = new LinkedList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(getFileListByAccountQuery);
            preparedStatement.setString(1, account.getLogin());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HostingFile hostingFile = new HostingFile();
                hostingFile.setId(resultSet.getLong("files_id"));
                hostingFile.setCategory(resultSet.getString("category"));
                hostingFile.setDescription(resultSet.getString("description"));
                hostingFile.setExtension(resultSet.getString("file_extension"));
                hostingFile.setSize(resultSet.getLong("file_size"));
                hostingFile.setTitle(resultSet.getString("title"));
                hostingFile.setUploadDate(resultSet.getDate("upload_date"));
                hostingFile.setUser(account.getLogin());
                hostingFiles.add(hostingFile);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return hostingFiles;
        }
        return hostingFiles;
    }

    public List<HostingFile> getTopDownloads() {
        LinkedList<HostingFile> hostingFiles = new LinkedList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(getTopLoadsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                HostingFile hostingFile = new HostingFile();
                hostingFile.setId(resultSet.getLong("files_id"));
                hostingFile.setCategory(resultSet.getString("category"));
                hostingFile.setDescription(resultSet.getString("description"));
                hostingFile.setExtension(resultSet.getString("file_extension"));
                hostingFile.setSize(resultSet.getLong("file_size"));
                hostingFile.setTitle(resultSet.getString("title"));
                hostingFile.setUploadDate(resultSet.getDate("upload_date"));
                hostingFile.setUser(resultSet.getString("login"));
                hostingFiles.add(hostingFile);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return hostingFiles;
        }
        return hostingFiles;
    }

    public List<String> getCategoryList() {
        LinkedList<String> categories = new LinkedList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(getCategoryListQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return categories;
        }
        return categories;
    }

    public List<HostingFile> getDownloadList(String key, String filter) {
        LinkedList<HostingFile> hostingFiles = new LinkedList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.setEscapeProcessing(false);
            ResultSet resultSet = statement.executeQuery("SELECT files_id, title, file_size, upload_date, category, file_extension, description, login FROM categories INNER JOIN (SELECT files_id, title, upload_date, file_size, category_id, file_extension, description, login FROM files INNER JOIN accounts ON files.account_id = accounts.accounts_id) AS files_with_login ON categories.categories_id = files_with_login.category_id WHERE "+ filter +" LIKE '"+ key +"%'");
            while (resultSet.next()) {
                HostingFile hostingFile = new HostingFile();
                hostingFile.setId(resultSet.getLong("files_id"));
                hostingFile.setCategory(resultSet.getString("category"));
                hostingFile.setDescription(resultSet.getString("description"));
                hostingFile.setExtension(resultSet.getString("file_extension"));
                hostingFile.setSize(resultSet.getLong("file_size"));
                hostingFile.setTitle(resultSet.getString("title"));
                hostingFile.setUploadDate(resultSet.getDate("upload_date"));
                hostingFile.setUser(resultSet.getString("login"));
                hostingFiles.add(hostingFile);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return hostingFiles;
        }
        return hostingFiles;
    }

    public HashMap<String, Long> getDropdList(String key, String filter) {
        HashMap<String, Long> dropStrings = new HashMap<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.setEscapeProcessing(false);
            String sql = "SELECT " + filter + ", files_id FROM categories INNER JOIN (SELECT files_id, title, category_id, file_extension, login FROM files INNER JOIN accounts ON files.account_id = accounts.accounts_id) AS goal ON categories.categories_id = goal.category_id WHERE " + filter + " LIKE '" + key + "%'";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                dropStrings.put(resultSet.getString(filter), resultSet.getLong("files_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return dropStrings;
        }
        return dropStrings;
    }

    public byte[] getFileContent(Account account, HostingFile file) {
        byte[] content = null;
        PreparedStatement incrementAccountDownloadsCountStatement;
        PreparedStatement getFileContentStatement;
        try {
            getFileContentStatement = connection.prepareStatement(getFileContentQuery);
            getFileContentStatement.setLong(1, file.getId());
            ResultSet resultSet = getFileContentStatement.executeQuery();
            resultSet.next();
            content = resultSet.getBytes("file_content");

            incrementAccountDownloadsCountStatement = connection.prepareStatement(incrementAccountDownloadsCountQuery);
            incrementAccountDownloadsCountStatement.setLong(1, account.getId());
            incrementAccountDownloadsCountStatement.setLong(2, account.getId());
            incrementAccountDownloadsCountStatement.execute();
            System.out.println("ACCOUNT DOWNLOADS COUNT INCREMENTED");
            System.out.println("EXTRACTED");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return content;
        }
        incrementFileDownloadsCount(file);
        return content;
    }
}
