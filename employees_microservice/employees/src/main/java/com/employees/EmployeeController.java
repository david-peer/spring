package com.employees;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.employees.pojo.Employee;
import com.employees.repo.EmployeeRepository;
import org.springframework.web.client.RestTemplate;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate currencyRest;
    private final String currencyURL = "https://free.currconv.com/api/v7/convert?q=ILS_USD&compact=ultra&apiKey=2828db38f53aaa5ccc04";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private SimpleFilterProvider filterProvider;

    @GetMapping("/employees")
    public String getEmployees() {

        List<Employee> employees = repository.findAll();
        String result = "";

        try {
            result = objectMapper.writer(filterProvider).writeValueAsString(employees);
        }
        catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @GetMapping("/employee/{id}")
    public String getEmployee(@PathVariable("id") String id) {

        Employee employee = repository.findById(id).orElse(new Employee());
        String result = "";

        try {
            result = objectMapper.writer(filterProvider).writeValueAsString(employee);
        }
        catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @GetMapping("/employees/emails")
    public String getEmails() {
        List<Employee> list = repository.findAll();
        filterProvider
                .addFilter("classFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("email"));
        String result = "";

        try {
            result = objectMapper.writer(filterProvider)
                    .writeValueAsString(list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            filterProvider.removeFilter("classFilter");
        }

        return result;
    }

    @PostMapping(path="/employee", consumes = "application/json")
    public String addEmployee(@RequestBody String input) {

        String result = "";

        try {
            Employee employee = objectMapper.readValue(input, Employee.class);

            // convert NIS to USD. If the exchange rate isn't important and the employee is paid
            // in NIS then we can convert here. If he is being paid in USD it's important to always
            // check the changing exchange rate. Since the currency server asked not to abuse with
            // requests I will convert once
            employee.setSalary(employee.getSalary() * getExchangeRate());

            result = objectMapper.writer(filterProvider).writeValueAsString(employee);
            repository.save(employee);
            kafkaTemplate.send("employees", employee.getName());
        }
        catch (JsonProcessingException | RuntimeException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @DeleteMapping("/employee/remove/{id}")
    public String deleteEmployee(@PathVariable("id") String id) {

        Employee employee = repository.findById(id).orElse(new Employee());

        String result = getEmployee(id);

        repository.delete(employee);

        return result;
    }

    private float getExchangeRate() {
        ResponseEntity<String> response = currencyRest.getForEntity(
                        currencyURL, String.class);
        float rate = 0.0F;

        try {
            ObjectNode node = objectMapper.readValue(response.getBody(),
                    ObjectNode.class);
            if (node.has("ILS_USD")) {
                rate = node.get("ILS_USD").floatValue();
            } else {
                throw new RuntimeException("failed to retrieve exchange rate");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return rate;
    }
}
