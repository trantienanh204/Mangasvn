package com.example.demo.Controller.admin.QLTK;

import com.example.demo.entity.ChiTietVaiTro;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.entity.VaiTro;
import com.example.demo.respository.*;
import com.example.demo.service.EmailService;
import com.example.demo.services.NhanVienService;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/panda")
public class TKNhanVienController {
    @Autowired
    NhanVienService nhanVienService;
    @Autowired
    TaiKhoanRepo taiKhoanRepo;
    @Autowired
    VaiTroRepo vaiTroRepo;
    @Autowired
    ChiTietVaiTroRepo chiTietVaiTroRepo;
    @Autowired
    NhanVienRespository nhanVienRespository;
    @Autowired
    EmailService emailService;
    @Autowired
    TaiKhoanRepository taiKhoanRepository;

    @GetMapping("/tknhanvien")
    public String tknhanvien(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "manv", required = false) String manv,
                             @RequestParam(value = "tennv", required = false) String tennv,
                             @RequestParam(value = "trangThai", required = false) Integer trangThai,
                             Model model) {
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        if (page < 0) {
            page = 0;
        }
        Page<NhanVien> listNV = nhanVienService.hienThiNV(page, manv , tennv, trangThai);
        model.addAttribute("totalPage", listNV.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("list", listNV.getContent());
        model.addAttribute("manv", manv);
        model.addAttribute("tennv", tennv);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("pageSize", listNV.getSize());
        return "/admin/QLTK/TKNhanVien";
    }
    @GetMapping("/tknhanvien/create")
    public String showFormSave(Model model){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("nhanVien",new NhanVien());
        return "admin/QLTK/ADD/AddTKNhanVien";
    }
    @PostMapping("/tknhanvien/create")
    public String saveSafftoDB(Model model,
                               @Valid @ModelAttribute NhanVien nhanVien,
                               BindingResult result,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        String role = "admin"; // Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        // check lỗi
        boolean hasErrors = false;

        // Kiểm tra lỗi trong BindingResult
        if (result.hasErrors()) {
            return "admin/QLTK/ADD/AddTKNhanVien";
        }
        // check mail tồn tại;
        if (taiKhoanRepository.existsByTenDangNhap(nhanVien.getTentaikhoan())) {
            model.addAttribute("emailExist","Email đã được đăng ký");
            hasErrors = true;
        }
        // Kiểm tra mã nhân viên đã tồn tại
        if (nhanVienService.existsNhanVienByManhanvien(nhanVien.getManhanvien())) {
            model.addAttribute("errorma", "Mã đã tồn tại");
            return "admin/QLTK/ADD/AddTKNhanVien";
        }
        // check email trống
        if(nhanVien.getTentaikhoan().trim().isEmpty()){
            model.addAttribute("emailEmpty","Vui lòng nhập email");
            hasErrors = true;
        }
        // check định dạng email
        if(!nhanVien.getTentaikhoan().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")){
            model.addAttribute("emailFormat","Email không hợp lệ !");
            hasErrors = true;
        }
        // trả về trang nếu lỗi
        if (hasErrors) {
            return "admin/QLTK/ADD/AddTKNhanVien";
        }


        try {
            // Tạo mật khẩu ngẫu nhiên
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
            int passwordLength = 8 + new SecureRandom().nextInt(7); // 8 đến 14
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder(passwordLength);

            for (int i = 0; i < passwordLength; i++) {
                int index = random.nextInt(characters.length());
                password.append(characters.charAt(index));
            }
            // Lưu mật khẩu chưa băm để gửi qua email
            String plainPassword = password.toString();
            // Băm mật khẩu
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            // lưa mk đã băm vào đb
            nhanVien.setMatkhau(hashedPassword);
            // Thiết lập ngày tạo và ngày sửa
            nhanVien.setNgaytao(LocalDate.now());
            nhanVien.setTrangthai(1); // Nếu cần thiết
            // Gửi email thông báo
            String subject = "Chào mừng nhân viên mới" + " " + nhanVien.getTennhanvien() + " " + "gia nhập shop Panda Shirt !";

            String body = "<!DOCTYPE html>" +
                    "<html lang='vi'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                    ".container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }" +
                    "h2 { text-align: center; color: #333; }" +
                    "p { color: #555; line-height: 1.6; }" +
                    ".password { font-size: 24px; font-weight: bold; color: #007bff; text-align: center; margin: 20px 0; padding: 10px; border: 1px solid #007bff; border-radius: 5px; background-color: #e7f3ff; }" +
                    ".footer { margin-top: 20px; text-align: center; color: #555; font-size: 12px; }" +
                    ".button { display: inline-block; margin: 20px auto; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }" +
                    ".button:hover { background-color: #0056b3; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2>Chào mừng bạn gia nhập shop Panda Shirt!</h2>" +
                    "<p>Mật khẩu của bạn đã được tạo thành công. Vui lòng ghi nhớ nó!</p>" +
                    "<div class='password'>" + plainPassword + "</div>" +
                    "<p>Bạn có thể đăng nhập vào tài khoản của mình tại <a href='http://localhost:8080/panda/login' style='color: #007bff;'>đây</a>.</p>" +
                    "<div class='footer'>Nếu bạn có bất kỳ câu hỏi nào, hãy liên hệ với bộ phận hỗ trợ của chúng tôi.</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            emailService.sendEmail(nhanVien.getTentaikhoan(), subject, body); // Gửi email

            TaiKhoan tk = new TaiKhoan();
            ChiTietVaiTro ctvt = new ChiTietVaiTro();

            String tentk = nhanVien.getTentaikhoan();
            // Lưu nhân viên vào cơ sở dữ liệu
            int vaitro = 1;
            if(nhanVien.getChucvu().equals("Nhân viên")){
                vaitro = 2;
            }
            tk.setMatKhau(hashedPassword);
            tk.setTenDangNhap(tentk);
            taiKhoanRepo.save(tk);
            VaiTro vt = vaiTroRepo.findById(vaitro).get();
            TaiKhoan tenDangNhap = taiKhoanRepo.findByTenDangNhap(tentk);
            System.out.println("vaitro " +vt.getTenVaiTro());
            System.out.println("tên " +tenDangNhap.getTenDangNhap());
            System.out.println("tên " +tentk);
            ctvt.setVaiTro(vt);
            ctvt.setTaiKhoan(tenDangNhap);
            chiTietVaiTroRepo.save(ctvt);

            nhanVien.setTinhtrang(true);
            nhanVien.setTaiKhoan(tenDangNhap);
            nhanVienService.saveSafftoDbCreate(file, nhanVien);
            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("saveMassage", "Thêm nhân viên thành công");
            return "redirect:/panda/tknhanvien";
        } catch (Exception e) {
            // Xử lý lỗi khi lưu dữ liệu
            System.out.println("Lỗi :" + e.getMessage());
            return "admin/QLTK/ADD/AddTKNhanVien";
        }
    }

    @GetMapping("/hinh-anh/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        Optional<NhanVien> nhanVienOptional = Optional.ofNullable(nhanVienService.findById(id));
        if (nhanVienOptional.isPresent()) {
            byte[] image = nhanVienOptional.get().getImage();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(image.length);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // update
    @GetMapping("/tknhanvien/update")
    public String showFormUpdate(Model model,@RequestParam("id")int id, @ModelAttribute NhanVien nhanVien){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        model.addAttribute("nhanVien",new NhanVien());
        model.addAttribute("nhanVien",nhanVienService.findById(id));
        return "admin/QLTK/UPDATE/UpdateTKNhanVien";
    }
    @PostMapping("/tknhanvien/update")
    public String update(Model model,@Valid @ModelAttribute NhanVien nhanVien, BindingResult result, RedirectAttributes redirectAttributes, @RequestParam("file")MultipartFile file){
        String role = "admin"; //Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);
        // check validation

        boolean hasErrors = false;

        if(result.hasErrors()){
            return "/admin/QLTK/UPDATE/UpdateTKNhanVien";
        }
//        if (taiKhoanRepository.existsByTenDangNhap(nhanVien.getTentaikhoan())) {
//            model.addAttribute("emailExist","Email này đã được đăng ký");
//            hasErrors = true;
//        }
        // check mail trống update
        if (nhanVien.getTentaikhoan().trim().isEmpty()){
            model.addAttribute("emailEmpty","Vui lòng nhập email");
            hasErrors = true;
        }
        // check định dạng email
        if(!nhanVien.getTentaikhoan().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")){
            model.addAttribute("emailFormat","Email không hợp lệ");
            hasErrors = true;
        }
        // show lỗi
        if (hasErrors) {
            return "/admin/QLTK/UPDATE/UpdateTKNhanVien";
        }

        NhanVien checkNhanVien  = nhanVienService.findById(nhanVien.getId());
            // Cập nhật
        if(checkNhanVien !=null){
            checkNhanVien.setManhanvien(nhanVien.getManhanvien());
            checkNhanVien.setTennhanvien(nhanVien.getTennhanvien());
            checkNhanVien.setTentaikhoan(nhanVien.getTentaikhoan());
            checkNhanVien.setMatkhau(nhanVien.getMatkhau());
            checkNhanVien.setChucvu(nhanVien.getChucvu());
            checkNhanVien.setGioitinh(nhanVien.getGioitinh());
            checkNhanVien.setTrangthai(nhanVien.getTrangthai());
            nhanVienService.saveSafftoDb(file,nhanVien);
            redirectAttributes.addFlashAttribute("UpdateMassage","Cập nhật thành công !");
        }
        return "redirect:/panda/tknhanvien";
    }

    // thay đổi trạng thái
    @GetMapping("/tknhanvien/change")
    public String changeStatus(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes) {
        String role = "admin"; // Hoặc lấy giá trị role từ session hoặc service
        model.addAttribute("role", role);

        NhanVien nhanVien = nhanVienService.findById(id);
        if (nhanVien != null) {
            nhanVien.setTrangthai(nhanVien.getTrangthai() == 1 ? 0 : 1); // Đảo ngược trạng thái
            nhanVienRespository.save(nhanVien);
        }
        redirectAttributes.addFlashAttribute("ChangesStatusMessage", "Chuyển trạng thái thành công !");
        return "redirect:/panda/tknhanvien" ;
    }
}
