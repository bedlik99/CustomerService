package pl.jbed.stud.SomeWebService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jbed.stud.SomeWebService.DAO.CustomerRepository;
import pl.jbed.stud.SomeWebService.Entity.Customer;
import pl.jbed.stud.SomeWebService.Entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceImpl implements IService {

    private final CustomerRepository customerRepository;

    //private RoleRepository roleRepository;

    @Override
    public Customer findByUserName(String userName) {
        // check the database if the user already exists
        return customerRepository.findByUsername(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException("Could not find user");
        }

        return new org.springframework.security.core.userdetails.User(customer.getUserName(), customer.getPassword(),
                mapRolesToAuthorities(customer.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Autowired
    public ServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public Customer getCustomer(int theId) {
        Optional<Customer> resultOfSearching = customerRepository.findById(theId);
        Customer searchedCustomer = null;
        if (resultOfSearching.isPresent()) {
            searchedCustomer = resultOfSearching.get();
        } else {
            throw new RuntimeException("Did not find a customer with id: " + theId);
        }

        return searchedCustomer;
    }

    @Override
    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    @Override
    public void deleteCustomer(int theId) {
        Optional<Customer> resultOfSearching = customerRepository.findById(theId);

        if (resultOfSearching.isPresent()) {
            customerRepository.deleteById(theId);
        } else {
            throw new RuntimeException("Did not find a customer with id: " + theId);
        }

    }



}

