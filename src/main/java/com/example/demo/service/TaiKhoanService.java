package com.example.demo.service;

import com.example.demo.DTO.*;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.respository.TaiKhoanRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public TaiKhoanDTO saveTk(TaiKhoanDTO taiKhoanDTO) {
        TaiKhoan taiKhoan = modelMapper.map(taiKhoanDTO, TaiKhoan.class);
        taiKhoan = taiKhoanRepo.save(taiKhoan);
        return modelMapper.map(taiKhoan, TaiKhoanDTO.class);
    }

    //dùng cho các dto
    public TaiKhoanDTO findByTenDangNhap(String tenDangNhap) {
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(tenDangNhap);
        System.out.println("Tìm kiếm tài khoản với tên đăng nhập: " + tenDangNhap); // Debug

        if (taiKhoan != null) {
            System.out.println("Tài khoản tồn tại: " + taiKhoan.getTenDangNhap());

            // Chuyển đổi thông tin nhân viên sang DTO nếu có
            NhanVien nhanVien = taiKhoan.getNhanVien();
            NhanVienDTO nhanVienDTO = nhanVien != null ? convertToDTO(nhanVien) : null;
            if (nhanVienDTO != null) {
                System.out.println("Họ tên nhân viên: " + nhanVienDTO.getTennhanvien());
            } else {
                System.out.println("Không tìm thấy nhân viên cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Chuyển đổi thông tin khách hàng sang DTO nếu có
            KhachHang khachHang = taiKhoan.getKhachHang();
            KhachHangDTO khachHangDTO = khachHang != null ? convertToKhachHangDTO(khachHang) : null;
            if (khachHangDTO != null) {
                System.out.println("Tên khách hàng: " + khachHangDTO.getTenkhachhang());
            } else {
                System.out.println("Không tìm thấy khách hàng cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Kiểm tra trạng thái
            if (nhanVienDTO == null && khachHangDTO == null) {
                throw new DisabledException("Tài khoản không hoạt động");
            }

            if (nhanVienDTO != null && nhanVienDTO.getTrangthai() == 0) {
                throw new DisabledException("Nhân viên không hoạt động");
            }

            if (khachHangDTO != null) {
                System.out.println("Trạng thái khách hàng: " + khachHangDTO.getDelete());
                if (!khachHangDTO.getDelete()) {
                    throw new DisabledException("Khách hàng không hoạt động");
                }
            }

            // Tạo TaiKhoanDTO với thông tin nhân viên và khách hàng (nếu có)
            return new TaiKhoanDTO(
                    taiKhoan.getTenDangNhap(),
                    taiKhoan.getMatKhau(),
                    nhanVienDTO, // Bao gồm thông tin nhân viên
                    khachHangDTO, // Bao gồm thông tin khách hàng
                    taiKhoan.getChiTietVaiTros().stream()
                            .map(chiTietVaiTro -> new ChiTietVaiTroDTO(
                                    chiTietVaiTro.getIdChiTietVaiTro(),
                                    new VaiTroDTO(chiTietVaiTro.getVaiTro().getIdVaiTro(),
                                            chiTietVaiTro.getVaiTro().getTenVaiTro())
                            ))
                            .collect(Collectors.toSet())
            );
        } else {
            System.out.println("Không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap);
            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + tenDangNhap);
        }
    }


    //convert to dto
    public NhanVienDTO convertToDTO(NhanVien nhanVien) {
        if (nhanVien == null) {
            return null;
        }

        return new NhanVienDTO(
                nhanVien.getId(),
                nhanVien.getManhanvien(),
                nhanVien.getTennhanvien(),
                nhanVien.getTinhtrang()
        );
    }

    //convert to dto khachHang
    public KhachHangDTO convertToKhachHangDTO(KhachHang khachHang) {
        if (khachHang == null) {
            return null;
        }
        return new KhachHangDTO(
                khachHang.getId(),
                khachHang.getMakhachhang(),
                khachHang.getTenkhachhang(),
                khachHang.getTaiKhoan().getTenDangNhap(),
                khachHang.getTaiKhoan().getMatKhau(),
                khachHang.isDelete(),
                khachHang.getDiachi()

        );
    }

    //dùng cho các trường DTO
    public TaiKhoanDTO getTaiKhoanTuSession() {
        // Lấy thông tin tài khoản từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                TaiKhoanDTO taiKhoanDTO = findByTenDangNhap(username); // Lấy TaiKhoanDTO từ tên đăng nhập

                if (taiKhoanDTO != null) {
                    return taiKhoanDTO; // Trả về thông tin tài khoản
                } else {
                    // Xử lý trường hợp không tìm thấy tài khoản
                    System.out.println("Không tìm thấy tài khoản cho tên đăng nhập: " + username);
                }
            }
        } else {
            System.out.println("Người dùng không được xác thực hoặc không có thông tin người dùng.");
        }

        return null; // Không có thông tin người dùng
    }

    //dùng cho các entity
    public TaiKhoan findByTenDanhNhap(String tenDangNhap) {
        // Tìm tài khoản theo tên đăng nhập
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(tenDangNhap);

        if (taiKhoan != null) {
            System.out.println("Tài khoản tồn tại: " + taiKhoan.getTenDangNhap());

            NhanVien nhanVien = taiKhoan.getNhanVien();

            if (nhanVien != null) {
                System.out.println("Họ tên: " + nhanVien.getTennhanvien());
            } else {
                System.out.println("Không tìm thấy nhân viên cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            KhachHang khachHang = taiKhoan.getKhachHang();
            if (khachHang != null){
                System.out.println("idKhachHang: "+khachHang.getId());
            }else{
                System.out.println("Không tìm thấy khách hàng cho tài khoản: " + taiKhoan.getTenDangNhap());
            }

            // Trả về đối tượng TaiKhoan
            return taiKhoan;
        } else {
            System.out.println("Không tìm thấy tài khoản với tên đăng nhập: " + tenDangNhap);
            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại: " + tenDangNhap);
        }
    }

    public TaiKhoan getTaiKhoanTuSession1() {
        // Lấy thông tin tài khoản từ SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findByTenDanhNhap(username); // Lấy TaiKhoan từ tên đăng nhập
        } else {
            return null; // Không có thông tin người dùng
        }
    }


}
