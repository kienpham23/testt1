package com.example.baitapt1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="name",length = 100)
    private String name;

    @Column(name= "description",length = 200)
    private String description;

    @Column (name= "price")
    private double price;

    @Column (name ="product_code",length = 50, unique = true)
    private String  productCode;

    @Column (name ="quantity")
    private long quantity;

    @Column (name="status",length = 1)
    private String status;

    @CreationTimestamp
    @Column (name= "created_date", updatable= false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name="modified_date")
    private LocalDateTime modifiedDate;

    @Column(name="created_by", length = 100)
    private String createdBy;

    @Column(name="modified_by",length = 100)
    private String modifiedBy;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProductCategory> productCategory;

    @OneToMany(mappedBy="product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProductImage> productsimage = new ArrayList<ProductImage>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getProductCode() {
        return productCode;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<ProductCategory> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    public List<ProductImage> getProductsimage() {
        return productsimage;
    }

    public void setProductsimage(List<ProductImage> productsimage) {
        this.productsimage = productsimage;
    }
}
