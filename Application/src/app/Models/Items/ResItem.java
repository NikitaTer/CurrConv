package app.Models.Items;

public class ResItem {
    private String name;
    private double value;
    private double pred;
    private String changePerCent;
    private String change;
    private String time;
    private String unit;

    public ResItem(String name, double value, double pred, String changePerCent, String change, String time, String unit) { //превышение максимального количества элементов в строке
        this.name = name;
        this.value = value;
        this.pred = pred;
        this.changePerCent = changePerCent;
        this.change = change;
        this.time = time;
        this.unit = unit;
    }

    public ResItem() {
        this.name = "";
        this.value = 0;
        this.pred = 0;
        this.changePerCent = "";
        this.change = "";
        this.time = "";
        this.unit = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPred() {
        return pred;
    }

    public void setPred(double pred) {
        this.pred = pred;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setAll(String name, double value, double pred, String changePerCent, String change, String time, String unit) { //превышение максимального количества элементов в строке
        this.name = name;
        this.value = value;
        this.pred = pred;
        this.changePerCent = changePerCent;
        this.change = change;
        this.time = time;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "\"" + name + "\" " + value + " "
                + pred + " " + changePerCent.replaceAll(" %", "") + " "
                + change + " " + time + " \"" + unit + "\"";
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
        tempName = tempName.replaceAll("\"", "");
        temp = temp.substring(i + 2);

        count = 0;
        tempChar = temp.toCharArray();
        i = tempChar.length - 1;
        while (true) {
            if (tempChar[i] == '\"') {
                count++;
                i--;
                if (count == 2) {
                    i++;
                    break;
                }
            } else {
                i--;
            }
        }
        String tempUnit = temp.substring(i, tempChar.length - 1);
        tempUnit = tempUnit.replaceAll("\"", "");
        temp = temp.substring(0, i - 1);

        String[] temps = temp.split(" ");
        name = tempName;
        value = Double.parseDouble(temps[0]);
        pred = Double.parseDouble(temps[1]);
        changePerCent = temps[2] + " %";
        change = temps[3];
        time = temps[4];
        unit = tempUnit;
    }
}
