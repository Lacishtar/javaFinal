package com.tdtu.JavaFn.Controller;

import com.tdtu.JavaFn.Classes.Product;
import com.tdtu.JavaFn.Classes.User;
import com.tdtu.JavaFn.Interface.ProductRepository;
import com.tdtu.JavaFn.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private UserService userService;
    private ProductRepository productRepository;

    @Autowired
    public ProductController(UserService userService, ProductRepository productRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public String showProductSearchForm(@RequestParam(name = "query", required = false) String query,
                                        @RequestParam(name = "username", required = false) String username,
                                        Model model) {
        User user = userService.findByUsername(username);
        if(user != null && !user.isPasswordChanged())
        {
            return "redirect:/change-password?username=" + username;
        }
        else if(user != null)
        {
            List<Product> searchResults;

            if (query != null && !query.isEmpty()) {
                searchResults = productRepository.findByProductNameContainingOrBarcode(query, query);
            } else {
                searchResults = (List<Product>) productRepository.findAll();
            }

            model.addAttribute("products", searchResults);
            return "products/search";
        }
        else {
            List<Product> products;
            products = (List<Product>) productRepository.findAll();
            model.addAttribute("products", products);

            return "products/list";
        }
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        Product product = new Product();
        product.setCreationDate(LocalDate.now());
        model.addAttribute("product", product);
        return "products/add";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product) {
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    @GetMapping("/products/update/{id}")
    public String showUpdateProductForm(@PathVariable int id, Model model) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            model.addAttribute("product", product);
            return "products/update";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable int id, @ModelAttribute Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setBarcode(updatedProduct.getBarcode());
            existingProduct.setProductName(updatedProduct.getProductName());
            existingProduct.setImportPrice(updatedProduct.getImportPrice());
            existingProduct.setRetailPrice(updatedProduct.getRetailPrice());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setCreationDate(updatedProduct.getCreationDate());
            productRepository.save(existingProduct);
        }
        return "redirect:/products";
    }
}
