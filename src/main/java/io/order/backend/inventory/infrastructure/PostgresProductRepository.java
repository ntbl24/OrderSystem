package io.order.backend.inventory.infrastructure;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository 
public class PostgresProductRepository implements ProductRepository{

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresProductRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public int decreaseStock(Long productId, int quantity){
        String sql = """
                UPDATE products
                SET stock = stock - :quantity
                WHERE id = :productId
                AND stock >= :quantity;
                """;

        return jdbcTemplate.update(sql, Map.of("productId", productId, "quantity", quantity));
    }
}
