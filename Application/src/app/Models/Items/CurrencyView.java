package app.Models.Items;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CurrencyView {
    private ImageView flag;
    private String url;
    private Label name;
    private HBox hBox;
    private VBox vBox;

    public CurrencyView(String name, String url) {
        this.name = new Label(name);

        if (url != null) {
            flag = new ImageView(new Image(url));
            this.url = url;
        } else {
            flag = new ImageView(new Image("app/Views/Images/flags/No_Image.png"));
            this.url = "app/Views/Images/flags/No_Image.png";
        }


        flag.setFitWidth(100);
        flag.setFitHeight(50);


        vBox = new VBox(5);
        vBox.getChildren().add(this.name);

        hBox = new HBox(10);
        hBox.getChildren().addAll(flag, vBox);
    }

    public HBox getHBox() {
        return hBox;
    }

    public String getUrl() {
        return url;
    }

    public Label getName() {
        return name;
    }
}
