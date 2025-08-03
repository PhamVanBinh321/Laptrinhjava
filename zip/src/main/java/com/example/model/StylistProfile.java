package com.example.model;


import jakarta.persistence.*;

@Entity
@Table(name = "stylist_profile")
public class StylistProfile {
    @Id
    @Column(name = "user_id")
    private Integer userId; // Khóa chính, cũng là khóa ngoại trỏ sang users.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Level level;

    @Column(length = 255)
    private String specialties;

    @Column(length = 255)
    private String intro; // Dòng giới thiệu ngắn

    @Lob
    private String bio;

    @Column(name = "served_customers")
    private Integer servedCustomers = 0;

    @Column(precision = 12, scale = 2)
    private Double salary = 0.0;

    @Column(name = "commission_percent", precision = 5, scale = 2)
    private Double commissionPercent = 10.00;

    // Enum cho level
    public enum Level {
        Junior, Senior, Master
    }

    // Constructors
    public StylistProfile() {}

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getServedCustomers() {
        return servedCustomers;
    }

    public void setServedCustomers(Integer servedCustomers) {
        this.servedCustomers = servedCustomers;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getCommissionPercent() {
        return commissionPercent;
    }

    public void setCommissionPercent(Double commissionPercent) {
        this.commissionPercent = commissionPercent;
    }
}
