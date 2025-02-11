package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonCTDTO{
    private Integer id;
    private Integer idSanPhamCT;
    private String maspct;
    private Integer idHoadon;
    private String mahdct;
    private BigDecimal donGia;
    private Integer soLuong;
    private Integer trangThai;
    private BigDecimal thanhTien;
    private String hinhthucthanhtoan;
}
