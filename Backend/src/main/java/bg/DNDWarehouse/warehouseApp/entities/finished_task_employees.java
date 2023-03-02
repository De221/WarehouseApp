package bg.DNDWarehouse.warehouseApp.entities;

import javax.persistence.*;

@Entity
public class finished_task_employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private Long task_id;
    private Long employee_id;

    public finished_task_employees(Long taskId, Long employeeId){
        this.task_id = taskId;
        this.employee_id = employeeId;
    };
    public finished_task_employees(){};

    public Long getTask_id() {
        return task_id;
    }

    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }

    public Long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(Long employee_id) {
        this.employee_id = employee_id;
    }
}
