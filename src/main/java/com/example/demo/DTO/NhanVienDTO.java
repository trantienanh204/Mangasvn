package com.example.demo.DTO;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class NhanVienDTO {
    private Integer id;

    private String manhanvien;

    private String tennhanvien;

    private String matKhau;

    private boolean delete;

    private Integer trangthai = 1 ;

    private Boolean tinhtrang;

    private TaiKhoanDTO taiKhoanDTO;

    public NhanVienDTO(Integer id, String manhanvien, String tennhanvien) {
        this.id = id;
        this.manhanvien = manhanvien;
        this.tennhanvien = tennhanvien;
    }


    public NhanVienDTO(int id, String manhanvien, String tennhanvien, Boolean tinhtrang) {
        this.id = id;
        this.manhanvien = manhanvien;
        this.tennhanvien = tennhanvien;
        this.tinhtrang = tinhtrang;

    }
}
