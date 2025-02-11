package com.example.demo.entity;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietDTO {
    private String mauSac; // Màu sắc (dạng tên, ví dụ: "Đỏ", "Xanh")
    private String kichThuoc; // Kích thước (dạng tên, ví dụ: "S", "M", "L")
    private Double gia; // Giá sản phẩm
    private Integer soLuong; // Số lượng sản phẩm
    private Integer idSanPham; // ID của sản phẩm

    private byte[] images; // ID của sản phẩm
    @Override
    public String toString() {
        return "SanPhamChiTietDTO{" +
                "mauSac='" + mauSac + '\'' +
                ", kichThuoc='" + kichThuoc + '\'' +
                ", gia=" + gia +
                ", soLuong=" + soLuong +
                ", idSanPham=" + idSanPham +
                ", images=" + images +
                '}';
    }
}





