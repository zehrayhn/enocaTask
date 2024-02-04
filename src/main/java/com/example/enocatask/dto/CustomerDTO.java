package com.example.enocatask.dto;

import com.example.enocatask.entities.Customer;
import jakarta.validation.constraints.NotBlank;

public class CustomerDTO {
    @NotBlank(message = "Name is required")
    private String name;

    public CustomerDTO( String name) {

        this.name = name;
        }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public CustomerDTO() {
    }

    public static Customer convertDTOToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        return customer;
    }
}
