package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.HoaDon;
import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.entity.SanPhamChiTietDTO;
import com.example.demo.respository.HoaDonCTRepository;
import com.example.demo.respository.HoaDonRepository;
import com.example.demo.respository.SanPhamChiTietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/panda/hoadonct")
public class HoaDonCTController {

    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    HoaDonRepository hoaDonRepository;

    @ModelAttribute("lsspct")
    public List<SanPhamChiTiet> getspct(){
        return sanPhamChiTietRepository.findAll();}
    @ModelAttribute("lshoadon")
    public List<HoaDon> gethd(){return  hoaDonRepository.findAll();}
//
//    @GetMapping("")
//    public String hienthi(@RequestParam("id") int id, Model model){
//        List<HoaDonCT> lshdct = hoaDonCTRepository.findhoadonct(id);
//        String mahd = "Danh sách hóa đơn chi tiết : " + lshdct.get(0).getHoaDon().getMahoadon();
//        model.addAttribute("lshdct",lshdct);
//        model.addAttribute("mahd",mahd);
//        System.out.println(lshdct.size());
//        return "admin/HoaDon/HDCT";
//    }
//        @GetMapping()
//        public List<HoaDonCT> getChiTietHoaDon(@RequestParam("id") int id) {
//            // Gọi service để lấy chi tiết hóa đơn dựa trên ID
//            List<HoaDonCT> lshdct = hoaDonCTRepository.findhoadonct(id);
//            System.out.println(lshdct.size());
//            returnid lshdct;
//        }
//
    @GetMapping
    public ResponseEntity<List<HoaDonCT>> gethdct(@RequestParam("id") int id ,Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        List<HoaDonCT> lshdct  = hoaDonCTRepository.findhoadonct(id);
        return ResponseEntity.ok(lshdct);
    }



    @GetMapping("update/{id}")
    public String formupdate(@PathVariable("id") int id, Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        HoaDonCT onehdct  = hoaDonCTRepository.findById(id).orElse(null);
        model.addAttribute("hoaDonCT",onehdct);
        return "admin/HoaDon/UPDATE/UpdateHdct";
    }
}
