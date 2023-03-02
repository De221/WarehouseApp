package bg.DNDWarehouse.warehouseApp.controllers;

import bg.DNDWarehouse.warehouseApp.entities.*;
import bg.DNDWarehouse.warehouseApp.repositories.CityRepository;
import bg.DNDWarehouse.warehouseApp.repositories.EmployeeRepository;
import bg.DNDWarehouse.warehouseApp.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    BCryptPasswordEncoder encoder;
    private final EmployeeRepository employeeRepo;
    private final CityRepository cityRepo;
    private final WarehouseRepository warehouseRepo;

    EmployeeController(EmployeeRepository employeeRepository, CityRepository cityRepository, WarehouseRepository warehouseRepository)
    {
        employeeRepo = employeeRepository;
        cityRepo = cityRepository;
        warehouseRepo = warehouseRepository;
    }

    @GetMapping("findByEmail")
    private String getEmployeeName(@RequestParam String email)
    {
        return employeeRepo.findEmployeeByEmail(email).getFullName();
    }

    @GetMapping("/fetch")
    private List<Employee> getAllEmployees()
    {
        return employeeRepo.findAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterEmployees(@RequestParam(defaultValue = "")String fname,
                                             @RequestParam(defaultValue = "")String lname,
                                             @RequestParam(defaultValue = "1")int currentPage,
                                             @RequestParam(defaultValue = "5")int perPage)
    {
        Pageable pageable = PageRequest.of(currentPage - 1, perPage);
        Page<Employee> employees = employeeRepo.filterEmployees(pageable, fname.toLowerCase(), lname.toLowerCase());
        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", employees.getTotalElements());
        response.put("totalPages", employees.getTotalPages());
        response.put("employees", employees.getContent());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public String deleteEmployee(@RequestParam(required = true)String email)
    {
        Employee result = employeeRepo.findEmployeeByEmail(email);
        if (result == null)
        {
            return "There is no such employee.";
        }
        else if (employeeRepo.isCurrentlyBusy(email) > 0)
            return "You can not delete an employee which is aquired for task.";
        employeeRepo.delete(result);
        return "Employee deleted successfuly.";
    }
    @PostMapping("/makeInactive")
    public String makeEmployeeInactive(@RequestParam(required = true)String email)
    {
        Employee result = employeeRepo.findEmployeeByEmail(email);
        if (result == null)
        {
            return "There is no such employee.";
        }
        else if (employeeRepo.isCurrentlyBusy(email) > 0)
            return "This employee is currently hired for a task.";
        result.setActive(false);
        employeeRepo.save(result);
        return "Employee's account status has been made inactive successfuly.";
    }

    @PostMapping("/save")
    public String persistEmployee(@RequestParam(required = true)String fname,
                                  @RequestParam(required = true)String lname,
                                  @RequestParam(required = true)String cityName,
                                  @RequestParam(required = true)String email,
                                  @RequestParam(required = true)String password)
    {
        Employee questionableEmployee = employeeRepo.findEmployeeByEmail(email);

        if(questionableEmployee == null)
        {
            Employee employee1 = new Employee(fname, lname, email);
            if(cityRepo.findCityByName(cityName) == null)
                return "Incorrect city name.";
            employee1.setCity(cityRepo.findCityByName(cityName));
            employee1.setPassword(encoder.encode(password));
            employee1.setRoles("ROLE_USER");
            employee1.setActive(true);
            employeeRepo.save(employee1);
            return "Registration - successful!";
        }
        else
            return "This email is already registered to another user.";
    }

    @PostMapping("/changeEmail")
    public String changeEmail(@RequestParam(required = true) String email,
                              @RequestParam(required = true) String newEmail)
    {
        Employee questionableEmployee = employeeRepo.findEmployeeByEmail(email);

        if(questionableEmployee == null)
            return "There is no employee with such email.";
        if(questionableEmployee.getEmail().equals(newEmail))
            return questionableEmployee.getFullName() + "'s email is already " + newEmail + ".";
        if(employeeRepo.findEmployeeByEmail(newEmail) != null)
            return "This email is already registered.";

        questionableEmployee.setEmail(newEmail);
        employeeRepo.save(questionableEmployee);

        return questionableEmployee.getFullName() + "'s email has been updated!";
    }

    @GetMapping("/fetchCity")
    private List<City> getAllCities()
    {
        return cityRepo.findAll();
    }

    @GetMapping("/getCityId")
    private Long getCityId(@RequestParam(required = true) String cityName)
    {
        return cityRepo.findCityByName(cityName).getId();
    }

    @PostMapping("/addWarehouse")
    public ResponseEntity<?> addWarehouse(@RequestParam(required = true) int storageSpace,
                                          @RequestParam(required = true) Long cityId)
    {
        if(cityRepo.findCityById(cityId) != null)
        {
            Warehouse warehouse1 = new Warehouse(cityRepo.findCityById(cityId),storageSpace);
            warehouseRepo.save(warehouse1);
            return ResponseEntity.ok("Warehouse added successfully.");
        }
        return  ResponseEntity.ok("Invalid city Id.");
    }

    @PostMapping("/addCity")
    public ResponseEntity<?> addCity(@RequestParam(required = true) String cityName)
    {
        if(cityRepo.findCityByName(cityName) == null)
        {
            City city1 = new City(cityName);
            cityRepo.save(city1);
            return ResponseEntity.ok("City added successfully.");
        }
        return  ResponseEntity.ok("There is already a city with this name.");
    }
    @PostMapping("/changeEmployeeProfile")
    public ResponseEntity<?> changeEmployeeProfile(@RequestParam(required = true) String oldEmail,
                                                   @RequestParam(required = true) String name,
                                                   @RequestParam(required = true) String email,
                                                   @RequestParam(required = true) String city,
                                                   @RequestParam(required = true) String accStatus,
                                                   @RequestParam(required = true) String role)
    {
        Employee employee1 = employeeRepo.findEmployeeByEmail(oldEmail);
        String[] names = name.split(" ");
        employee1.setFirstName(names[0]);
        employee1.setLastName(names[1]);
        if(employeeRepo.findEmployeeByEmail(email) == null)
        {
            employee1.setEmail(email);
        }
        if(employeeRepo.isCurrentlyBusy(oldEmail) == 0)
        {
            employee1.setCity(cityRepo.findCityByName(city));
        }
        if(accStatus.equals("1"))
        {
            employee1.setActive(true);
        }
        if(accStatus.equals("0"))
        {
            employee1.setActive(false);
        }
        employee1.setRoles(role);

        employeeRepo.save(employee1);
        if(employeeRepo.isCurrentlyBusy(oldEmail) != 0)
            return ResponseEntity.ok("This employee is currently hired for a task. Change its city when the task is finished.");
        else if (employeeRepo.findEmployeeByEmail(email) != null)
            return ResponseEntity.ok("There is already an employee registered to this email.");
        else
            return ResponseEntity.ok("Employee account modified successfully.");
    }
}
