package com.example.demo.baove.mapper;

import com.example.demo.baove.controller.DTO.theloaiDTO;
import com.example.demo.baove.entity.ComicDanhMuc;
import com.example.demo.baove.entity.DanhMuc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface mapperComic_danhmuc {
    @Mapping(source = "comics.id", target = "id")
    @Mapping(source = "comics.tenTruyen", target = "tenTruyen")
    @Mapping(source = "comics.moTa", target = "moTa")
    @Mapping(source = "comics.imageComic", target = "imageComic")
    @Mapping(source = "comics.luotXem", target = "luotXem")
    @Mapping(source = "comics.luotThich", target = "luotThich")
    @Mapping(source = "danhMuc.tenDanhMuc", target = "tenDanhMuc")
    theloaiDTO danhmuc_comicgigidoDto(ComicDanhMuc comicDanhMuc);
}

