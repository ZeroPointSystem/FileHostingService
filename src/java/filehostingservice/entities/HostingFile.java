package filehostingservice.entities;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date;
import java.util.Arrays;

/**
 * Created by Zetro on 21.05.2015.
 */
public class HostingFile {
    private StringProperty title;
    private StringProperty extension;
    private StringProperty category;
    private StringProperty description;
    private StringProperty uploadDate;
    private StringProperty size;
    private StringProperty user;
    private byte[] content;
    private long id;

    public HostingFile() {
        title = new SimpleStringProperty();
        extension = new SimpleStringProperty();
        category = new SimpleStringProperty();
        description = new SimpleStringProperty();
        uploadDate = new SimpleStringProperty();
        size = new SimpleStringProperty();
        user = new SimpleStringProperty();
    }

    public String getUser() {
        return user.get();
    }

    public StringProperty userProperty() {
        return user;
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getExtension() {
        return extension.get();
    }

    public StringProperty extensionProperty() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension.set(extension);
    }

    public Long getSize() {
        return Long.valueOf(size.get());
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(Long size) {
        this.size.set(String.valueOf(size));
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public Date getUploadDate() {
        return Date.valueOf(uploadDate.get());
    }

    public StringProperty uploadDateProperty() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate.set(String.valueOf(uploadDate));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HostigsFile{" +
                "category=" + category +
                ", title=" + title +
                ", extension=" + extension +
                ", description=" + description +
                ", uploadDate=" + uploadDate +
                ", size=" + size +
                ", user=" + user +
                ", content=" + Arrays.toString(content) +
                ", id=" + id +
                '}';
    }
}
