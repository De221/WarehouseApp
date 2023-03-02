package bg.DNDWarehouse.warehouseApp.payload.request;

import bg.DNDWarehouse.warehouseApp.entities.Warehouse;

public class PacketRequest {
    private String name;
    private Double weight;
    private Warehouse warehouse;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Long getWarehouseId() {
        return warehouse.getId();
    }
}
