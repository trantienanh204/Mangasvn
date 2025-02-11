package com.example.demo.DTO;

import java.math.BigDecimal;

public class ThanhTienDTO {
    private BigDecimal thanhTien;

    // Constructor
    public ThanhTienDTO(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    // Getter and Setter
    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}
