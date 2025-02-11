
package com.example.demo.service;
import com.example.demo.entity.Anh_SP;
import com.example.demo.respository.HinhAnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class hinhanhService {
    @Autowired
    private HinhAnhRepository hinhAnhRepository;



    public void luuHinhAnh(MultipartFile file) throws IOException {
        Anh_SP hinhAnh = new Anh_SP();
        hinhAnh.setTenanh(file.getOriginalFilename());
        hinhAnh.setDulieuanh(file.getBytes());
        hinhAnhRepository.save(hinhAnh);
    }


    public List<String> allUserAccout = new ArrayList<>();
    public void add(String e){
        allUserAccout.add(e);
    }
    public static void main(String[] args) {

        hinhanhService hinhAnhService = new hinhanhService();

        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();

        String regex = "^[a-zA-Z]+[a-z0-9]*@{1}+gmail.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            System.out.println("ok");
            hinhAnhService.add(text);
            System.out.println(hinhAnhService.allUserAccout.stream().toList());
        }else{
            System.out.println("not good");
        }
    }

    public  Optional<Anh_SP> findById(Integer id) {
        return hinhAnhRepository.findById(id);
    }
    public List<Anh_SP> hinhanh(){
        return hinhAnhRepository.findAll() ;
    }
    public void xoa(Integer id){
        hinhAnhRepository.deleteById(id);
    }
}



