package app.Controllers;

import app.Main;
import javafx.stage.Stage;

public class MainController {
    private MainWindow mainWindowController;
    private CurrencyWindow currencyWindowController;
    private Main main;
    private Stage currencyWindowStage;
    public Languages language = Languages.RUS;

    public MainController() {

    }

    public void setCurrencyWindowController(CurrencyWindow currencyWindowController, Stage currencyWindowStage) {
        this.currencyWindowController = currencyWindowController;
        this.currencyWindowStage = currencyWindowStage;
    }

    public CurrencyWindow getCurrencyWindowController() {
        return currencyWindowController;
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

    public void closeCurrencyWindow() {
        currencyWindowStage.close();
        currencyWindowController = null;
        currencyWindowStage = null;
    }

    public void changesLanguage(Languages language) {
        if (this.language == language)
            return;
        this.language = language;
    }

    public Languages getLanguage() {
        return language;
    }
}

enum Languages {
    RUS,
    ENG
}
