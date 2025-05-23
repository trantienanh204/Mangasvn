package com.example.demo.baove.service;

import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class comicService {
    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;

    public Comic createTruyen(String moTa, String ghiChu, Integer translatorId) {
        Comic truyen = new Comic();
        truyen.setMoTa(moTa);
        truyen.setGhiChu(ghiChu);
        truyen.setLuotThich(0);
        truyen.setLuotXem(0);
        truyen.setNgayTao(LocalDate.now());

        User translator = userRepository.findById(translatorId)
                .orElseThrow(() -> new RuntimeException("Translator not found"));
        truyen.setTranslator(translator);

        return comicRepository.save(truyen);
    }

    public Comic updateTruyen(Integer truyenId, String moTa, String ghiChu, Integer userId, String userRole) {
        Comic truyen = comicRepository.findById(truyenId)
                .orElseThrow(() -> new RuntimeException("Truyen not found"));

        if (!userRole.equals("admin") && !(truyen.getTranslator().getId()!= userId)) {
            throw new RuntimeException("You don't have permission to edit this truyen");
        }

        truyen.setMoTa(moTa);
        truyen.setGhiChu(ghiChu);
        return comicRepository.save(truyen);
    }
}
