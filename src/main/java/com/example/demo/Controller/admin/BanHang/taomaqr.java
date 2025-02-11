package com.example.demo.Controller.admin.BanHang;

import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.SanPhamChiTietRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/panda/qrsp")
public class taomaqr {
    @Autowired
    SanPhamChiTietRepository sanPhamChiTietRepository;

    @GetMapping
    public String hienthi(Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepository.findAll(Sort.by(Sort.Order.desc("id")));
        model.addAttribute("spct", sanPhamChiTietList);
        return "admin/BanHang/qrsp";
    }

    @GetMapping("/lsspct")
    public String showProductDetails(@RequestParam("maspct") String maspct, Model model) {
        String role = "admin";
        model.addAttribute("role", role);
        SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaspct(maspct);
        model.addAttribute("lsspct", spct);
        return "admin/BanHang/lsspct";
    }


//    @GetMapping("/taoqr")
//    public String generateQrCode(@RequestParam("maspct") String maspct, Model model) throws IOException, WriterException {
//        // Tạo mã QR từ mã sản phẩm chi tiết (maspct)
//         String role = "admin";
//        model.addAttribute("role", role);
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(maspct, BarcodeFormat.QR_CODE, 200, 200);
//
//        // Chuyển BitMatrix thành BufferedImage
//        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
//        for (int x = 0; x < 200; x++) {
//            for (int y = 0; y < 200; y++) {
//                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
//            }
//        }
//
//        // Chuyển BufferedImage thành byte[]
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "PNG", baos);
//        byte[] qrCodeBytes = baos.toByteArray();
//
//        // Lưu vào cơ sở dữ liệu
//        SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaspct(maspct);
//        if (spct != null) {
//            spct.setQrspct(qrCodeBytes); // Lưu mảng byte vào thuộc tính qrspct
//            sanPhamChiTietRepository.save(spct); // Lưu lại đối tượng đã được cập nhật
//        }
//        model.addAttribute("message", "QR code generated and saved successfully!");
//        return "admin/BanHang/qrsp";
//    }

}
