package com.example.demo.Controller;


import com.example.demo.service.SanPhamService;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/panda")
public class SideBarController {

    @Autowired
    private SanPhamService sanPhamService;


    @GetMapping("/hienthi")
    public String hienthi(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role); // Truyền role xuống view
        return "/admin/QLSP/SanPham";
    }
//    khi chưa code back-end thì chỉ có phần font-end
//    ta trả về một giao diện bth
//    nhưng nếu đã code back-end bên controller rồi
//    hì phải trả về theo kiểu như này:
//    "redirect:/thongke/hienthi" (Xem lại dự án xưởng nếu quên)

    //    QLSP===================

    @GetMapping("/sanpham")
    public String sanpham(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "/admin/QLSP/SanPham";
    }

    @GetMapping("/sanpham/spct")
    public String xemspct(Model model) {
        String role = "admin"; // Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "/admin/QLSP/xemspct"; // Trả về template
    }


    @GetMapping("/sanpham/add")
    public String ctsp(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "/admin/QLSP/CTSP";
    }
    @GetMapping("/mausac")
    public String mausac(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/mausac/hienthi";
    }

    @GetMapping("/kichthuoc")
    public String kichthuoc(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        return "redirect:/panda/kichthuoc/hienthi";
    }

    @GetMapping("/coao")
    public String coao(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/coao/hienthi";
    }

//    @GetMapping("/danhmuc")
//    public String danhmuc(Model model) {
//        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
//        model.addAttribute("role", role);
//        return "/admin/QLSP/DanhMuc";
//    }

    @GetMapping("/chatlieu")
    public String chatlieu(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/chatlieu/hienthi";
    }

//    @GetMapping("/nsx")
//    public String nsx(Model model) {
//        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
//        model.addAttribute("role", role);
//        return "/admin/QLSP/NSX";
//    } @GetMapping("/thuonghieu")
//    public String thuonghieu(Model model) {
//        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
//        model.addAttribute("role", role);
//        return "/admin/QLSP/ThuongHieu";
//    }


    //          QLGG ====================
    @GetMapping("/giamgia")
    public String giamgia(Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/voucher/hienthi";
    }
    //      QLTK ===================


//    @GetMapping("/hoadon")
//    public String hoadon(Model model) {
//        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
//        model.addAttribute("role", role);
//        return "/admin/HoaDon/HoaDon";
//    }
//    @GetMapping("/hdct")
//    public String hdct(Model model) {
//        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
//        model.addAttribute("role", role);
//        return "/admin/HoaDon/HDCT";
//    }

    //========================
    //Bán Hàng
    @GetMapping("/nhanvien/banhang")
    public String banhang(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/nhanvien/banhang/hienthi";
    }
    @GetMapping("/nhanvien/duyetdon")
    public String duyetdon(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/nhanvien/duyetdon/hienthi";
    }
    @GetMapping("/nhanvien/xemsanpham")
    public String xemsp(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/nhanvien/xemsanpham/hienthi";
    }
    @GetMapping("/nhanvien/tkkhachhang")
    public String xemctsp(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/nhanvien/tkkhachhang/hienthi";
    }
    @GetMapping("/nhanvien/xemhoadon")
    public String xemhd(Model model) {
        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        return "redirect:/panda/nhanvien/xemhoadon/hienthi";
    }
}
