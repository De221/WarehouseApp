package bg.DNDWarehouse.warehouseApp.repositories;

import bg.DNDWarehouse.warehouseApp.entities.Packet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PacketRepository extends JpaRepository<Packet, Long> {

    Packet findPacketById(Long id);
    Packet findPacketByName(String name);

    @Query("SELECT COUNT (name) " +
            "FROM Packet " +
            "WHERE EXISTS " +
            "(SELECT t_p " +
            "FROM Task t " +
            "JOIN t.packets t_p " +
            "WHERE t_p.name =:name)")
    int isCurrentlyInTask(String name); //ne raboti mnogo pravilno - 0 ako ne e busy i tasks^2 ako e

}
