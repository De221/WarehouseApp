package bg.DNDWarehouse.warehouseApp.repositories;

import bg.DNDWarehouse.warehouseApp.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findEmployeeByEmail(String email);
    Employee findEmployeeById(Long id);

    @Query("SELECT e " +
            "FROM Employee e " +
            "WHERE e.email =:email")
    Optional<Employee> findOptionalEmployeeByEmail(String email);

    @Query("SELECT e " +
            "FROM Employee e " +
            "WHERE " +
            "lower(e.firstName) " +
            "LIKE :#{#firstName.isEmpty()? '%' : #firstName+'%'} " +
            "AND lower(e.lastName) " +
            "LIKE :#{#lastName.isEmpty()? '%' : #lastName+'%'}")
    Page<Employee> filterEmployees(Pageable pageable, String firstName, String lastName);

    @Query("SELECT COUNT (email) " +
            "FROM Employee " +
            "WHERE EXISTS " +
            "(SELECT t_e " +
            "FROM Task t " +
            "JOIN t.employees t_e " +
            "WHERE t_e.email =:email)")
            int isCurrentlyBusy(String email); //ne raboti mnogo pravilno - 0 ako ne e busy i tasks^2 ako e

//    @Query("SELECT case " +
//            "WHEN EXISTS " +
//            "(SELECT t_e " +
//            "FROM Task t " +
//            "JOIN t.employees t_e " +
//            "WHERE t_e.email =:email) " +
//            "THEN 1 ELSE 0 END")
//    Boolean isCurrentlyBusy(String email);
}
