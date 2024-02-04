package com.example.enocatask.service.customer;

import com.example.enocatask.dao.CustomerRepository;
import com.example.enocatask.dto.CustomerDTO;
import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.Customer;
import com.example.enocatask.service.cart.CartServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.enocatask.dto.CustomerDTO.convertDTOToCustomer;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private CartServiceImpl cartService;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CartServiceImpl cartService) {
        this.customerRepository = customerRepository;
        this.cartService = cartService;
    }

    public Customer addCustomer(@Valid CustomerDTO customerDTO) {
        Customer customer = convertDTOToCustomer(customerDTO);
        try {

            customer = customerRepository.save(customer);
        } catch (Exception e) {
            e.getMessage();
        }
        return customer;


    }



}
