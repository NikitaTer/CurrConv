package app.Models.Items;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;

public class CurrencyView {
    private ImageView flag;
    private Label name;
    private String fName;
    private boolean isNoImage = false;
    private HBox hBox;

    public CurrencyView(String name, String rName) {
        fName = name;
        this.name = new Label(rName);

        InputStream inputStream = getClass().getResourceAsStream("../../Views/Images/flags/" + name + ".png");
        Image image;
        if (inputStream != null)
            image = new Image(inputStream,50,50,false,true);
        else {
            image = new Image(getClass().getResourceAsStream("../../Views/Images/flags/No_Image.png"), 50, 50, false, true);
            isNoImage = true;
        }
        flag = new ImageView(image);

        hBox = new HBox(10);
        hBox.getChildren().addAll(flag, this.name);
        hBox.setAlignment(Pos.CENTER_LEFT);
    }

    public HBox getHBox() {
        return hBox;
    }

    public String getName() {
        return fName;
    }

    public String getRName(String string) {
        return name.getText();
    }

    public boolean isNoImage() {
        return isNoImage;
    }
}
