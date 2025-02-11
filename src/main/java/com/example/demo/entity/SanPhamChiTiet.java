package com.example.demo.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SAN_PHAM_CHI_TIET")

public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "MA_SPCT")
    private String maspct;

    @Column(name = "SO_LUONG_SAN_PHAM_CHI_TIET")
    private Integer soluongsanpham;



    @Column(name = "DON_GIA")
    private double dongia;

//    @Column(name = "MA_QR")
//    private byte[] qrspct;

    @Column(name = "MO_TA")
    private String mota;

    @Column(name = "NGAY_TAO")
    private LocalDate ngaytao;

    @Column(name = "NGAY_SUA")
    private LocalDate ngaysua;

    @Column(name = "TRANG_THAI")
    private boolean trangthai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_KICH_THUOC", referencedColumnName = "id")
    @JsonManagedReference // Hoặc không dùng gì nếu không cần vòng lặp
    private KichThuoc kichThuoc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_MAU_SAC", referencedColumnName = "id")
    @JsonManagedReference
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "ID_SAN_PHAM", referencedColumnName = "id")
    @JsonBackReference
    private SanPham sanPham;


//    @ManyToOne
//    @JoinColumn(name = "ID_KICH_THUOC")
//
//    private KichThuoc kichThuoc;
//
//    @ManyToOne
//    @JoinColumn(name = "ID_MAU_SAC")
//
//    private MauSac mauSac;


    public SanPhamChiTiet(Integer id) {
        this.id = id;
    }

    private transient String base64Image;

    public String getBase64Imagespct() {
        return base64Image;
    }

    public void setBase64Imagespct(String base64Image) {
        this.base64Image = base64Image;
    }

    @Column(name = "ANH_SAN_PHAM_CHI_TIET")
    private byte[] anhSanPhamChiTiet;

    public String FormatteddonGia() {
        try {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(dongia);
        } catch (NumberFormatException e) {
            return "Không hợp lệ"; //
        }
    }

    @Override
    public String toString() {
        return "SanPhamChiTiet{" +
                "id=" + id +
                ", soluongsanpham=" + soluongsanpham +
                ", dongia=" + dongia +
                ", mota='" + mota + '\'' +
                ", ngaytao=" + ngaytao +
                ", ngaysua=" + ngaysua +
                ", trangthai=" + trangthai +
//                ", sanPham=" + (sanPham != null ? sanPham.getId() : "null") +  // Hoặc lấy thuộc tính khác của SanPham
                ", kichThuoc=" + (kichThuoc != null ? kichThuoc.getId() : "null") +
                ", mauSac=" + (mauSac != null ? mauSac.getId() : "null") +
                '}';
    }

}
