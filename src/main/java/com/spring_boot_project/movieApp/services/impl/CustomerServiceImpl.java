package com.spring_boot_project.movieApp.services.impl;

import com.spring_boot_project.movieApp.entities.Customer;
import com.spring_boot_project.movieApp.entities.User;
import com.spring_boot_project.movieApp.repositories.CustomerRepository;
import com.spring_boot_project.movieApp.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer createNewCustomer(User user) {

        Customer customer = Customer
                .builder()
                .user(user)
                .build();

        return customerRepository.save(customer);
    }
}
