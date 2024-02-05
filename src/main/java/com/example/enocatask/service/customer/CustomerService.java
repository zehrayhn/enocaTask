package com.example.enocatask.service.customer;

import com.example.enocatask.dto.CustomerDTO;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.exception.CustomerException;

public interface CustomerService {

    Customer addCustomer(CustomerDTO customer) throws CustomerException;
}
