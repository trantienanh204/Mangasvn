package com.example.demo.Controller.khachhang;
import com.example.demo.entity.DanhMuc;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.ThuongHieu;
import com.example.demo.respository.DanhMucRepository;
import com.example.demo.service.DanhMucService;
import com.example.demo.service.SanPhamService;
import com.example.demo.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/panda")
public class SearchController {
    @Autowired
    private final SanPhamService sanPhamService;

    @Autowired
    private final DanhMucService danhMucService;

    @Autowired
    private final DanhMucRepository danhMucRepository;

    @Autowired
    private final ThuongHieuService thuongHieuService;

    public SearchController(SanPhamService sanPhamService, DanhMucService danhMucService, DanhMucRepository danhMucRepository, ThuongHieuService thuongHieuService) {
        this.sanPhamService = sanPhamService;
        this.danhMucService = danhMucService;
        this.danhMucRepository = danhMucRepository;
        this.thuongHieuService = thuongHieuService;
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(value = "category", required = false) String categoryId,
                         @RequestParam(value = "brand", required = false) String brand,
                         @RequestParam(value = "priceRange", required = false) String priceRange,
                         Model model) {
        List<SanPham> searchResults;

        // Tìm kiếm theo từ khóa và danh mục
        if (categoryId == null || categoryId.isEmpty()) {
            searchResults = sanPhamService.searchSanPham(query);
        } else {
            searchResults = sanPhamService.searchSanPhamByCategory(query, Integer.parseInt(categoryId));
        }

        // Lọc kết quả theo thương hiệu
        if (brand != null && !brand.isEmpty()) {
            searchResults = searchResults.stream()
                    .filter(sp -> sp.getThuongHieu() != null && sp.getThuongHieu().getTenthuonghieu().equalsIgnoreCase(brand))
                    .collect(Collectors.toList());
        }

        // Lọc kết quả theo giá
        if (priceRange != null && !priceRange.isEmpty()) {
            searchResults = searchResults.stream()
                    .filter(sp -> {
                        switch (priceRange) {
                            case "low":
                                return sp.getMinPrice() < 100000;
                            case "medium":
                                return sp.getMinPrice() >= 100000 && sp.getMinPrice() <= 500000;
                            case "high":
                                return sp.getMinPrice() > 500000;
                            default:
                                return true;
                        }
                    })
                    .collect(Collectors.toList());
        }

        searchResults.forEach(sp -> {
            if (sp != null && sp.getAnhsp() != null) {
                String base64Image = Base64.getEncoder().encodeToString(sp.getAnhsp());
                sp.setBase64Image(base64Image);
            }
        });

        List<DanhMuc> categories = danhMucRepository.findAll();
        List<ThuongHieu> brands = thuongHieuService.findAllThuongHieu();

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);

        return "khachhang/timkiemsanphamTT";
    }
}