package com.example.demo.baove.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDTO {
    private int id;
    private String username;
    private String email;
    private boolean trangThai;
    private int roleId;
    private String roleName;
    private LocalDate ngayTao;
    private String avatar;
}
