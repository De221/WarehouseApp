package bg.DNDWarehouse.warehouseApp.repositories;

import bg.DNDWarehouse.warehouseApp.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Warehouse findWarehouseById(Long id);

}
