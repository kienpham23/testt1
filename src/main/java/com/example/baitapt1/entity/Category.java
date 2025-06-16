package com.example.baitapt1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name= "category")
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="name", length = 100 )
    private String name;

    @Column(name="category_code", unique = true,length=50, nullable = false)
    private String categorycode;

    @Column(name="description", length = 200)
    private String description;

    @Column(name="status")
    private String status;

    @CreationTimestamp
    @Column(name="create_date", updatable= false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name="modified_date")
    private LocalDateTime modifiedDate;

    @Column(name="created_by",length = 100)
    private String createdBy;

    @Column(name="modified_by",length = 100)
    private String modifiedBy;

    @OneToMany(mappedBy= "category",fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
            CascadeType.MERGE})
    private List<ProductCategory> productCategory;
    @OneToMany(mappedBy= "category", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
            CascadeType.MERGE})
    private List<CategoryImage> categoryImage;
    @PrePersist
    public void prePersist(){
        if(status == null){
            status = "1";
        }

    }
    public Category(Long id) {
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategorycode() {
        return categorycode;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategorycode(String categorycode) {
        this.categorycode = categorycode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
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

    public List<CategoryImage> getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(List<CategoryImage> categoryImage) {
        this.categoryImage = categoryImage;
    }
}
