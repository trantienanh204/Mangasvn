package com.example.demo.baove.service;

import com.example.demo.baove.entity.Comic;
import com.example.demo.baove.entity.User;
import com.example.demo.baove.entity.Viewed;
import com.example.demo.baove.repository.ViewedRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ViewedService {

    @Autowired
    private ViewedRepository viewedRepository;

    public Viewed saveViewed(Viewed viewed) {
        return viewedRepository.save(viewed);
    }

    public Viewed findByUserAndComicId(User user, int comicId) {
        return viewedRepository.findByUsersAndComicsId(user, comicId);
    }

    public List<Viewed> getViewedByUser(User user) {
        return viewedRepository.findByUsers(user);
    }

    public void deleteViewedByUser(User user) {
        viewedRepository.deleteByUsers(user);
    }
@Transactional
    public void deleteViewedByUserAndComicId(User user, Long comicId) {
        viewedRepository.deleteByUsersAndComicsId(user, comicId);
    }
}