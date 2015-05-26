package filehostingservice.entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Zetro on 22.05.2015.
 */
public class Account {
    private SimpleStringProperty login;
    private SimpleStringProperty password;
    private SimpleStringProperty uploads;
    private SimpleStringProperty downloads;
    private long id;

    public Account() {
        login = new SimpleStringProperty();
        password = new SimpleStringProperty();
        uploads = new SimpleStringProperty();
        downloads = new SimpleStringProperty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getDownloads() {
        return Long.valueOf(downloads.get());
    }

    public SimpleStringProperty downloadsProperty() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads.set(String.valueOf(downloads));
    }

    public String getLogin() {
        return login.get();
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public Long getUploads() {
        return Long.valueOf(uploads.get());
    }

    public SimpleStringProperty uploadsProperty() {
        return uploads;
    }

    public void setUploads(Long uploads) {
        this.uploads.set(String.valueOf(uploads));
    }
}
