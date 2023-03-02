package bg.DNDWarehouse.warehouseApp.controllers;

import bg.DNDWarehouse.warehouseApp.entities.Packet;
import bg.DNDWarehouse.warehouseApp.entities.Task;
import bg.DNDWarehouse.warehouseApp.entities.Warehouse;
import bg.DNDWarehouse.warehouseApp.payload.request.PacketRequest;
import bg.DNDWarehouse.warehouseApp.repositories.PacketRepository;
import bg.DNDWarehouse.warehouseApp.repositories.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/packet")
@CrossOrigin(origins = "*")
public class PacketController {

    private PacketRepository packetRepo;
    private WarehouseRepository warehouseRepo;
    PacketController(PacketRepository packetRepository, WarehouseRepository warehouseRepository)
    {
        packetRepo = packetRepository;
        warehouseRepo = warehouseRepository;
    }

    @GetMapping("/fetch")
    private List<Packet> getAllPackets()
    {
        return packetRepo.findAll();
    }

    @PostMapping("/receiveNew")
    public ResponseEntity<?> receivePacket(@RequestParam(required = true) String packetName,
                                          @RequestParam(required = true) Double weight,
                                          @RequestParam(required = true) Long warehouseId)
    {
        if (packetRepo.findPacketByName(packetName) == null)
        {
            if(warehouseRepo.findWarehouseById(warehouseId) != null)
            {
                Packet packet1 = new Packet(packetName,weight);
                packet1.setWarehouse(warehouseRepo.findWarehouseById(warehouseId));
                packetRepo.save(packet1);
                return ResponseEntity.ok(packetName + " appeared in warehouse" + warehouseId + ".");
            }
            return  ResponseEntity.ok("Invalid warehouse Id.");
        }
        return ResponseEntity.ok("There is already a packet with this name. Please enter another name.");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferPacket(@RequestParam(required = true)Long packetId,
                                            @RequestParam(required = true)Long warehouseId)
    {
        Packet packet1 = packetRepo.findPacketById(packetId);
        if(packet1 == null)
        {
            return ResponseEntity.ok("There is no such packet.");
        }
        else if(packet1.getWarehouseId() != warehouseId)
        {
            if(warehouseRepo.findWarehouseById(warehouseId) == null)
                return ResponseEntity.ok("Invalid warehouse Id.");
            packet1.setWarehouse(warehouseRepo.findWarehouseById(warehouseId));
            packetRepo.save(packet1);
            return ResponseEntity.ok(packet1.getName() + " has been transferred to warehouse " + warehouseId + ".");
        }
        else
        {
            return ResponseEntity.ok("This packet is already in warehouse "+ warehouseId + ".");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removePacket(@RequestParam(required = true)String  packetName)
    {
        Packet packet1 = packetRepo.findPacketByName(packetName);
        if(packet1 == null)
        {
            return ResponseEntity.ok("There is no such packet.");
        }
        else if(packetRepo.isCurrentlyInTask(packetName) > 0)
        {
            return ResponseEntity.ok("Exclude the packet from all tasks before deleting it. ");
        }
        else
        {
            packetRepo.delete(packet1);
            return ResponseEntity.ok("Packet " + packet1.getName() + " has been deleted.");
        }
    }
    @GetMapping("/fetchWarehouses")
    public List<Warehouse> getAllWarehouses() {return warehouseRepo.findAll();}
}
