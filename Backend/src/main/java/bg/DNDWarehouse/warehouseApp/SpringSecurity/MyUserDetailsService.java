package bg.DNDWarehouse.warehouseApp.SpringSecurity;

import bg.DNDWarehouse.warehouseApp.entities.Employee;
import bg.DNDWarehouse.warehouseApp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    EmployeeRepository employeeRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Employee> employee = employeeRepo.findOptionalEmployeeByEmail(email);

        employee.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
        return employee.map(MyUserDetails::new).get();
    }
}
