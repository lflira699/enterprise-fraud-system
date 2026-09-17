package com.efs.modules.customer.adapter;

import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.port.out.CustomerExistencePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerExistenceAdapter
        implements CustomerExistencePort {

    private final CustomerRepository customerRepository;

    public CustomerExistenceAdapter(
            CustomerRepository customerRepository) {

        this.customerRepository =
                customerRepository;
    }

    @Override
    public boolean exists(
            UUID customerId) {

        return customerRepository.existsById(
                customerId
        );
    }
}
