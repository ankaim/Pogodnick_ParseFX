package sample;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

public class ParseRestClass {
    private static Gson gson;
    private static BufferedWriter writer;
    private static Document doc;
    private static Elements All;
    private static Elements wo;
    private static Elements test;
    int id;

    public void result(int id){
        gson = new Gson();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("weter.json")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println("\n" + "Введите ID:");
        try {
            doc = Jsoup.connect("http://pogodnik.com/" + id).get();//сайт с которого парсим
        } catch (IOException e) {
            e.printStackTrace();
        }
        All = doc.getElementsByAttributeValue("class", "small");//Погода
        wo = doc.getElementsByAttributeValue("class", "mci-cont");//Название города
        test = doc.getElementsByTag("div");//Облачность (где иконлки)
        int d = 0;
        //System.out.println(wo.get(0).text() + "\n");
        String a = wo.get(0).text()+" ";        //Создаем суммарную строку для последующей записи в JSON
        for (int i = 76; i < 283; i = i + 23) {
            String f = test.get(i) + "";
            int start = f.indexOf("title");
            int end = f.indexOf("alt");
            a = a + All.get(d).text()+" "+f.substring(start + 7, end - 2)+" ";
            //System.out.print(All.get(d).text()+" ");
            //System.out.println(f.substring(start + 7, end - 2));
            d++;
        }
        String json = gson.toJson(a);
        try {
            writer.write(json);     //Сохраняем файл JSON
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
