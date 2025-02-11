package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamDTO {

    private String masp;
    private Integer tenSanPham;
    private Integer danhMucId;
    private Integer thuongHieuId;
    private Integer chatLieuId;
    private Integer nhaSanXuatId;
    private Integer coAoId;

    private List<SanPhamChiTietDTO> chiTietSanPham;

    @Override
    public String toString() {
        return "SanPhamDTO{" +
                "masp='" + masp + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", danhMucId=" + danhMucId +
                ", thuongHieuId=" + thuongHieuId +
                ", chatLieuId=" + chatLieuId +
                ", nhaSanXuatId=" + nhaSanXuatId +
                ", coAoId=" + coAoId +
                ", chiTietSanPham=" + chiTietSanPham +
                '}';
    }


}

