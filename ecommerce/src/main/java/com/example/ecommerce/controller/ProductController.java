package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService ps;

    @PostMapping
    public Product create(@Valid @RequestBody Product p) {
        return ps.create(p);
    }

    @GetMapping
    public List<Product> all() {
        return ps.all();
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String q) {
        return ps.search(q);
    }
}
