package com.example.demo.DTO;


import jakarta.persistence.Column;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {

    private Integer id;

    private String makhachhang;

    private String tenkhachhang;

    private String tenDangNhap;

    private String matKhau;

    private Boolean delete;

    private Boolean tinhtrang;
    private String sdt;
    private String diachi;


    public KhachHangDTO(Integer id, String makhachhang, String tenkhachhang, String tenDangNhap, String matKhau, Boolean delete,String diachi) {

        this.id = id;
        this.makhachhang = makhachhang;
        this.tenkhachhang= tenkhachhang;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.delete = delete;

        this.diachi = diachi;

    }
}
