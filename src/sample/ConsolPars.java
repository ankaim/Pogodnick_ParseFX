package sample;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//Ищем элеменет в БД
public class ConsolPars {
    private static Connection connection;
    private static Statement stmt;
    private static Scanner sc;
    private static Map<Integer, String> findet;
    private static Gson gson;
    private static String cit;
    private static int gorod;
    private static BufferedWriter writer;

    public static void connect()throws SQLException{        //Поключаемся к БД
        connection = DriverManager.getConnection("jdbc:sqlite:listCities.db");
        stmt = connection.createStatement();
    }
    public static void disconnect(){        //Отключаемся от БД
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Введите название города:");
        sc = new Scanner(System.in);
        cit = sc.nextLine();
        try {
            connect();
            findet = new HashMap<>();
            while (true) {
                ResultSet set = stmt.executeQuery("select*from idCities where city = '" + cit + "';");   //Запрашиваем строку по городу
                while (set.next()) {
                    findet.put(set.getInt(1), set.getString(2) + " " + set.getString(3));    //Загружаем все найденные сроки в массив
                }
                if (findet.size() > 1) {
                    System.out.println("Найдены следующие совпаденя:");  //Если аналогично введенному несколько вариантов, выдоим
                    for (Map.Entry<Integer, String> inem : findet.entrySet()) {
                        System.out.println(inem.getKey() + " " + inem.getValue());
                    }
                }
                if (findet.size() == 1) {
                    for (Map.Entry<Integer, String> inem : findet.entrySet()) {
                        gorod = inem.getKey(); //Если полное совпадение выводим
                    }
                    break;
                }
                if (findet.size() == 0) {
                    ResultSet sset = stmt.executeQuery("select*from idCities where city like '" + cit + "%';");  //Запрашиваем строку по не полному совподению
                    System.out.println("Возможно вы хотелли ввести:" + "\n");
                    while (sset.next()) {
                        System.out.println(sset.getInt(1) + " " + sset.getString(2) + " " + sset.getString(3));
                    }
                    System.out.println("\n" + "Введите ID:");
                    cit = sc.nextLine();
                    try {
                        gorod = Integer.parseInt(cit);
                    }catch (Exception e){
                        continue;
                    }
                        ResultSet sett = stmt.executeQuery("select*from idCities where id = '" + cit + "';");    //Запрашиваем строку по ID
                    break;
                }
            }

        }catch (SQLException e) {
            System.out.println("Проблемы при работе с БД!");
            e.printStackTrace();
        }finally {
            disconnect();
        }
            gson = new Gson();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("weter.json")));
            Document doc = Jsoup.connect("http://pogodnik.com/" + gorod).get();//сайт с которого парсим

            Elements All = doc.getElementsByAttributeValue("class", "small");//Погода
            Elements wo = doc.getElementsByAttributeValue("class", "mci-cont");//Название города

            Elements test = doc.getElementsByTag("div");//Облачность (где иконлки)
            int d = 0;
            System.out.println(wo.get(0).text() + "\n");
            String a = wo.get(0).text()+" ";        //Создаем суммарную строку для последующей записи в JSON
            for (int i = 76; i < 283; i = i + 23) {
                String f = test.get(i) + "";
                int start = f.indexOf("title");
                int end = f.indexOf("alt");
                a = a + All.get(d).text()+" "+f.substring(start + 7, end - 2)+" ";
                System.out.print(All.get(d).text()+" ");
                System.out.println(f.substring(start + 7, end - 2));
                d++;
            }
        String json = gson.toJson(a);
        writer.write(json);     //Сохраняем файл JSON
            writer.close();
    }
}

