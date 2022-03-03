import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // Первая часть ДЗ
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        writeString(listToJson(list), "data1.json");

        //Вторая часть ДЗ
        String fileName2 = "data.xml";
        List<Employee> list2 = parseXML(fileName2);
        writeString(listToJson(list2), "data2.json");

        // Третья часть ДЗ
        String json = readString("data1.json");
        List<Employee> list3 = jsonToList(json);
        list3.forEach(System.out::println);
    }

    private static String readString(String fileName) {
        JSONParser parser = new JSONParser();
        String staff = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Object obj = parser.parse(br);
            JSONObject jsonObject = (JSONObject) obj;
            staff = (String) jsonObject.get("staff");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return staff;
    }

    private static List<Employee> jsonToList(String str) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONArray array = (JSONArray) parser.parse(str);
            for (Object obj : array) {
                Employee employee = gson.fromJson(String.valueOf(obj), Employee.class);
                list.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Employee> parseCSV(String[] array, String file) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(array);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String value, String saveToFile) {
        JSONObject obj = new JSONObject();
        obj.put("staff", value);
        try (FileWriter file = new
                FileWriter(saveToFile)) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String file) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(file));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                long a = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String b = element.getElementsByTagName("firstName").item(0).getTextContent();
                String c = element.getElementsByTagName("lastName").item(0).getTextContent();
                String d = element.getElementsByTagName("country").item(0).getTextContent();
                int e = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                staff.add(new Employee(a, b, c, d, e));
            }
        }
        return staff;
    }
}
