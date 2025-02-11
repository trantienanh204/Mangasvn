package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaChi {
    private String name;
    private String code;
    private String divisionType;
    private String codename;
    private int phoneCode;
}
