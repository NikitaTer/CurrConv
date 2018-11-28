package app.Models.Items;

public class CurrencyItem {
    private String name;
    private double buy;
    private double sell;
    private double nbrb;

    public CurrencyItem(String name, double buy, double sell, double nbrb) {
        this.name = name;
        this.buy = buy;
        this.sell = sell;
        this.nbrb = nbrb;
    }

    public CurrencyItem() {
        this.name = "";
        this.buy = 0;
        this.sell = 0;
        this.nbrb = 0;
    }

    public String getName() {
        return name;
    }

    public String getRName() {
        switch (name) {
            case "10 китайских юаней":
                return "Китайская юань";
            case "10 польских злотых":
                return "Польский злотый";
            case "10 шведских крон":
                return "Шведская крона";
            case "100 йен":
                return "Йена";
            case "100 российских рублей":
                return "Российский рубль";
            case "100 украинских гривен":
                return "Украинская гривна";
            case "100 чешских крон":
                return "Чешская крона";
            default:
                return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public double getNbrb() {
        return nbrb;
    }

    public void setNbrb(double nbrb) {
        this.nbrb = nbrb;
    }

    public void setAll(String name, double buy, double sell, double nbrb) {
        this.name = name;
        this.buy = buy;
        this.sell = sell;
        this.nbrb = nbrb;
    }

    @Override
    public String toString() {
        return "\"" + name + "\" " + buy + " " + sell + " " + nbrb;
    }

    public void fromString(final String source) {
        String temp = source;
        int i = 0;
        int count = 0;
        char[] tempChar = temp.toCharArray();

        while (true) {
            if (tempChar[i] == '\"') {
                count++;
                i++;
                if (count == 2) {
                    i--;
                    break;
                }
            } else {
                i++;
            }
        }

        String tempName = temp.substring(0, i);
        tempName = tempName.substring(1);
        temp = temp.substring(i + 2);
        String[] temps = temp.split(" ");
        name = tempName;
        buy = Double.parseDouble(temps[0]);
        sell = Double.parseDouble(temps[1]);
        nbrb = Double.parseDouble(temps[2]);
    }
}
