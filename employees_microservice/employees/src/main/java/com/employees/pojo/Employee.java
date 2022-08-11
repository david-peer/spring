package com.employees.pojo;


import com.fasterxml.jackson.annotation.JsonFilter;
import org.springframework.data.annotation.Id;

@JsonFilter("classFilter")
public class Employee {

    @Id
    private String id;
    private String name = "";
    private String country = "";
    private String city = "";
    private float salary = 0;
    private String email = "";

    public void setId (String id) {
        this.id = id;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setCountry (String country) {
        this.country = country;
    }

    public void setCity (String city) {
        this.city = city;
    }

    public void setSalary (float salary) {
        this.salary = salary;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public float getSalary() {
        return salary;
    }
    @JsonFilter("email")
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Name: " + name +
                ", Country: " + country +
                ", City: " + city +
                ", Salary: " + salary +
                ", Email:" + email;
    }
}
