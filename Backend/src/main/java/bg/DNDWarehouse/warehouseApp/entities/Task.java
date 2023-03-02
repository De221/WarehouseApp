package bg.DNDWarehouse.warehouseApp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
@JsonPropertyOrder({"id","name","fullStatusName","cityName","employees","packets","start","expectedFinish"})
public class Task {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JsonIgnore
    @Column(length = 1)
    private String status = "N";
    private Timestamp start;
    private Timestamp expected_finish;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_employee",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "task_packet",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "packet_id")
    )
    private Set<Packet> packets;

    @Transient private String fullStatusName;

    public Task(String name, Timestamp start) {
        this.name = name;
        this.start = start;
    }

    public Task(){};

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStart() {return start;}

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Date getExpectedFinish() {
        return expected_finish;
    }

    public void setExpectedFinish(Timestamp expected_finish) {
        this.expected_finish = expected_finish;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public String getFullStatusName() {
        return fullStatusName;
    }

    public void setFullStatusName(String fullStatusName) {
        this.fullStatusName = fullStatusName;
    }

    public void hireEmployee(Employee employee) {

        employees.add(employee);
    }

    public void fireEmployee(Employee employee) {

        employees.remove(employee);
    }

    public void includePacket(Packet packet) {

        packets.add(packet);
    }

    public void excludePacket(Packet packet) {

        packets.remove(packet);
    }

    public Set<Packet> getPackets() {
        return packets;
    }

    @JsonIgnore
    public City getCity() {
        if (!packets.isEmpty())
            return getPackets().iterator().next().getCity();
        else if (!employees.isEmpty())
            return getEmployees().iterator().next().getCity();
        else
            return null;
    }
    public String getCityName(){
        if (!packets.isEmpty())
            return getPackets().iterator().next().getCityName();
        else if (!employees.isEmpty())
            return getEmployees().iterator().next().getCityName();
        else
            return null;
    }
}
