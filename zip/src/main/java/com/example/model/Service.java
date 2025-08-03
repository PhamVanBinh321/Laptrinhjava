package com.example.model;


import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String intro; // Dòng giới thiệu ngắn

    @Lob
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private Double price;

    @Column(nullable = false)
    private Integer duration; // Thời gian thực hiện (phút, giờ...)

    @Column(length = 255)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status = Status.ACTIVE;

    // Enum cho status
    public enum Status {
        ACTIVE, INACTIVE
    }

    // Constructors
    public Service() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
