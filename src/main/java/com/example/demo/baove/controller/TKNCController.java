package com.example.demo.baove.controller;

import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.service.comicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("search")
public class TKNCController {

    @Autowired
    comicService comicService;

    @GetMapping()
    public List<Comic> allComic(@RequestParam List<Integer> id, @RequestParam int page) {
        return comicService.searchALL(id,page);
    }

    @GetMapping("tag")
    public List<Comic> allComicdanhmic(@RequestParam Integer id, @RequestParam int page) {
        return comicService.searchcomictag(id,page);
    }

    
}
