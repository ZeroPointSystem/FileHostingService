package filehostingservice.presentation;

import filehostingservice.persistence.ConnectionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FileHostingServiceApplication extends Application  {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fileHostingService.fxml"));
        primaryStage.setTitle("File Hosting Service by ZTR Systems Inc.");
        primaryStage.getIcons().add(new Image("/filesHosting.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            ConnectionManager.closeConnection();
            System.out.println("CONNECTION CLOSE");
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
