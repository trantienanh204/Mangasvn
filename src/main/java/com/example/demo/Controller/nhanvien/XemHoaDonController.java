package com.example.demo.Controller.nhanvien;

import com.example.demo.DTO.NhanVienDTO;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.HoaDonCT;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.NhanVien;
import com.example.demo.respository.HoaDonCTRepository;
import com.example.demo.respository.HoaDonRepository;
import com.example.demo.service.HoaDonService;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/panda/nhanvien/xemhoadon")
public class XemHoaDonController {
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HoaDonCTRepository hoaDonCTRepository;
    @Autowired
    private TemplateEngine templateEngine;
    @GetMapping("/hienthi")
    public String hienthi(@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "mahd", required = false) String mahd,
                          @RequestParam(value = "nv", required = false) String nv,
                          @RequestParam(value = "tenkh", required = false) String tenkh,
                          @RequestParam(value = "trangThai", required = false) Integer trangThai,
                          Model model){
        System.out.println("mahd: " + mahd);
        System.out.println("tenkh: " + tenkh);
        System.out.println("trangThai: " + trangThai);

        String role = "nhanvien"; //Hoặc lấy giá trị role từ session hoặc service
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
        return "/nhanvien/XemHoaDon";
    }

    @GetMapping("xuatfile")
    public String filePdf(@RequestParam("id") int id, Model model) {

        List<HoaDonCT> lshdct  = hoaDonCTRepository.findhoadonct(id);

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatterngay= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedTime = time.format(formatter);
        String formatdate = date.format(formatterngay);
        model.addAttribute("date",formatdate);
        model.addAttribute("time",formattedTime);

        HoaDon hoadon = hoaDonRepository.finid(lshdct.get(0).getHoaDon().getId());
        System.out.println("đã in ");
        if (hoadon.getLanin() == null) {
            hoadon.setLanin(0);
        }
        hoadon.setLanin(hoadon.getLanin() +1 );
        System.out.println("lần in " +hoadon.getLanin());
        hoaDonRepository.save(hoadon);

        model.addAttribute("hoadonct",lshdct);
//        model.addAttribute("lshd",hoaDonRepository.findAll();
        String directoryPath = "D:\\HocTap\\HoaDon"; // Đường dẫn lưu PDF
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục nếu không tồn tại
        }
        String customFileName = "hoadon_" + lshdct.get(0).getHoaDon().getMahoadon() + "_" +
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
        model.addAttribute("hoaDonCTs", hoaDonCT);

        HoaDon HD = hoaDonRepository.findById(id).orElse(null);
        System.out.println("giá giảm : "+HD.getGiagiam());
        model.addAttribute("giagiam",HD);
//        HoaDon hoaDon = hoaDonRepository.finid(15);
//        model.addAttribute("hoaDon", hoaDon);

        return "/nhanvien/XemHoaDon :: chiTiet"; // Trả về fragment HTML
    }
    private NhanVien mapToNhanvien(NhanVienDTO dto) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setId(dto.getId());
        nhanVien.setManhanvien(dto.getManhanvien());
        nhanVien.setTennhanvien(dto.getTennhanvien());
        return nhanVien;
    }
}
