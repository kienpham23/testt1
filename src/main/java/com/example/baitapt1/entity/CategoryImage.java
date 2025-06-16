package com.example.baitapt1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="category_image")
public class CategoryImage {
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
    @Column(name="update_date")
    private LocalDateTime updateDate;
    @Column(name="create_by",length = 100)
    private String createBy;
    @Column(name="modified_by",length = 100)
    private String modifiedBy;
    @Column(name="status",length = 1)
    private String status;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id")
    @JsonIgnore
    private Category category;
    @Column(name = "category_id",insertable = false, updatable = false)
    private Long categoryId;
    @PrePersist
    public void prePersist() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
    }



}
