package com.employees.repo;

import java.util.List;

import com.employees.pojo.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    public List<Employee> findByName(String Name);
}
