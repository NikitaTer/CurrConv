package app.Controllers;

import app.Models.Items.CurrencyView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CurrencyWindow implements Initializable {

    @FXML private ScrollPane currencyScroll;
    @FXML private Label choiceCurrencyLabel;
    private VBox vBox;

    private MainController controller;
    private boolean isLeft;
    private EventHandler<MouseEvent> hBoxEvent;

    public CurrencyWindow(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeLanguage();
        vBox = new VBox(10);
        currencyScroll.setContent(vBox);
        File file = new File(String.valueOf(getClass().getResource("src/app/Views/Images/flags/USD.png")));
        CurrencyView second;
        if (file.exists()) {
            second = new CurrencyView("Белорусский рубль", file.getPath());
        } else {
            second = new CurrencyView("Белорусский рубль",null);
        }

        file = new File(String.valueOf(getClass().getResource("src/app/Views/Images/flags/USD.png")));
        CurrencyView first;
        if (file.exists()) {
            first = new CurrencyView("Доллар США", file.getPath());
        } else {
            first = new CurrencyView("Доллар США",null);
        }


        first.getHBox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.getMainWindowController().setCurrency(isLeft, first.getUrl());
                controller.closeCurrencyWindow();
            }
        });

        second.getHBox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.getMainWindowController().setCurrency(isLeft, second.getUrl());
                controller.closeCurrencyWindow();
            }
        });

        vBox.getChildren().addAll(first.getHBox(), second.getHBox());
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public void changeLanguage() {
        switch (controller.getLanguage()) {
            case RUS:
                choiceCurrencyLabel.setText("Выбор валюты");
                break;

            case ENG:
                choiceCurrencyLabel.setText("Currency selection");
                break;

            default: break;
        }
    }
}
