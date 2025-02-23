package com.example.demo.baove.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@NoArgsConstructor
public class nhanviendto {
private int id ;
public String ma;
private String hovaten;
private String gioitinh;
private String diachi;
private String tenchucvu;


    public nhanviendto(int id, String ma, String hovaten, String gioitinh, String diachi, String tenchucvu) {
        this.id = id;
        this.ma = ma;
        this.hovaten = hovaten;
        this.gioitinh = gioitinh;
        this.diachi = diachi;
        this.tenchucvu = tenchucvu;
    }
}
