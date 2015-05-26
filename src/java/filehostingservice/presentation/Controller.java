package filehostingservice.presentation;

import filehostingservice.entities.Account;
import filehostingservice.entities.HostingFile;
import filehostingservice.persistence.AccountDAO;
import filehostingservice.persistence.HostingFileDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ProgressIndicator progressBar;
    // Login pane
    @FXML
    private Button logoutButton;
    @FXML
    private Pane loginPane;
    @FXML
    private ImageView icon;
    @FXML
    private Button loginButton;
    @FXML
    private Button createNewAccountButton;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField passwordTextField;
    // Tool Bar
    @FXML
    private ToolBar toolBar;
    @FXML
    private Button uploadFileButton;
    @FXML
    private Button downloadFilesButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button myFilesButton;
    @FXML
    private Button myAccountButton;
    // My Account Pane
    @FXML
    private Pane myAccountPane;
    @FXML
    private TextField accountLoginTextField;
    @FXML
    private PasswordField accountPasswordTextField;
    @FXML
    private Button saveChangesButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private Label accountUploadsLabel;
    @FXML
    private Label accountDownloadsLabel;
    // My Files Pane
    @FXML
    private Pane myFilesPane;
    @FXML
    private TableView<HostingFile> myFilesTable;
    @FXML
    private TableColumn<HostingFile, String> myFilesTitleColumn;
    @FXML
    private TableColumn<HostingFile, String> myFilesCategoryColumn;
    @FXML
    private TableColumn<HostingFile, String> myFilesSizeColumn;
    @FXML
    private TableColumn<HostingFile, String> myFilesExtensionColumn;
    @FXML
    private TableColumn<HostingFile, String> myFilesUploadDateColumn;
    @FXML
    private TableColumn<HostingFile, String> myFilesDescriptionColumn;

    private ObservableList myFilesTableContent;
    // Upload File Pane
    @FXML
    private Pane uploadFilePane;
    @FXML
    private TextField uploadFileTitleTextField;
    @FXML
    private ComboBox<String> uploadFileCategoryComboBox;
    @FXML
    private TextArea uploadFileDescriptionTextArea;
    @FXML
    private Button saveFileButton;
    // Download File Pane
    @FXML
    private Pane downloadFilesPane;
    @FXML
    private CheckBox autocompleteModeCheckBox;
    @FXML
    private ListView<String> dropList;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TextField searchKeyTextField;
    @FXML
    private TableView<HostingFile> downloadFilesTable;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesTitleColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesCategoryColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesSizeColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesExtensionColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesUserColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesUploadDateColumn;
    @FXML
    private TableColumn<HostingFile, String> downloadFilesDescriptionColumn;

    private ObservableList downloadTableContent;
    // Users Pane
    @FXML
    private Pane usersPane;
    @FXML
    private TableView<Account> usersTable;
    @FXML
    private TableColumn<Account, String> usersLoginColumn;
    @FXML
    private TableColumn<Account, String> usersUploadsColumn;
    @FXML
    private TableColumn<Account, String> usersDownloadsColumn;

    private ObservableList usersTableContent;

    private AccountDAO accountDAO;
    private HostingFileDAO fileDAO;
    private HostingFile fileToSave;
    private HostingFile fileToDownload;
    private Account account;
    private HashMap<String, String> filterMap;
    private boolean isFirstClick;
    private ObservableList<String> observableDropList = FXCollections.observableArrayList();
    private HashMap<String, Long> dropMap;
    private FilterMode filterMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPanesVisible(false);
        initialize();
        addLoginPaneListeners();
        addToolBarListeners();
        addMyAccountPaneListeners();
        addUploadFilePaneListeners();
        addDownloadFilesPaneListeners();
        addMyFilesPanePaneListeners();
        addUsersPaneListeners();
        loginPane.setVisible(true);
    }

    private void addMyFilesPanePaneListeners() {
        myFilesTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && !myFilesTableContent.isEmpty()) {
                downloadFile(myFilesTable);
            } else {
                deleteFile(myFilesTable);
            }
        });
    }

    private void deleteFile(TableView<HostingFile> myFilesTable) {
        new Thread(() -> {
            Platform.runLater(() -> {
                setGUIDisable(true);
                progressBar.setVisible(true);
                progressBar.setProgress(-1);
            });
            try {
                fileDAO.deleteFile(myFilesTable.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            Platform.runLater(() -> {
                progressBar.setVisible(false);
                progressBar.setProgress(0);
                downloadTableContent.clear();
                setGUIDisable(false);
                myFilesButton.fire();
            });
        }).start();
    }

    private void setUsersTableContent() {
        usersTable.getItems().clear();
        usersTable.getItems().setAll(accountDAO.getAllAccounts());
    }

    private void setDownloadsFilesTableContent() {
        if (isFirstClick) {
            downloadFilesTable.getItems().setAll(fileDAO.getTopDownloads());
        }
        isFirstClick = false;
    }

    private void addUsersPaneListeners() {
        usersTable.setOnMouseClicked(event -> {
            if (!usersTableContent.isEmpty()) {
                downloadTableContent.clear();
                downloadTableContent.setAll(fileDAO.getFileListByAccount(usersTable.getSelectionModel().getSelectedItem()));
                downloadFilesButton.fire();
            }
        });
    }

    private void setMyFilesTableContent() {
        myFilesTable.getItems().clear();
        myFilesTable.getItems().setAll(fileDAO.getFileListByAccount(account));
    }

    private void addLoginPaneListeners() {
        createNewAccountButton.setOnAction(event -> {
            account = new Account();
            account.setLogin(loginTextField.getText().trim());
            account.setPassword(passwordTextField.getText().trim());

            if (account.getLogin().equals("") || account.getPassword().equals("")) {
                System.out.println("Fill the form!");
            } else if (accountDAO.createNewAccount(account)) {
                showMessageDialog();
            } else {
                System.out.println("Account is already exist!");
            }

        });

        loginButton.setOnAction(event -> {
            account = new Account();
            account.setLogin(loginTextField.getText().trim());
            account.setPassword(passwordTextField.getText().trim());
            if (account.getLogin().equals("") || account.getPassword().equals("")) {
                System.out.println("Fill the form!");
            } else if (accountDAO.checkAccount(account)) {
                accountUploadsLabel.textProperty().set("Uploads : " + String.valueOf(account.getUploads()));
                accountDownloadsLabel.textProperty().set("Downloads : " + String.valueOf(account.getDownloads()));
                showDownloadPane();
            } else {
                System.out.println("Account is not exist!");
            }
        });

        loginTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire();
            }
        });

        passwordTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire();
            }
        });
    }

    private void showMessageDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information dialog");
        alert.setHeaderText("The user with the login " + account.getLogin() + " created!");
        alert.setContentText("Now you can login");
        alert.showAndWait();
    }

    private void showDownloadPane() {
        loginPane.setVisible(false);
        toolBar.setVisible(true);
        downloadFilesButton.fire();
    }

    private void addDownloadFilesPaneListeners() {
        autocompleteModeCheckBox.setOnAction(event -> {
            if (autocompleteModeCheckBox.isSelected()) {
                filterMode = FilterMode.AUTOCOMPLETE;
            } else {
                dropList.getItems().clear();
                dropList.setVisible(false);
                filterMode = FilterMode.AUTOCOMPLETE_DISABLE;
            }
        });

        searchButton.setOnAction(event -> {
            downloadTableContent.clear();
            String key = searchKeyTextField.getText();
            String filter = filterMap.get(filterComboBox.getValue());
            downloadTableContent.setAll(fileDAO.getDownloadList(key, filter));
        });

        downloadFilesTable.setOnMousePressed(event -> {
            if (!downloadTableContent.isEmpty()) {
                downloadFile(downloadFilesTable);
            }
        });

        filterComboBox.setOnMouseMoved(event -> {
            filterComboBox.show();
        });

        observableDropList.addListener((ListChangeListener<String>) change -> {
            if (!observableDropList.isEmpty()) {
                dropList.autosize();
                dropList.setVisible(true);
            } else {
                dropList.setVisible(false);
            }
        });

        dropList.setOnMouseClicked(event -> {
            searchKeyTextField.clear();
            searchKeyTextField.setText(dropList.getSelectionModel().getSelectedItem());
            dropList.setVisible(false);
            searchButton.fire();
        });

        searchKeyTextField.setOnKeyPressed(event -> {
            if (filterMode.equals(FilterMode.AUTOCOMPLETE)) {
                if (searchKeyTextField.getText().equals("")) {
                    observableDropList.clear();
                    dropList.setVisible(false);
                } else if (event.getCode().equals(KeyCode.DOWN)) {
                    searchKeyTextField.clear();
                    searchKeyTextField.setText(dropList.getItems().get(0));
                    dropList.setVisible(false);
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    dropList.setVisible(false);
                    searchButton.fire();
                } else {
                    observableDropList.clear();
                    dropMap = fileDAO.getDropdList(searchKeyTextField.getText(), filterMap.get(filterComboBox.getValue()));
                    observableDropList.addAll(dropMap.keySet());
                    dropList.autosize();
                }
            } else if (event.getCode().equals(KeyCode.ENTER)) {
                searchButton.fire();
            }
        });
    }

    private void downloadFile(TableView<HostingFile> table) {
        fileToDownload = new HostingFile();
        fileToDownload = table.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileToDownload.getCategory(), "*." + fileToDownload.getExtension()));
        fileChooser.setInitialFileName(fileToDownload.getTitle());
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        new Thread(() -> {
            Platform.runLater(() -> {
                setGUIDisable(true);
                progressBar.setVisible(true);
                progressBar.setProgress(-1);
            });
            try {
                Files.write(selectedFile.toPath(), fileDAO.getFileContent(account, fileToDownload));
                selectedFile.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            Platform.runLater(() -> {
                progressBar.setVisible(false);
                progressBar.setProgress(0);
                setGUIDisable(false);
            });
        }).start();
    }

    private void addUploadFilePaneListeners() {
        saveFileButton.setOnAction(event -> {
            fileToSave.setTitle(uploadFileTitleTextField.getText());
            fileToSave.setCategory(uploadFileCategoryComboBox.getValue());
            fileToSave.setDescription(uploadFileDescriptionTextArea.getText());
            fileToSave.setUser(account.getLogin());
            fileToSave.setUploadDate(Date.valueOf(LocalDate.now()));
            new Thread(() -> {
                ProgressIndicator finalIndicator = progressBar;
                Platform.runLater(() -> {
                    setGUIDisable(true);
                    finalIndicator.setVisible(true);
                    finalIndicator.setProgress(-1);
                });
                fileDAO.saveNewFile(account, fileToSave);
                Platform.runLater(() -> {
                    finalIndicator.setVisible(false);
                    finalIndicator.setProgress(0);
                    setGUIDisable(false);
                });
            }).start();
        });

        uploadFileCategoryComboBox.setOnMouseMoved(event -> {
            uploadFileCategoryComboBox.show();
        });
    }

    private void initialize() {
        filterMode = FilterMode.AUTOCOMPLETE_DISABLE;

        dropList.toFront();
        dropList.setEditable(true);
        dropList.setVisible(false);
        dropList.setItems(observableDropList);

        progressBar.setVisible(false);
        progressBar.toFront();
        isFirstClick = true;
        filterMap = new HashMap<>();
        filterMap.put("File title", "title");
        filterMap.put("File category", "category");
        filterMap.put("File extension", "file_extension");
        filterMap.put("User login", "login");
        filterComboBox.getItems().setAll(filterMap.keySet());
        filterComboBox.autosize();
        uploadFileCategoryComboBox.setValue("Category");
        icon.setImage(new Image("filesHosting.png"));

        accountDAO = new AccountDAO();
        fileDAO = new HostingFileDAO();

        toolBar.setVisible(false);

        downloadTableContent = FXCollections.observableArrayList();
        downloadFilesTitleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        downloadFilesCategoryColumn.setCellValueFactory(param -> param.getValue().categoryProperty());
        downloadFilesSizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        downloadFilesExtensionColumn.setCellValueFactory(param -> param.getValue().extensionProperty());
        downloadFilesUserColumn.setCellValueFactory(param -> param.getValue().userProperty());
        downloadFilesUploadDateColumn.setCellValueFactory(param -> param.getValue().uploadDateProperty());
        downloadFilesDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        downloadFilesTable.setItems(downloadTableContent);

        usersTableContent = FXCollections.observableArrayList();
        usersLoginColumn.setCellValueFactory(param -> param.getValue().loginProperty());
        usersDownloadsColumn.setCellValueFactory(param -> param.getValue().downloadsProperty());
        usersUploadsColumn.setCellValueFactory(param -> param.getValue().uploadsProperty());
        usersTable.setItems(usersTableContent);

        myFilesTableContent = FXCollections.observableArrayList();
        myFilesTitleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        myFilesCategoryColumn.setCellValueFactory(param -> param.getValue().categoryProperty());
        myFilesSizeColumn.setCellValueFactory(param -> param.getValue().sizeProperty());
        myFilesExtensionColumn.setCellValueFactory(param -> param.getValue().extensionProperty());
        myFilesUploadDateColumn.setCellValueFactory(param -> param.getValue().uploadDateProperty());
        myFilesDescriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        myFilesTable.setItems(myFilesTableContent);

        uploadFileCategoryComboBox.getItems().setAll(fileDAO.getCategoryList());
    }

    private void addToolBarListeners() {
        myAccountButton.setOnAction(event -> {
            setPanesVisible(false);
            accountLoginTextField.setText(account.getLogin());
            accountPasswordTextField.setText(account.getPassword());
            myAccountPane.setVisible(true);
        });

        myFilesButton.setOnAction(event -> {
            setPanesVisible(false);
            setMyFilesTableContent();
            myFilesPane.setVisible(true);
        });

        uploadFileButton.setOnAction(event -> {
            uploadFileFromSystem();
        });

        downloadFilesButton.setOnAction(event -> {
            setPanesVisible(false);
            setDownloadsFilesTableContent();
            downloadFilesPane.setVisible(true);
        });

        usersButton.setOnAction(event -> {
            setPanesVisible(false);
            setUsersTableContent();
            usersTableContent.clear();
            usersTableContent.setAll(accountDAO.getAllAccounts());
            usersPane.setVisible(true);
        });
    }

    private void addMyAccountPaneListeners() {
        saveChangesButton.setOnAction(event -> {
            account.setLogin(accountLoginTextField.getText().trim());
            account.setPassword(accountPasswordTextField.getText().trim());
            accountDAO.saveAccountChanges(account);
        });

        deleteAccountButton.setOnAction(event -> {
            accountDAO.deleteAccount(account);
            showLoginPane();
        });

        logoutButton.setOnAction(event -> {
            showLoginPane();
        });
    }

    private void showLoginPane() {
        setPanesVisible(false);
        toolBar.setVisible(false);
        searchKeyTextField.clear();
        filterComboBox.getItems().clear();
        autocompleteModeCheckBox.setSelected(false);
        myFilesTableContent.clear();
        downloadTableContent.clear();
        loginTextField.clear();
        passwordTextField.clear();
        account = null;
        loginPane.setVisible(true);
    }

    private void setPanesVisible(boolean isVisible) {
        myAccountPane.setVisible(isVisible);
        myFilesPane.setVisible(isVisible);
        uploadFilePane.setVisible(isVisible);
        downloadFilesPane.setVisible(isVisible);
        usersPane.setVisible(isVisible);
    }

    private void uploadFileFromSystem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to upload");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File uploadFile = fileChooser.showOpenDialog(new Stage());
        if (uploadFile != null) {
        try {
            fileToSave = new HostingFile();
            uploadFileTitleTextField.setText(getFileNameWithoutExtension(uploadFile));
            fileToSave.setExtension(getFileExtension(uploadFile));
            new Thread(() -> {
                Platform.runLater(() -> {
                    setGUIDisable(true);
                    progressBar.setVisible(true);
                    progressBar.setProgress(-1);
                });
                try {
                    fileToSave.setContent(Files.readAllBytes(uploadFile.toPath()));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                    setGUIDisable(false);
                });
            }).start();
            fileToSave.setSize(Files.size(uploadFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPanesVisible(false);
        uploadFileDescriptionTextArea.clear();
        uploadFilePane.setVisible(true);
        }
    }

    private void setGUIDisable(boolean isDisable) {
        autocompleteModeCheckBox.setDisable(isDisable);
        toolBar.setDisable(isDisable);
        myFilesPane.setDisable(isDisable);
        downloadFilesPane.setDisable(isDisable);
        uploadFilePane.setDisable(isDisable);
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public static String getFileNameWithoutExtension(File file) {
        int whereDot = file.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= file.getName().length() - 2) {
            return file.getName().substring(0, whereDot);
        }
        return "";
    }
}

enum FilterMode {
    AUTOCOMPLETE, AUTOCOMPLETE_DISABLE;
}