package app.Controllers;

import app.Models.Items.CurrencyItem;
import app.Models.Items.ResItem;
import app.Models.Items.SharesItem;
import app.Models.Parser;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.text.TabableView;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainWindow implements Initializable {

    @FXML private ImageView leftFlag;
    @FXML private ImageView rightFlag;
    @FXML private Label leftLabel;
    @FXML private Label rightLabel;
    @FXML private ChoiceBox<Languages> languageBox;
    @FXML private AnchorPane currencyPane;
    @FXML private AnchorPane sharesPane;
    @FXML private AnchorPane resPane;
    @FXML private ScrollPane currencyScrollPane;
    @FXML private ScrollPane sharesScrollPane;
    @FXML private ScrollPane resScrollPane;
    private Lock currencyLock, quotesLock;

    private TableView<CurrencyItem> currencyTable;
    private TableView<SharesItem> sharesTable;
    private TableView<ResItem>[] resTables;
    private ChoiceBox<String> choiceBox;

    private Parser parser;

    private MainController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyLock = new ReentrantLock(true);
        quotesLock = new ReentrantLock(true);
        leftFlag.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    currencyChange(true);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        rightFlag.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    currencyChange(false);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        languageBox.getItems().addAll(Languages.RUS, Languages.ENG);
        languageBox.setValue(Languages.RUS);
        languageBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Languages>() {
            @Override
            public void changed(ObservableValue<? extends Languages> observable, Languages oldValue, Languages newValue) {
                if (oldValue == newValue)
                    return;
                controller.changesLanguage(newValue);
            }
        });

        parser = new Parser(currencyLock, quotesLock);
        parser.start();

        currencyTableInit();
        sharesTableInit();
        resTableInit();

        currencyPane.getChildren().add(currencyTable);
        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(choiceBox, sharesTable);
        sharesPane.getChildren().add(vBox);
        VBox vBox1 = new VBox(0);
        for (TableView<ResItem> tw : resTables)
            vBox1.getChildren().add(tw);
        resPane.getChildren().add(vBox1);
    }

    public void setController(MainController controller) {
        this.controller = controller;

        controller.getMain().getPrStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                parser.stop();
            }
        });
    }

    private void currencyChange(boolean isLeft) throws IOException {
        FXMLLoader loader = new FXMLLoader(controller.getMain().getClass().getResource("Views/CurrencyWindow.fxml"));
        Stage currencyWindow = new Stage();
        if (controller.getLanguage() == Languages.RUS)
            currencyWindow.setTitle("Выбор валюты");
        else {
            currencyWindow.setTitle("Currency selection");
        }
        currencyWindow.initModality(Modality.WINDOW_MODAL);
        currencyWindow.initOwner(controller.getMain().getPrStage());
        loader.setController(new CurrencyWindow(controller));
        currencyWindow.setScene(new Scene(loader.load()));
        controller.setCurrencyWindowController(loader.getController(), currencyWindow);
        controller.getCurrencyWindowController().setLeft(isLeft);

        currencyWindow.show();
    }

    public void setCurrency(boolean isLeft, String url) {
        if (isLeft)
            leftFlag.setImage(new Image(url));
        else
            rightFlag.setImage(new Image(url));
    }

    private void currencyTableInit() {
        currencyPane.setPrefSize(currencyScrollPane.getPrefWidth(), currencyScrollPane.getPrefHeight());
        currencyTable = new TableView<>();
        TableColumn<CurrencyItem, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setSortable(false);

        TableColumn<CurrencyItem, Double> buyColumn = new TableColumn<>("Банк покупает");
        buyColumn.setSortable(false);

        TableColumn<CurrencyItem, Double> sellColumn = new TableColumn<>("Банк продает");
        sellColumn.setSortable(false);

        TableColumn<CurrencyItem, Double> nbrbColumn = new TableColumn<>("НБРБ");
        nbrbColumn.setSortable(false);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        buyColumn.setCellValueFactory(new PropertyValueFactory<>("buy"));
        sellColumn.setCellValueFactory(new PropertyValueFactory<>("sell"));
        nbrbColumn.setCellValueFactory(new PropertyValueFactory<>("nbrb"));
        currencyTable.getColumns().addAll(nameColumn, buyColumn, sellColumn, nbrbColumn);
        currencyTable.prefWidthProperty().set(currencyPane.getPrefWidth());
        currencyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        try {
            currencyLock.lockInterruptibly();
            currencyTable.setItems(parser.getCurrencyList());
            currencyTable.setFixedCellSize(20);
            currencyTable.prefHeightProperty().bind(Bindings.size(currencyTable.getItems()).multiply(currencyTable.getFixedCellSize()).add(26));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            currencyLock.unlock();
        }
    }

    private void sharesTableInit() {
        sharesPane.setPrefSize(sharesScrollPane.getPrefWidth(), sharesScrollPane.getPrefHeight());
        choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("MICEX", "RTS", "Dow Jones", "Nasdaq 100");
        choiceBox.setValue("MICEX");
        choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == newValue)
                    return;
                try {
                    currencyLock.lockInterruptibly();
                    switch (newValue) {
                        case "MICEX":
                            sharesTable.setItems(parser.getSharesLists()[0]);
                            break;

                        case "RTS":
                            sharesTable.setItems(parser.getSharesLists()[1]);
                            break;

                        case "Dow Jones":
                            sharesTable.setItems(parser.getSharesLists()[2]);
                            break;

                        case "Nasdaq 100":
                            sharesTable.setItems(parser.getSharesLists()[3]);
                            break;

                        default:
                            break;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    currencyLock.unlock();
                }
            }
        });
        sharesTable = new TableView<>();
        TableColumn<SharesItem, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setSortable(false);

        TableColumn<SharesItem, String> isinColumn = new TableColumn<>("ISIN");
        isinColumn.setSortable(false);

        TableColumn<SharesItem, Double> predColumn = new TableColumn<>("Пред. закр.");
        predColumn.setSortable(false);

        TableColumn<SharesItem, Double> valueColumn = new TableColumn<>("Посл. зн.");
        valueColumn.setSortable(false);

        TableColumn<SharesItem, String> volumeColumn = new TableColumn<>("Объём");
        volumeColumn.setSortable(false);

        TableColumn<SharesItem, String> changePerCentColumn = new TableColumn<>("%");
        changePerCentColumn.setSortable(false);

        TableColumn<SharesItem, String> changeColumn = new TableColumn<>("+/-");
        changeColumn.setSortable(false);

        TableColumn<SharesItem, String> timeColumn = new TableColumn<>("Время");
        timeColumn.setSortable(false);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        isinColumn.setCellValueFactory(new PropertyValueFactory<>("isin"));
        predColumn.setCellValueFactory(new PropertyValueFactory<>("pred"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        changePerCentColumn.setCellValueFactory(new PropertyValueFactory<>("changePerCent"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        sharesTable.getColumns().addAll(nameColumn, isinColumn, predColumn, valueColumn, volumeColumn, changePerCentColumn, changeColumn, timeColumn);
        sharesTable.setPrefWidth(sharesPane.getPrefWidth());
        sharesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        try {
            quotesLock.lockInterruptibly();
            sharesTable.setItems(parser.getSharesLists()[0]);
            sharesTable.setFixedCellSize(20);
            sharesTable.prefHeightProperty().bind(Bindings.size(sharesTable.getItems()).multiply(sharesTable.getFixedCellSize()).add(26));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            quotesLock.unlock();
        }
    }

    private void resTableInit() {
        resTables = new TableView[4];
        resPane.setPrefSize(resScrollPane.getPrefWidth(), resScrollPane.getPrefHeight());
        for (int i = 0; i < resTables.length; i++) {
            resTables[i] = new TableView<>();
            TableColumn<ResItem, String> nameColumn;
            switch (i) {
                case 0:
                    nameColumn = new TableColumn<>("Драгметаллы");
                    break;

                case 1:
                    nameColumn = new TableColumn<>("Энергоносители");
                    break;

                case 2:
                    nameColumn = new TableColumn<>("Металлы");
                    break;

                case 3:
                    nameColumn = new TableColumn<>("C/x продукция");
                    break;

                default:
                    nameColumn = new TableColumn<>("Unknown");
                    break;
            }
            TableColumn<ResItem, Double> valueColumn = new TableColumn<>("Посл. зн.");
            valueColumn.setSortable(false);

            TableColumn<ResItem, Double> predColumn = new TableColumn<>("Пред. закр.");
            predColumn.setSortable(false);

            TableColumn<ResItem, String> changePerCentColumn = new TableColumn<>("%");
            changePerCentColumn.setSortable(false);

            TableColumn<ResItem, String> changeColumn = new TableColumn<>("+/-");
            changeColumn.setSortable(false);

            TableColumn<ResItem, String> timeColumn = new TableColumn<>("Время");
            timeColumn.setSortable(false);

            TableColumn<ResItem, String> unitColumn = new TableColumn<>("Ед. измерения");
            unitColumn.setSortable(false);

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            predColumn.setCellValueFactory(new PropertyValueFactory<>("pred"));
            changePerCentColumn.setCellValueFactory(new PropertyValueFactory<>("changePerCent"));
            changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
            timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
            unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
            resTables[i].getColumns().addAll(nameColumn, valueColumn, predColumn, changePerCentColumn, changeColumn, timeColumn, unitColumn);
            resTables[i].setPrefWidth(resPane.getPrefWidth());
            resTables[i].setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            try {
                quotesLock.lockInterruptibly();
                resTables[i].setItems(parser.getResList()[i]);
                resTables[i].setFixedCellSize(20);
                resTables[i].prefHeightProperty().bind(Bindings.size(resTables[i].getItems()).multiply(resTables[i].getFixedCellSize()).add(26));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                quotesLock.unlock();
            }
        }
    }
}
