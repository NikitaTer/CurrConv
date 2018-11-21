package app.Models.Items;

public class SharesItem {
    private String name;
    private String isin;
    private double pred;
    private double value;
    private String volume;
    private String changePerCent;
    private String change;
    private String time;

    public SharesItem(String name, String isin, double pred, double value, String volume, String changePerCent, String change, String time) {
        this.name = name;
        this.isin = isin;
        this.pred = pred;
        this.value = value;
        this.volume = volume;
        this.changePerCent = changePerCent;
        this.change = change;
        this.time = time;
    }

    public SharesItem() {
        this.name = "";
        this.isin = "";
        this.pred = 0;
        this.value = 0;
        this.volume = "";
        this.changePerCent = "";
        this.change = "";
        this.time = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public double getPred() {
        return pred;
    }

    public void setPred(double pred) {
        this.pred = pred;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getChangePerCent() {
        return changePerCent;
    }

    public void setChangePerCent(String changePerCent) {
        this.changePerCent = changePerCent;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAll(String name, String isin, double pred, double value, String volume, String changePerCent, String change, String time) {
        this.name = name;
        this.isin = isin;
        this.pred = pred;
        this.value = value;
        this.volume = volume;
        this.changePerCent = changePerCent;
        this.change = change;
        this.time = time;
    }

    @Override
    public String toString() {
        return "\"" + name + "\" " + isin + " "
                + pred + " " + value + " "
                + volume + " " + changePerCent.replaceAll(" %", "") + " "
                + change + " " + time;
    }

    public void fromString(String source) {
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
        tempName.replaceAll("\"", "");
        temp = temp.substring(i + 2);
        String[] temps = temp.split(" ");
        name = tempName;
        isin = temps[0];
        pred = Double.parseDouble(temps[1]);
        value = Double.parseDouble(temps[2]);
        volume = temps[3];
        changePerCent = temps[4] + " %";
        change = temps[5];
        time = temps[6];
    }
}
