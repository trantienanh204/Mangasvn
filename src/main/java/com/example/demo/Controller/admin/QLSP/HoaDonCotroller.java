package com.example.demo.Controller.admin.QLSP;

import com.example.demo.entity.HoaDon;
import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.respository.*;
import com.example.demo.service.HoaDonService;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/panda/hoadon")
public class HoaDonCotroller {

    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    nhanvienRepository nhanvien;
    @Autowired
    KhachHangRepository khachHangRepository;

    @Autowired
    private TemplateEngine templateEngine; // Thymeleaf engine

    @ModelAttribute("listnhanvien")
    List<NhanVien> getnv (){return nhanvien.findAll();}

    @ModelAttribute("listkhachhang")
    List<KhachHang> getkh (){return khachHangRepository.findAll();}

    @GetMapping
    public String hienthi(@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "mahd", required = false) String mahd,
                          @RequestParam(value = "nv", required = false) String nv,
                          @RequestParam(value = "tenkh", required = false) String tenkh,
                          @RequestParam(value = "trangThai", required = false) Integer trangThai,
                          Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        if (page < 0) {
            page = 0;
        }
        Page<HoaDon> listHD = hoaDonService.hienThiHD(page,mahd, nv , tenkh, trangThai);
        model.addAttribute("totalPage", listHD.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("lshd",listHD.getContent());
        model.addAttribute("mahd", mahd);
        model.addAttribute("nv", nv);
        model.addAttribute("tenkh", tenkh);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("pageSize", listHD.getSize());
        List<HoaDonCT> listhdct = hoaDonCTRepository.findAll();
        model.addAttribute("listhdct",listhdct);
        return "/admin/HoaDon/HoaDon";
    }
    @GetMapping("/xuatfile")
    public String filePdf(@RequestParam("id") int id, Model model,
                          RedirectAttributes redirectAttributes) {
        List<HoaDonCT> lshdct  = hoaDonCTRepository.findhoadonct(id);
        HoaDon hoadon = hoaDonRepository.finid(lshdct.get(0).getHoaDon().getId());
        System.out.println("đã in ");
        if (hoadon.getLanin() == null) {
            hoadon.setLanin(0);
        }
        hoadon.setLanin(hoadon.getLanin() +1 );
        System.out.println("lần in " +hoadon.getLanin());
        
        hoaDonRepository.save(hoadon);
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatterngay= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTime = time.format(formatter);
        String formatdate = date.format(formatterngay);
        model.addAttribute("hoadonct",lshdct);
        model.addAttribute("date",formatdate);
        model.addAttribute("time",formattedTime);
//        model.addAttribute("lshd",hoaDonRepository.findAll();

        String directoryPath = "D:\\HocTap\\HoaDon"; // Đường dẫn lưu PDF
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục nếu không tồn tại
        }
        String customFileName = "Hoadon_" + lshdct.get(0).getHoaDon().getMahoadon() + "_" +
        lshdct.get(0).getHoaDon().getKhachHang().getTenkhachhang()  + ".pdf"; // Tên file tùy chỉnh
        String filePath = directoryPath + "\\" + customFileName; // Đường dẫn đầy đủ đến file PDF

//        String filePath = directoryPath + "\\hoadon.pdf"; // Đường dẫn đầy đủ đến file PDF

        // Lấy dữ liệu hóa đơn từ SQL
        List<HoaDon> hd = hoaDonRepository.findAll();
        model.addAttribute("hd", hd);

        // Render HTML từ Thymeleaf template
        Context context = new Context();
        context.setVariables(model.asMap());
        String htmlContent = templateEngine.process("pdf", context);

        // Chuyển HTML thành PDF
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            HtmlConverter.convertToPdf(htmlContent, fos);
            return "pdf";
        } catch (IOException e) {
            e.printStackTrace();
            return "pdf"+ e.getMessage();
        }
    }

    @GetMapping("/chitiet")
    public String chiTietHoaDon(@RequestParam("id") Integer id, Model model) {
        List<HoaDonCT> hoaDonCT = hoaDonCTRepository.findhoadonct(id);
        HoaDon hd = hoaDonRepository.finid(id);
        model.addAttribute("hoaDonCTs", hoaDonCT);
        model.addAttribute("hd", hd.getMahoadon());
        model.addAttribute("hoadon", hd);
        HoaDon HD = hoaDonRepository.findById(id).orElse(null);
        model.addAttribute("giagiam",HD);
        System.out.println(hd.getMahoadon());
        return "/admin/HoaDon/HoaDon::hdct"; // Trả về fragment HTML
    }

    @GetMapping("update")
    public String formupdate(@RequestParam("id") int id,Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("hoadon",hoaDonRepository.findById(id).get());
        return "admin/HoaDon/UPDATE/UpdateHd";
    }

    @PostMapping("update")
    public String update(@ModelAttribute("hoadon")HoaDon hoaDon, Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        hoaDon.setTrangthai(1);
        hoaDonRepository.save(hoaDon);
        return "redirect:/panda/hoadon";
    }

    @GetMapping("import")
    public String importpdf(Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        List<HoaDonCT> lshdct  = hoaDonCTRepository.findhoadonct(1);
        model.addAttribute("hoadonct",lshdct);
        model.addAttribute("lshd",hoaDonRepository.findAll().get(0));
        System.out.println(lshdct .size());
        System.out.println(lshdct.get(0).getSanPhamChiTiet().getSanPham().getTensp());
        return "pdf";
    }



//        @ResponseBody
//        @GetMapping("testA")
//        public Map<String, Object> testA(
//                @RequestParam(value = "page", defaultValue = "0") int page,
//                @RequestParam(value = "size", defaultValue = "10") int size,
//                @RequestParam(value = "mahd", required = false) String mahd,
//                @RequestParam(value = "sdt", required = false) String sdt,
//                @RequestParam(value = "tenkh", required = false) String tenkh,
//                @RequestParam(value = "trangThai", required = false) Integer trangThai) {
//            Page<HoaDon> listHD = hoaDonService.hienThiHD(page, mahd, sdt, tenkh, trangThai);
//            Map<String, Object> response = new HashMap<>();
//            response.put("totalPages", listHD.getTotalPages());
//            response.put("content", listHD.getContent());
//
//            return response;
//        }



}
