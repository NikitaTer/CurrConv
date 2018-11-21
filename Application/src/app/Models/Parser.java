package app.Models;

import app.Models.Items.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class Parser {
    private Document currencyDoc, resDoc;
    private Document[] sharesDocs;
    private static final String currencyUrl = "https://finance.tut.by/kurs/";
    private static final String[] sharesUrls = {    "https://www.finanz.ru/aktsii/realnom-vremeni-spisok/MICEX",
                                                    "https://www.finanz.ru/aktsii/realnom-vremeni-spisok/RTS",
                                                    "https://www.finanz.ru/aktsii/realnom-vremeni-spisok/Dow_Jones",
                                                    "https://www.finanz.ru/aktsii/realnom-vremeni-spisok/Nasdaq_100"};
    private static final String resUrl = "https://www.finanz.ru/birzhevyye-tovary/v-realnom-vremeni";
    private static final String[] filesPaths = {    "src/app/Files/Currency.txt", "src/app/Files/Shares_MICEX.txt", "src/app/Files/Shares_RTS.txt",
                                                    "src/app/Files/Shares_Dow_Jones.txt", "src/app/Files/Shares_Nasdaq_100.txt", "src/app/Files/Resources.txt"};
    private File currencyFile, resFile;
    private File[] sharesFiles;
    private volatile ObservableList<CurrencyItem> currencyList;
    private volatile ObservableList<SharesItem>[] sharesLists;
    private volatile ObservableList<ResItem>[] resLists;


    Thread currencyThread, quotesThread;
    private volatile boolean isRunning = false;
    private Lock currencyLock, quotesLock;

    public Parser(Lock currencyLock, Lock quotesLock) {
        this.currencyLock = currencyLock;
        this.quotesLock = quotesLock;
        currencyList = FXCollections.observableArrayList();
        sharesLists = new ObservableList[4];
        sharesLists[0] = FXCollections.observableArrayList();
        sharesLists[1] = FXCollections.observableArrayList();
        sharesLists[2] = FXCollections.observableArrayList();
        sharesLists[3] = FXCollections.observableArrayList();
        resLists = new ObservableList[4];
        resLists[0] = FXCollections.observableArrayList();
        resLists[1] = FXCollections.observableArrayList();
        resLists[2] = FXCollections.observableArrayList();
        resLists[3] = FXCollections.observableArrayList();
        sharesDocs = new Document[4];
        currencyFile = new File(filesPaths[0]);
        sharesFiles = new File[4];
        sharesFiles[0] = new File(filesPaths[1]);
        sharesFiles[1] = new File(filesPaths[2]);
        sharesFiles[2] = new File(filesPaths[3]);
        sharesFiles[3] = new File(filesPaths[4]);
        resFile = new File(filesPaths[5]);
        currencyRead();
        sharesRead();
        resRead();
    }

    public synchronized void start() {
        if (isRunning) return;
        isRunning = true;

        currencyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning){
                        currencyLock.lockInterruptibly();
                        currencyUpdate();
                        currencyWrite();
                        currencyLock.unlock();
                        Thread.sleep(300000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    currencyLock.unlock();
                }
            }
        }, "Currency Thread");

        quotesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning){
                        quotesLock.lockInterruptibly();
                        sharesUpdate();
                        resUpdate();
                        sharesWrite();
                        resWrite();
                        quotesLock.unlock();
                        Thread.sleep(60000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    quotesLock.unlock();
                }
            }
        }, "Quotes Thread");

        currencyThread.start();
        quotesThread.start();
    }

    public synchronized void stop() {
        if (!isRunning) return;
        isRunning = false;
        try {
            currencyThread.interrupt();
            quotesThread.interrupt();
            currencyThread.join();
            quotesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<CurrencyItem> getCurrencyList() {
        return currencyList;
    }

    public ObservableList[] getSharesLists() {
        return sharesLists;
    }

    public ObservableList[]getResList() {
        return resLists;
    }

    private void currencyUpdate() {
        try {
            currencyDoc = Jsoup.connect(currencyUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements el = currencyDoc.getElementsByTag("tbody");
        el = el.eq(3);
        el = el.get(0).getElementsByTag("tr");
        int i = 0;
        for (Element element : el) {
            if (element.hasClass("hidden no-hover") || element.hasClass("no-hover graph-block hidden"))
                continue;
            Elements tds = element.getElementsByTag("td");
            String name = tds.get(0).text();
            double buy = Double.parseDouble(tds.get(1).getElementsByTag("p").text().replace(',', '.').replaceAll(" ", ""));
            double sell = Double.parseDouble(tds.get(2).getElementsByTag("p").text().replace(',', '.').replaceAll(" ", ""));
            String nbrbString = tds.get(3).getElementsByTag("p").get(0).text().replace(',', '.').replaceAll(" ", "");
            double nbrb;
            if (nbrbString.contains("—"))
                nbrb = 0;
            else
                nbrb = Double.parseDouble(nbrbString);
            if (currencyList.size() < i + 1)
                currencyList.add(new CurrencyItem(name, buy, sell, nbrb));
            else
                currencyList.get(i).setAll(name, buy, sell, nbrb);
            i++;
        }
    }

    private void sharesUpdate() {
        for (int i = 0; i < sharesLists.length; i++) {
            try {
                sharesDocs[i] = Jsoup.connect(sharesUrls[i]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements el = sharesDocs[i].getElementsByTag("tbody");
            el = el.eq(4);
            el = el.get(0).getElementsByTag("tr");
            boolean isFirst = true;
            int j = 0;
            for (Element element : el) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                Elements tds = element.getElementsByTag("td");
                String name = tds.get(1).getElementsByTag("a").get(0).text();
                String isin = tds.get(2).text();
                double pred = Double.parseDouble(tds.get(3).text().replace(',', '.').replaceAll(" ", ""));
                double value = Double.parseDouble(tds.get(4).getElementsByTag("span").get(0).text().replace(',', '.').replaceAll(" ", ""));
                String volume = tds.get(5).getElementsByTag("span").get(0).text().replace(',', '.').replaceAll(" ", "");
                String changePerCent = tds.get(6).getElementsByTag("span").get(0).text().replace(',', '.');
                String change = tds.get(7).getElementsByTag("span").get(0).text().replace(',', '.').replaceAll(" ", "");
                String time = tds.get(8).getElementsByTag("span").get(0).text();
                if (sharesLists[i].size() < j + 1)
                    sharesLists[i].add(new SharesItem(name, isin, pred, value, volume, changePerCent, change, time));
                else
                    sharesLists[i].get(j).setAll(name, isin, pred, value, volume, changePerCent, change, time);
                j++;
            }
        }
    }

    private void resUpdate() {
        try {
            resDoc = Jsoup.connect(resUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements el = resDoc.getElementsByTag("tbody");
        el = el.eq(1);
        el = el.get(0).getElementsByTag("tr");
        int i = 0;
        int j = -1;
        for (Element element : el) {
            Elements tds = element.getElementsByTag("td");
            if (tds.size() == 0) {
                i = 0;
                j++;
                continue;
            }
            String name = tds.get(1).getElementsByTag("a").get(0).text();
            double value = Double.parseDouble(tds.get(2).getElementsByTag("span").get(0).text().replace(',', '.').replaceAll(" ", ""));
            double pred = Double.parseDouble(tds.get(3).text().replace(',', '.').replaceAll(" ", ""));
            String changePerCent = tds.get(4).getElementsByTag("span").get(0).text().replace(',', '.');
            String change = tds.get(5).getElementsByTag("span").get(0).text().replace(',', '.').replaceAll(" ", "");
            String time = tds.get(6).getElementsByTag("span").get(0).text();
            String unit = tds.get(7).text();
            if (resLists[j].size() < i + 1)
                resLists[j].add(new ResItem(name, value, pred, changePerCent, change, time, unit));
            else
                resLists[j].get(i).setAll(name, value, pred, changePerCent, change, time, unit);
            i++;
        }
    }

    private void currencyWrite() {
        try {
            if (!currencyFile.exists())
                currencyFile.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(currencyFile));
            for (CurrencyItem ci : currencyList) {
                bufferedWriter.write(ci.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sharesWrite() {
            for (int i = 0; i < sharesLists.length; i++) {
                try {
                    if (!sharesFiles[i].exists())
                        sharesFiles[i].createNewFile();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sharesFiles[i]));
                    for (SharesItem si : sharesLists[i]) {
                        bufferedWriter.write(si.toString());
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
    }

    private void resWrite() {
        try {
            if (!resFile.exists())
                resFile.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resFile));
            for (ObservableList<ResItem> ob : resLists) {
                for (ResItem ri : ob) {
                    bufferedWriter.write(ri.toString());
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("next list");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void currencyRead() {
        if (!currencyFile.exists())
            return;
        currencyList.clear();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(currencyFile));
            while (bufferedReader.ready()) {
                CurrencyItem currencyItem = new CurrencyItem();
                currencyItem.fromString(bufferedReader.readLine());
                currencyList.add(currencyItem);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sharesRead() {
        for (int i = 0; i < sharesLists.length; i++) {
            if (!sharesFiles[i].exists())
                return;
            sharesLists[i].clear();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(sharesFiles[i]));
                while (bufferedReader.ready()) {
                    SharesItem sharesItem = new SharesItem();
                    sharesItem.fromString(bufferedReader.readLine());
                    sharesLists[i].add(sharesItem);
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resRead() {
        if (!resFile.exists())
            return;
        for (int i = 0; i < resLists.length; resLists[i].clear(), i++);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(resFile));
            int i = 0;
            String string;
            while (bufferedReader.ready()) {
                string = bufferedReader.readLine();
                if (string.equals("next list")) {
                    i++;
                    continue;
                }
                ResItem resItem = new ResItem();
                resItem.fromString(string);
                resLists[i].add(resItem);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}