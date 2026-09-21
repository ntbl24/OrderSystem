package io.order.backend.inventory.domain;

public class Product {
    private Long id;
    private String name;
    private double price;
    private long stock;

    public Product(Long id,String name,double price,long stock) {
        this.id=id;
        this.name=name;
        this.price=price;
        this.stock=stock;
    }

    public Long getId() { return id; }

    public String getProdName() { return name; }

    public double getPrice() { return price; }

    public long getStock() { return stock; }
    
}
