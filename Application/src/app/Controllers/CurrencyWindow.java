package app.Controllers;

import app.Models.Items.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CurrencyWindow implements Initializable {

    @FXML
    private ScrollPane currencyScroll;
    private VBox vBox;

    private MainController controller;
    private boolean isLeft;
    private ArrayList<CurrencyItem> currencyItemsList;

    public CurrencyWindow(MainController controller, boolean isLeft, ArrayList<CurrencyItem> currencyItemsList) {
        this.controller = controller;
        this.isLeft = isLeft;
        this.currencyItemsList = currencyItemsList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBox = new VBox(5);
        for (CurrencyItem currencyItem : currencyItemsList) {
            CurrencyView currencyView = new CurrencyView(currencyItem.getName(), currencyItem.getRName());
            currencyView.getHBox().setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    controller.getMainWindowController().setCurrency(isLeft, currencyView.getName(), currencyView.getRName(currencyView.getName()), currencyView.isNoImage()); //превышение максимального количества элементов в строке
                    controller.closeCurrencyWindow();
                }
            });
            vBox.getChildren().add(currencyView.getHBox());
        }
        CurrencyView currencyView = new CurrencyView("Белорусский рубль", "Белорусский рубль");
        currencyView.getHBox().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.getMainWindowController().setCurrency(isLeft, currencyView.getName(), currencyView.getRName(currencyView.getName()), currencyView.isNoImage()); //превышение максимального количества элементов в строке
                controller.closeCurrencyWindow();
            }
        });
        vBox.getChildren().add(currencyView.getHBox());
        currencyScroll.setContent(vBox);
    }
}
