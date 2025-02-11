package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CO_AO")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CoAo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    @Column(name = "ID")
    private Integer id;
    @NotBlank(message = "Không được để trống dữ liệu")
    @Size(min = 1, max = 50,message = "Mã phải có độ dài từ 1 đến 50 ký tự")
    @Column(name = "MA_CO_AO")
    private String ma;
    @NotBlank(message = "Không được để trống dữ liệu")
    @Size(min = 1, max = 50,message = "Tên phải có độ dài từ 1 đến 100 ký tự")
    @Column(name = "TEN_CO_AO")
    private String ten;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;
    @NotNull(message = "Trạng thái không được để null")
    @Column(name = "TRANG_THAI")
    private Integer trangThai;
//    public void toggleTrangThai() {
//        this.trangThai = !this.trangThai; // Đảo ngược giá trị
//    }
}
