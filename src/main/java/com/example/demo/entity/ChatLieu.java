package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHAT_LIEU")
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    @Column(name = "ID")
    private int id;
    @NotBlank(message = "Không được để trống dữ liệu")
    @Size(min = 1, max = 50,message = "Mã phải có độ dài từ 1 đến 50 ký tự")
    @Column(name = "MA_CHAT_LIEU")
    private String maChatLieu;
    @NotBlank(message = "Không được để trống dữ liệu")
    @Size(min = 1, max = 50,message = "Tên phải có độ dài từ 1 đến 100 ký tự")
    @Column(name = "TEN_CHAT_LIEU")
    private String tenChatLieu;
    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;
    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;
    @NotNull(message = "Trạng thái không được để null")
    @Column(name = "TRANG_THAI")
    private Integer trangThai;

}
