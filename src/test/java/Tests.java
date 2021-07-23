import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class Tests {
    @Test
    public void checkingCSVFileParsingTest() {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> expectedEmployeeList = Arrays.asList(
                new Employee(1L, "John", "Smith", "USA", 25),
                new Employee(2L, "Inav", "Petrov", "RU", 23));

        List<Employee> employeeListFromCSV = Main.parseCSV("src/test/resources/data.csv", columnMapping);
        assert employeeListFromCSV != null;
        assertEquals(expectedEmployeeList, employeeListFromCSV);
    }

    @Test
    public void checkingXMLFileParsingTest() {
        List<Employee> expectedEmployeeList = Arrays.asList(
                new Employee(1L, "John", "Smith", "USA", 25),
                new Employee(2L, "Inav", "Petrov", "RU", 23));

        List<Employee> employeeListFromCSV = Main.parseXML("src/test/resources/data.xml");
        assertEquals(expectedEmployeeList, employeeListFromCSV);
    }

    @Test
    public void parsToEmployeeTest() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = builder.newDocument();
        Employee expectedEmployee = new Employee(1L, "2", "3", "4", 5);

        Element employee = document.createElement("employee");
        document.appendChild(employee);
        Element id = document.createElement("id");
        id.appendChild(document.createTextNode(String.valueOf(expectedEmployee.id)));
        employee.appendChild(id);
        Element firstName = document.createElement("firstName");
        firstName.appendChild(document.createTextNode(expectedEmployee.firstName));
        employee.appendChild(firstName);
        Element lastName = document.createElement("lastName");
        lastName.appendChild(document.createTextNode(expectedEmployee.lastName));
        employee.appendChild(lastName);
        Element country = document.createElement("country");
        country.appendChild(document.createTextNode(expectedEmployee.country));
        employee.appendChild(country);
        Element age = document.createElement("age");
        age.appendChild(document.createTextNode(String.valueOf(expectedEmployee.age)));
        employee.appendChild(age);

        Employee newEmployee = Main.parsToEmployee(employee);
        assertEquals(expectedEmployee, newEmployee);
    }
}
