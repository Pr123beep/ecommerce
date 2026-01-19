package com.example.ecommerce.service;

import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository pr;

    public Product create(Product p) {
        if (p.getStock() == null) p.setStock(0);
        return pr.save(p);
    }

    public List<Product> all() {
        return pr.findAll();
    }

    public Product get(String id) {
        return pr.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    public void reduceStock(String productId, int qty) {
        Product p = get(productId);
        if (p.getStock() < qty) throw new com.example.ecommerce.exception.BadRequestException("Insufficient stock for product: " + productId);
        p.setStock(p.getStock() - qty);
        pr.save(p);
    }
    public List<Product> search(String q) {
        return pr.findByNameContainingIgnoreCase(q);
    }

}
