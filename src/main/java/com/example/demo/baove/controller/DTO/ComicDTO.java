package com.example.demo.baove.controller.DTO;

import com.example.demo.baove.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.util.List;

public class ComicDTO {
    private Integer id;
    private String tenTruyen;
    private String moTa;
    private String ghiChu;
    private String imageComic;
    private Integer luotXem;
    private Integer luotThich;
    private LocalDate ngayTao;
    private LocalDate ngaySua;
    private List<Integer> categoryIds;
    private List<Integer> authorIds;
    private User user;



    public void setLuotThich(Integer luotThich) {
        this.luotThich = luotThich;
    }

    public Integer getLuotThich() {
        return luotThich;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getImageComic() {
        return imageComic;
    }

    public void setImageComic(String imageComic) {
        this.imageComic = imageComic;
    }

    public Integer getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(Integer luotXem) {
        this.luotXem = luotXem;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDate getNgaySua() {
        return ngaySua;
    }

    public void setNgaySua(LocalDate ngaySua) {
        this.ngaySua = ngaySua;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Integer> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Integer> authorIds) {
        this.authorIds = authorIds;
    }
}
