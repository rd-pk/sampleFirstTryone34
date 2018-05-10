package com.prokarma.dao;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.prokarma.model.Employee;
 
public class EmployeeDB {
     
    public static HashMap<Integer, Employee> employees = new HashMap<>();
    static{
        employees.put(1, new Employee(1, "Rohit", "Kumar", "India"));
        employees.put(2, new Employee(2, "Anshuman", "Singh", "India"));
        employees.put(3, new Employee(3, "Ninod", "Pillai", "USA"));
        employees.put(4, new Employee(3, "Sri Hari", "Tadepalli", "USA"));
    }
     
    public static List<Employee> getEmployees(){
        return new ArrayList<Employee>(employees.values());
    }
     
    public static Employee getEmployee(Integer id){
        return employees.get(id);
    }
     
    public static void updateEmployee(Integer id, Employee employee){
        employees.put(id, employee);
    }
     
    public static void removeEmployee(Integer id){
        employees.remove(id);
    }
}