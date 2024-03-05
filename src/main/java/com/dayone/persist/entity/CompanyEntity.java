package com.dayone.persist.entity;

import com.dayone.model.Company;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
