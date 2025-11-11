package com.yallatawsil.backend.entity ;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @NotBlank
    private String name ;

    @NotBlank
    private String address ;

    @NotNull
    private Double longitude ;

    @NotNull
    private Double latitude ;

    @NotNull
    private String preferredTimeSlot ;

    // Relation between customer and delivery
    @OneToMany(mappedBy = "costumer", cascade = CascadeType.ALL)
    private List<Delivery> deliveries = new ArrayList<>();

    @OneToMany(mappedBy = "costumer")
    private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

    @Column(updatable = false)
    private LocalDate createdAt ;
    private LocalDate updatedAt ;

    @PrePersist
    public void create(){
        this.createdAt = LocalDate.now() ;
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    public void update(){
        this.updatedAt = LocalDate.now();
    }
}