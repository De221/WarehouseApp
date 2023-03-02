package bg.DNDWarehouse.warehouseApp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    private int storage_space;

    @OneToMany(mappedBy = "warehouse")
    private Set<Packet> packets;

    public Warehouse(City city, int storage_space) {
        this.city = city;
        this.storage_space = storage_space;
    }

    public Warehouse(){};

    public Long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getStorage_space() {
        return storage_space;
    }

    public void setStorage_space(int storage_space) {
        this.storage_space = storage_space;
    }

    public String getCityName() {return city.getName();}
}
