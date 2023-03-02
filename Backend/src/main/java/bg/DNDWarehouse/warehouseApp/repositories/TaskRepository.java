package bg.DNDWarehouse.warehouseApp.repositories;

import bg.DNDWarehouse.warehouseApp.entities.Employee;
import bg.DNDWarehouse.warehouseApp.entities.Packet;
import bg.DNDWarehouse.warehouseApp.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findTaskById(Long id);
    Task findTaskByName(String name);

    @Query("SELECT t_e " +
            "FROM Task t " +
            "JOIN t.employees t_e " +
            "WHERE t.id = :taskId")
    List<Employee> getTaskEmployees(Long taskId);

    @Query("SELECT ft.employee_id " +
            "FROM finished_task_employees ft " +
            "WHERE ft.task_id = :taskId")
    List<Long> getFinishedTaskEmployeesIds(Long taskId);

    @Query("SELECT count(t_e) " +
            "FROM Task t " +
            "JOIN t.employees t_e " +
            "WHERE t.id = :taskId")
    int getTaskEmployeesCount(Long taskId);

    @Query("SELECT t_p " +
            "FROM Task t " +
            "JOIN t.packets t_p " +
            "WHERE t.id = :taskId")
    List<Packet> getTaskPackets(Long taskId);

    @Query("SELECT t " +
            "FROM Task t " +
            "JOIN t.employees t_e " +
            "WHERE t_e.email = :email")
    List<Task> getEmployeeTasks(String email);

    @Query("SELECT t " +
            "FROM Task t " +
            "JOIN finished_task_employees ft_e " +
            "ON t.id = ft_e.task_id " +
            "JOIN Employee e " +
            "ON e.id = ft_e.employee_id " +
            "WHERE e.email =:email")
    List<Task> getEmployeeFinishedTasks(String email);

//    @Query("SELECT t.id AS taskNumber, t_e.firstName, t_e.lastName " +
//            "FROM Task t " +
//            "JOIN t.employees t_e " +
//            "WHERE t_e.firstName = :fname AND t_e.lastName = :lname"
//    )
//    List<Employee> getTaskEmployee(String fname, String lname);
}
