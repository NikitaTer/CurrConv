package app.Controllers;

import app.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    private MainWindow mainWindowController;
    private CurrencyWindow currencyWindowController;
    private Main main;
    private Stage currencyWindowStage;

    public MainController() {

    }

    public void setMainWindowController(MainWindow mainWindowController) {
        this.mainWindowController = mainWindowController;
        mainWindowController.setController(this);
    }

    public MainWindow getMainWindowController() {
        return mainWindowController;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    public void openCurrencyWindow(boolean isLeft) {
        FXMLLoader loader = new FXMLLoader(main.getClass().getResource("Views/CurrencyWindow.fxml"));
        currencyWindowStage = new Stage();
        currencyWindowStage.setTitle("Выбор валюты");
        currencyWindowStage.initModality(Modality.WINDOW_MODAL);
        currencyWindowStage.initOwner(main.getPrStage());
        currencyWindowController = new CurrencyWindow(this, isLeft, mainWindowController.getCurrencyList());
        loader.setController(currencyWindowController);
        try {
            currencyWindowStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        currencyWindowStage.show();
    }

    public void closeCurrencyWindow() {
        currencyWindowStage.close();
        currencyWindowController = null;
        currencyWindowStage = null;
    }
}