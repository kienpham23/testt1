package com.example.baitapt1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name="product_image")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name")
    private String name;
    @Lob
    @Column(name="image")
    private byte[] image;
    @Column(name= "atltext",length = 200)
    private String atltext;
    @Column(name = "url")
    private String url;
    @Column(name ="uuid")
    private String uuid;
    @CreationTimestamp
    @Column(name="create_date",updatable = false)
    private LocalDateTime createDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;
    @Column(name="create_by",length = 100)
    private String createBy;
    @Column(name="modified_by",length = 100)
    private String modifiedBy;
    @ManyToOne
    @JoinColumn(name="product_id" )
    private Product product;
    @Column(name="status",length = 1)
    private String status;


    public void setModifiedDate(LocalDateTime now) {
    }

    public void setCreatedDate(LocalDateTime now) {
    }

    public void setCreatedBy(String updatedBy) {
    }
}

