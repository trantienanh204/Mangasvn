package com.example.demo.baove.controller.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
        private Integer userId;
        private Integer comicId;
        private Integer chapterId;
        private String content;
        private Integer parentId;
    }

