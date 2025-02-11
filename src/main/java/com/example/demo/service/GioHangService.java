package com.example.demo.service;

import com.example.demo.entity.GioHang;
import com.example.demo.entity.KhachHang;
import com.example.demo.entity.SanPham;
import com.example.demo.entity.SanPhamChiTiet;
import com.example.demo.respository.KhachHangRepository;
import com.example.demo.respository.SanPhamChiTietRepository;
import com.example.demo.respository.nhanVien.GioHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
    public class GioHangService {

    public  List<SanPhamChiTiet> spctLisst = new ArrayList<>();
    @Autowired
        private GioHangRepository gioHangRepository;

        @Autowired
        private SanPhamChiTietRepository sanPhamChiTietRepository;

        @Autowired
        private KhachHangRepository khachHangRepository;

        public GioHang addToCart(int khachHangId, int sanPhamChiTietId, int quantity) {
            KhachHang khachHang = khachHangRepository.findById(khachHangId)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId)
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));
            Optional<GioHang> existingCartItem = gioHangRepository
                    .findByKhachHangAndSanPhamChiTiet(khachHang, sanPhamChiTiet);

            BigDecimal gia = BigDecimal.valueOf(sanPhamChiTiet.getDongia());

            if (existingCartItem.isPresent()) {
                GioHang cartItem = existingCartItem.get();
                cartItem.setSoluong(cartItem.getSoluong() + quantity);
                cartItem.setTongtien(gia.multiply(BigDecimal.valueOf(cartItem.getSoluong())));

                return gioHangRepository.save(cartItem);
            } else {
                GioHang newCartItem = new GioHang();
                newCartItem.setKhachHang(khachHang);
                newCartItem.setSanPhamChiTiet(sanPhamChiTiet);
                newCartItem.setSoluong(quantity);
                newCartItem.setTongtien(gia.multiply(BigDecimal.valueOf(newCartItem.getSoluong())));
                return gioHangRepository.save(newCartItem);
            }
        }
        public double updateQuantity(int khachHangId, int gioHangId, int quantity) {
            GioHang gioHang = gioHangRepository.findByKhachHangIdAndId(khachHangId, gioHangId);
            if (gioHang != null) {
                int availableQuantity = gioHang.getSanPhamChiTiet().getSoluongsanpham();
                if (quantity > availableQuantity) {
                    quantity = availableQuantity;
                }

                gioHang.setSoluong(quantity);
                gioHangRepository.save(gioHang);

                double newPrice = gioHang.getSanPhamChiTiet().getDongia() * quantity;
                return newPrice;
            } else {
                throw new RuntimeException("Product not found in the cart.");
            }
        }



        public void deleteFromCart(int khachHangId, int gioHangId) {
                GioHang gioHang = gioHangRepository.findByKhachHangIdAndId(khachHangId, gioHangId);
                if (gioHang != null) {
                    gioHangRepository.delete(gioHang);
                } else {
                    throw new RuntimeException("Product not found in the cart.");
                }
            }

        public List<GioHang> getCartItems(int khachHangId) {
                return gioHangRepository.findByKhachHangId(khachHangId);
            }


            public Map<String, Object> checkQuantity(Integer sizeId, Integer colorId, Integer productId) {
                Map<String, Object> response = new HashMap<>();
                SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepository.findByKichThuocIdAndMauSacIdAndSanPhamId(sizeId, colorId, productId);

                if (sanPhamChiTiet != null) {
                    response.put("availableQuantity", sanPhamChiTiet.getSoluongsanpham());
                    response.put("sanPhamChiTietId", sanPhamChiTiet.getId());
                } else {
                    response.put("availableQuantity", 0);
                }

                return response;
            }


            public boolean checkInventory(List<Integer> selectedItems) {
                for (Integer itemId : selectedItems) {
                    GioHang item = gioHangRepository.findById(itemId).orElse(null);
                    if (item != null) {
                        int availableQuantity = item.getSanPhamChiTiet().getSoluongsanpham();
                        if (item.getSoluong() > availableQuantity) {
                            return false;
                        }
                    }
                }
                return true;
            }



        public void clearCart(int khachHangId) {
            List<GioHang> cartItems = gioHangRepository.findByKhachHangId(khachHangId);
            if (cartItems != null && !cartItems.isEmpty()) {
                List<GioHang> gioHangList = cartItems.stream()
                        .filter(gioHang -> spctLisst.stream()
                                .anyMatch(spct -> spct.getId().equals(gioHang.getSanPhamChiTiet().getId())))
                        .collect(Collectors.toList());

                gioHangRepository.deleteAll(gioHangList);
            } }

//    List<GioHang>spct = cartItems.stream().filter(gioHang ->
//            spctLisst.equals(gioHang.getSanPhamChiTiet().getId())).collect(Collectors.toList());
//            if (cartItems != null && !cartItems.isEmpty())
//    { gioHangRepository.deleteAll(spct); } }


    public List<GioHang> getCartItemsByIds(int khachHangId, List<Integer> itemIds) { return gioHangRepository.findAllByIdInAndKhachHangId(itemIds, khachHangId); }

    }




