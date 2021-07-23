import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> employeeListFromCSV = parseCSV("src/main/resources/data.csv", columnMapping);
        System.out.println(employeeListFromCSV);
        listToJson("src/main/resources/dataCSV.json", employeeListFromCSV);
        List<Employee> employeeListFromXML = parseXML("src/main/resources/data.xml");
        listToJson("src/main/resources/dataXML.json", employeeListFromXML);
        System.out.println(employeeListFromXML);
    }

    public static List<Employee> parseCSV(String pathToFile, String[] map) {
        try (CSVReader csvReader = new CSVReader(new FileReader(pathToFile))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(map);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    public static void listToJson(String pathToFile, List<Employee> employeeList) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        String s = gson.toJson(employeeList);

        try (FileWriter file = new FileWriter(pathToFile)) {
            file.write(s);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String pathToFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(pathToFile));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()&& node.getNodeName().equals("employee")) {
                    employeeList.add(parsToEmployee(node));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static Employee parsToEmployee(Node node) {
        Employee employee = new Employee();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
                Element element = (Element) child;
                try {
                    Field field = Employee.class.getDeclaredField(element.getTagName());
                    field.setAccessible(true);
                    if (element.getTagName().equals("id")) {
                        field.set(employee, Long.valueOf(element.getTextContent()));
                    } else if (element.getTagName().equals("age")) {
                        Employee.class.getDeclaredField("age").set(employee, Integer.parseInt(element.getTextContent()));
                    } else {
                        Employee.class.getDeclaredField(element.getTagName()).set(employee, element.getTextContent());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        return employee;
    }
}
