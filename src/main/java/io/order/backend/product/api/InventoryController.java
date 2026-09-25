package io.order.backend.product.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.order.backend.product.application.ProductService;

@RestController 
@RequestMapping("api/inventory")
public class InventoryController {
    private final ProductService inventoryService;

    public InventoryController(ProductService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/decrease")
    public void decreaseStock(@PathVariable Long productId, @RequestParam int quantity){
        this.inventoryService.decreaseStock(productId, quantity);
    }

    
    
}
