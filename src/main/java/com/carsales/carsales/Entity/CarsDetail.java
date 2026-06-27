package com.carsales.carsales.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "CarDetails")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarsDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private String company;
    private String model;
    private String trim;
    private String body;
    private String transmission;
    private String vin;
    private String state;
    @Column(name = "car_condition")
    private Integer carCondition;
    private Integer odometer;
    private String color;
    private String interior;
    private String seller;
    private Integer mmr;
    private Float sellingPrice;


}
