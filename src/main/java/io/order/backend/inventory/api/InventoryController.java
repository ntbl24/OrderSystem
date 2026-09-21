package io.order.backend.inventory.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.order.backend.inventory.application.InventoryService;

@RestController 
@RequestMapping("api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/decrease")
    public void decreaseStock(@PathVariable Long productId, @RequestParam int quantity){
        inventoryService.decreaseStock(productId, quantity);
    }

    
    
}
