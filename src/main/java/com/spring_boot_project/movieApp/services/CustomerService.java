package com.spring_boot_project.movieApp.services;

import com.spring_boot_project.movieApp.entities.Customer;
import com.spring_boot_project.movieApp.entities.User;

public interface CustomerService {

    Customer createNewCustomer(User user);
}
