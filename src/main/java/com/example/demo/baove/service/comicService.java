package com.example.demo.baove.service;

import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.repository.comicRepository;
import com.example.demo.baove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



import java.util.List;


@Service
public class comicService {
    @Autowired
    private comicRepository comicRepository;

    @Autowired
    private UserRepository userRepository;
private final Integer size = 12;

    public List<Comic> searchALL(List<Integer> id, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comic> result = comicRepository.findByDanhMucIds(id, pageable);
        return result.getContent();
    }
    public List<Comic> searchcomictag(Integer id, int page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Comic> result = comicRepository.findByDanhMucId(id, pageable);
        return result.getContent();
    }

}
