package app.Models;

import app.Controllers.MainWindow;

import app.Models.Items.CurrencyItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    private TextField leftNum;
    private TextField rightNum;
    private MainWindow mainWindow;
    private boolean isConverted = false;

    public Converter(TextField leftNum, TextField rightNum, MainWindow mainWindow) {
        this.leftNum = leftNum;
        this.rightNum = rightNum;
        this.mainWindow = mainWindow;

        leftNum.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (check(newValue.toCharArray(), true))
                    convertLeft(newValue);
            }
        });

        rightNum.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (check(newValue.toCharArray(), false))
                    convertRight(newValue);
            }
        });
    }

    private void convertLeft(String value) {
        if (isConverted) {
            isConverted = false;
            return;
        }
        double currNum = Double.parseDouble(value);
        CurrencyItem leftCurrency = mainWindow.getLeftCurrency();
        CurrencyItem rightCurrency = mainWindow.getRightCurrency();
        int kl = 1;
        int kr = 1;

        switch (leftCurrency.getName()) {
            case "10 китайских юаней":
                kl *= 10;
                break;
            case "10 польских злотых":
                kl *= 10;
                break;
            case "10 шведских крон":
                kl *= 10;
                break;
            case "100 йен":
                kl *= 100;
                break;
            case "100 российских рублей":
                kl *= 100;
                break;
            case "100 украинских гривен":
                kl *= 100;
                break;
            case "100 чешских крон":
                kl *= 100;
                break;
            default:
                break;
        }
        switch (rightCurrency.getName()) {
            case "10 китайских юаней":
                kr *= 10;
                break;
            case "10 польских злотых":
                kr *= 10;
                break;
            case "10 шведских крон":
                kr *= 10;
                break;
            case "100 йен":
                kr *= 100;
                break;
            case "100 российских рублей":
                kr *= 100;
                break;
            case "100 украинских гривен":
                kr *= 100;
                break;
            case "100 чешских крон":
                kr *= 100;
                break;
            default:
                break;
        }

        double res = (currNum * leftCurrency.getNbrb() / kl) / (rightCurrency.getNbrb() / kr);
        rightNum.setText(String.format("%.3f", res).replace(',', '.'));
    }

    private void convertRight(String value) {
        if (isConverted) {
            isConverted = false;
            return;
        }
        double currNum = Double.parseDouble(value);
        CurrencyItem leftCurrency = mainWindow.getLeftCurrency();
        CurrencyItem rightCurrency = mainWindow.getRightCurrency();
        int kl = 1;
        int kr = 1;

        switch (leftCurrency.getName()) {
            case "10 китайских юаней":
                kl *= 10;
                break;
            case "10 польских злотых":
                kl *= 10;
                break;
            case "10 шведских крон":
                kl *= 10;
                break;
            case "100 йен":
                kl *= 100;
                break;
            case "100 российских рублей":
                kl *= 100;
                break;
            case "100 украинских гривен":
                kl *= 100;
                break;
            case "100 чешских крон":
                kl *= 100;
                break;
            default:
                break;
        }
        switch (rightCurrency.getName()) {
            case "10 китайских юаней":
                kr *= 10;
                break;
            case "10 польских злотых":
                kr *= 10;
                break;
            case "10 шведских крон":
                kr *= 10;
                break;
            case "100 йен":
                kr *= 100;
                break;
            case "100 российских рублей":
                kr *= 100;
                break;
            case "100 украинских гривен":
                kr *= 100;
                break;
            case "100 чешских крон":
                kr *= 100;
                break;
            default:
                break;
        }

        double res = (currNum * rightCurrency.getNbrb() / kr) / (leftCurrency.getNbrb() / kl);
        leftNum.setText(String.format("%.3f", res).replace(',', '.'));
    }

    private boolean check(char[] chars, boolean isLeft) {
        boolean isFirst = true;
        boolean found = false;
        for (char c : chars) {
            if ((c < '0' || c > '9') && c != '.') {
                if (isLeft)
                    rightNum.setText("wrong value");
                else
                    leftNum.setText("wrong value");
                return false;
            }
            if (isFirst && c == '.') {
                if (isLeft)
                    rightNum.setText("wrong value");
                else
                    leftNum.setText("wrong value");
                return false;
            }
            if (c == '.') {
                if (found) {
                    if (isLeft)
                        rightNum.setText("wrong value");
                    else
                        leftNum.setText("wrong value");
                    return false;
                } else
                    found = true;
            }
            if (isFirst) isFirst = false;
        }
        if (chars[chars.length - 1] == '.') {
            if (isLeft)
                rightNum.setText("wrong value");
            else
                leftNum.setText("wrong value");
            return false;
        }
        return true;
    }
}
