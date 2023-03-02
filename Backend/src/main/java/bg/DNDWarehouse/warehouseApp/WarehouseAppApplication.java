package bg.DNDWarehouse.warehouseApp;

import bg.DNDWarehouse.warehouseApp.repositories.EmployeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ServletComponentScan
@SpringBootApplication
public class WarehouseAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseAppApplication.class, args);
	}

}
