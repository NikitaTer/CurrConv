package app;

import app.Controllers.MainController;
import app.Controllers.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.IOException;

public class Main extends Application {

    private MainController controller;
    private Stage prStage;

    @Override
    public void start(Stage prStage) {
        this.prStage = prStage;
        prStage.setTitle("CurrConv");
        controller = new MainController();
        controller.setMain(this);
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("Views/MainWindow.fxml"));
            prStage.setScene(new Scene(loader.load()));
            controller.setMainWindowController(loader.getController());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        prStage.show();
    }

    public Stage getPrStage() {
        return prStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
