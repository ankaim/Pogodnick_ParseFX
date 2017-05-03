package sample;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

public class Controller {
    public TextField textField;
    public TextArea textArea;
    static String tstat;
    public void pogoda() throws IOException {
        tstat = textField.getText();
        textField.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("1.txt"), "UTF-8"));
        String[] temp;
        String sd;
        int stad = 0;
        textArea.clear();
        textArea.appendText("Вы ввели что то не то!)))\n Введите начало слова, выберите подходящий вариант\n И введите еще раз правильно!\n К примеру: (моск), если ищите Москву\n\n");
        while (reader.ready()){
            if(tstat!=" ") {
                if ((sd = reader.readLine().toLowerCase()).contains(tstat.toLowerCase())) {
                    temp = sd.split(" ");
                    if (temp[1].equals(tstat.toLowerCase())) {
                        stad = Integer.parseInt(temp[0]);
                        break;
                    } else {
                        if (temp[1].startsWith(tstat.toLowerCase())) {
                            textField.clear();
                            textArea.appendText(sd.substring(sd.indexOf(" ")) + "\n");
                        }
                    }
                }
            }
        }
//        FileWriter writer = new FileWriter("c:/3.txt");
//        writer.write(reader.readLine());
//        reader.close();
//        writer.close();
        if(stad!=0) {
            Document doc = Jsoup.connect("http://pogodnik.com/" + stad).get();

            Elements All = doc.getElementsByAttributeValue("class", "small");
            Elements wo = doc.getElementsByAttributeValue("class", "mci-cont");

            Elements test = doc.getElementsByTag("div");
            int d = 0;
            textArea.clear();
            textArea.appendText(wo.get(0).text() + "\n\n");
            for (int i = 77; i < 284; i = i + 23) {
                String f = test.get(i) + "";
                int start = f.indexOf("title");
                int end = f.indexOf("alt");
                textArea.appendText(All.get(d).text() + "\n");
                textArea.appendText(f.substring(start + 7, end - 2) + "\n\n");
                d++;
            }
        }
    }
}

