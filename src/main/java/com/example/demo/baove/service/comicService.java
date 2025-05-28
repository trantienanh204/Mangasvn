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


}
