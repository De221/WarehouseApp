package bg.DNDWarehouse.warehouseApp.controllers;

import bg.DNDWarehouse.warehouseApp.entities.Employee;
import bg.DNDWarehouse.warehouseApp.entities.Packet;
import bg.DNDWarehouse.warehouseApp.entities.Task;
import bg.DNDWarehouse.warehouseApp.entities.finished_task_employees;
import bg.DNDWarehouse.warehouseApp.repositories.EmployeeRepository;
import bg.DNDWarehouse.warehouseApp.repositories.PacketRepository;
import bg.DNDWarehouse.warehouseApp.repositories.TaskRepository;
import bg.DNDWarehouse.warehouseApp.repositories.finished_task_employeesRepository;
import bg.DNDWarehouse.warehouseApp.utils.OrderConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/task")
@CrossOrigin(origins = "*")
public class TaskController {

    HashMap<String, String> statusMap = new HashMap<>();
    @PostConstruct
    public void init() {
        statusMap.put(OrderConstants.NEW_STATUS, "New Task");
        statusMap.put(OrderConstants.IN_PROGRESS_STATUS, "In Progress");
        statusMap.put(OrderConstants.FINISHED_STATUS, "Finished");
    }

    private TaskRepository taskRepo;
    private EmployeeRepository employeeRepo;
    private PacketRepository packetRepo;
    private finished_task_employeesRepository finishedRepo;
    TaskController(TaskRepository taskRepository, EmployeeRepository employeeRepository, PacketRepository packetRepository,
                   finished_task_employeesRepository finishedRepository) {
        taskRepo = taskRepository;
        employeeRepo = employeeRepository;
        packetRepo = packetRepository;
        finishedRepo = finishedRepository;
    }

    @GetMapping("/fetch")
    public List<Task> getAllTasks()
    {
        List<Task> tasks = taskRepo.findAll();
        tasks.forEach(Task -> {
            Task.setFullStatusName(statusMap.get(Task.getStatus()));
            if (Task.getStatus().equals("F"))
            {
                List<Long> empIds = taskRepo.getFinishedTaskEmployeesIds(Task.getId());
                for (Long empid : empIds)
                {
                    Task.hireEmployee(employeeRepo.findEmployeeById(empid));
                }
            }
        });
        return tasks;
    }

    @PostMapping("/hireEmployee")
    public ResponseEntity<?> hireEmployee(@RequestParam(required = true)Long taskNumber,
                                          @RequestParam(required = true)String email) {
        Employee employee = employeeRepo.findEmployeeByEmail(email);
        if(employee == null)
            return ResponseEntity.ok("There is no such employee.");

        Task task1 = taskRepo.findTaskById(taskNumber);
        if(task1 != null)
        {
            if(task1.getStatus().equals("F"))
                return ResponseEntity.ok("Finished tasks are not eligible for employee addition.");
            List<Employee> task_employees = taskRepo.getTaskEmployees(taskNumber);
            for (Employee e : task_employees)
                if(e.hashCode() == employee.hashCode())
                    return ResponseEntity.ok(employee.getFullName() + " is already hired for task " + taskNumber + ".");
            if(task1.getCity() == employee.getCity() || task1.getCity() == null)
            {
                if(employeeRepo.isCurrentlyBusy(email) > 0)
                    return ResponseEntity.ok(employee.getFullName() + " is currently hired for another task.");
                task1.hireEmployee(employee);
                taskRepo.save(task1);
                return ResponseEntity.ok(employee.getFullName() + " has been hired for task " + taskNumber + ".");
            }
            else
                return ResponseEntity.ok(employee.getFullName() + " is in different city. Hire employees from same city as the task.");
        }
        else
        {
            return ResponseEntity.ok("Invalid task number");
        }
    }
    @PostMapping("/fireEmployee")
    public ResponseEntity<?> fireEmployee(@RequestParam(required = true)Long taskNumber,
                                          @RequestParam(required = true)String email) {
        Task task1 = taskRepo.findTaskById(taskNumber);
        if(task1 != null)
        {
            if(!task1.getStatus().equals("N"))
                return ResponseEntity.ok("This task is not eligible for employee removal.");
            if(employeeRepo.findEmployeeByEmail(email) == null)
                return ResponseEntity.ok("There is no such employee.");
            List<Employee> task_employees = taskRepo.getTaskEmployees(taskNumber);
            Employee questionableEmployee = employeeRepo.findEmployeeByEmail(email);
            for (Employee e : task_employees)
                if(e.hashCode() == questionableEmployee.hashCode())
                    {
                        task1.fireEmployee(questionableEmployee);
                        taskRepo.save(task1);
                        return ResponseEntity.ok(questionableEmployee.getFullName() + " has been fired from task " + taskNumber + ".");
                    }
            return ResponseEntity.ok("There is no such employee hired for this task.");
        }
        else
        {
            return ResponseEntity.ok("Invalid task number");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestParam(required = true)String name,
                                        @RequestParam(required = false)Timestamp timestamp) //trqbva da moje da suzdade task za budeshteto
    {
        if(name == "")
            return ResponseEntity.ok("Please input a name.");
        Task task1 = taskRepo.findTaskByName(name);
        if(task1 == null)
        {
            task1 = taskRepo.save(new Task (name, null));
            //task1.setStart(new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok("Task number " + task1.getId() + " is saved.");
        }
        else
        {
            return ResponseEntity.ok("A task with this name already exists.");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeTask(@RequestParam(required = true)Long taskId)
    {
        Task task1 = taskRepo.findTaskById(taskId);
        if(task1 == null)
        {
            return ResponseEntity.ok("There is no such task.");
        }
        else
        {
            taskRepo.delete(task1);
            return ResponseEntity.ok("Task number " + task1.getId() + " was removed.");
        }
    }


    @GetMapping("/listEmployees")
    public List<Employee> GetAllEmployees(@RequestParam(required = true)Long taskNumber) {return taskRepo.getTaskEmployees(taskNumber);}

    @PostMapping("/addPacket")
    public ResponseEntity<?> addPacket(@RequestParam(required = true)Long packetId,
                                       @RequestParam(required = true)Long taskId)
    {
        Packet packet1 = packetRepo.findPacketById(packetId);
        if(packet1 == null)
            return ResponseEntity.ok("There is no such packet.");

        Task task1 = taskRepo.findTaskById(taskId);
        if(task1 != null)
        {
            if(!task1.getStatus().equals("N"))
                return ResponseEntity.ok("This task is not eligible for packet addition.");
            List<Packet> task_packets = taskRepo.getTaskPackets(taskId);
            for(Packet p : task_packets)
            {
                if(p.hashCode() == packet1.hashCode())
                    return ResponseEntity.ok("This packet is already included in task " + taskId + ".");
            }
            if(task1.getCity() == packet1.getCity() || task1.getCity() == null)
            {
                task1.includePacket(packet1);
                taskRepo.save(task1);
                return ResponseEntity.ok(packet1.getName() + " has been included in task " + taskId + ".");
            }
            else
                return ResponseEntity.ok("This packet is in different city. It should first be transfered to a warehouse in the same city as the task.");

        }
        else
        {
            return ResponseEntity.ok("Invalid task number");
        }
    }

    @PostMapping("/removePacket")
    public ResponseEntity<?> removePacket(@RequestParam(required = true)Long packetId,
                                          @RequestParam(required = true)Long taskId) {
        Task task1 = taskRepo.findTaskById(taskId);
        if(task1 != null)
        {
            if(!task1.getStatus().equals("N"))
                return ResponseEntity.ok("This task is not eligible for packet removal.");
            List<Packet> task_packets = taskRepo.getTaskPackets(taskId);
            if(packetRepo.findPacketById(packetId) == null)
                return ResponseEntity.ok("Such packet does not exist.");
            Packet questionablePacket = packetRepo.findPacketById(packetId);
            for(Packet p : task_packets)
                if(p.hashCode() == questionablePacket.hashCode())
                {
                    task1.excludePacket(questionablePacket);
                    taskRepo.save(task1);
                    return ResponseEntity.ok(questionablePacket.getName() + " has been excluded from task " + taskId + ".");
                }
            return ResponseEntity.ok("There is no such packet in this task.");
        }
        else
        {
            return ResponseEntity.ok("Invalid task number");
        }
    }

    @GetMapping("/getEmployeeTasks")
    private List<Task> getEmployeeTasks(@RequestParam(required = true)String email)
    {
        List<Task> tasks = taskRepo.getEmployeeTasks(email);
        tasks.forEach(Task -> {Task.setFullStatusName(statusMap.get(Task.getStatus()));});
        return tasks;
    }
    @GetMapping("/getEmployeeFinishedTasks")
    private List<Task> getEmployeeFinishedTasks(@RequestParam(required = true)String email)
    {
        List<Task> tasks = taskRepo.getEmployeeFinishedTasks(email);
        tasks.forEach(Task -> {Task.setFullStatusName(statusMap.get(Task.getStatus()));});
        return tasks;
    }

    @PostMapping("/actualizeTasks")
    public void actualizeTaskInfo()
    {
        for(Task task1: taskRepo.findAll())
        {
            List<Packet> task_packets = taskRepo.getTaskPackets(task1.getId());
            int task_employees_count = taskRepo.getTaskEmployeesCount(task1.getId());

            Double taskWeight = 0.0;
            for(Packet p:task_packets)
                taskWeight += p.getWeight();

            if(task1.getStatus().equals("N") && task_employees_count > 0 && task_packets.size() > 0)
            {
                task1.setStatus("I");
                task1.setStart(new Timestamp(System.currentTimeMillis()));

                Double taskWorkingPower = 0.0;
                taskWorkingPower = task_employees_count * 2.69; // hardcoded, Да кажем, че един работник обработва 2.69т работа за час

                Double hoursWork = 0.0;
                hoursWork = taskWeight/taskWorkingPower;

                long s = task1.getStart().getTime();
                Double m = hoursWork * 60 * 60 * 1000; // * 60 min * 60 sec * 1000 milisec
                long m1 = m.longValue();
                task1.setExpectedFinish(new Timestamp(s + m1));
                taskRepo.save(task1);
            }
            else if(task1.getStatus().equals("I"))
            {
                long start = task1.getStart().getTime();
                long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
                long finish = task1.getExpectedFinish().getTime();
                if(currentTime > finish)
                {
                    task1.setStatus("F");
                    List<Employee> employeesForFiring = new ArrayList(){};
                    for(Employee e : task1.getEmployees())
                    {
                        finishedRepo.save(new finished_task_employees(task1.getId(), e.getId()));
                        employeesForFiring.add(e);
                    }
                    for(Employee e : employeesForFiring)
                        task1.fireEmployee(e);
                }
                else
                {
                    Double finishedWeightPercent = Double.valueOf(currentTime-start)/Double.valueOf(finish-start);
                    Double remainingWeight = (1 - finishedWeightPercent) * taskWeight;

                    Double hoursWork = 0.0;
                    hoursWork = remainingWeight/(task_employees_count * 2.69);
                    Double m1 = hoursWork *60*60*1000;
                    task1.setExpectedFinish(new Timestamp(currentTime + m1.longValue()));
                }
                taskRepo.save(task1);
            }
        }
    }
}
