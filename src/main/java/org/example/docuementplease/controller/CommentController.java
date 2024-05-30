package org.example.docuementplease.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.docuementplease.domain.Comment;
import org.example.docuementplease.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create_comment")
    public ResponseEntity<?> createComment(
            @RequestParam(value = "doc_id") Long doc_id,
            @RequestParam(value = "user_name") String user_name,
            @RequestParam(value = "content") String content
    ) {
        try {
            commentService.createComment(doc_id, user_name, content);
            return ResponseEntity.ok("성공적으로 저장하였습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update_comment")
    public ResponseEntity<?> updateComment(
            @RequestParam(value = "comment_id") Long comment_id,
            @RequestParam(value = "new_content") String new_content
    ) {
        try {
            commentService.updateComment(comment_id, new_content);
            return ResponseEntity.ok("업데이트 성공");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update_comment_like")
    public ResponseEntity<?> updateCommentLike(
            @RequestParam(value = "comment_id") Long comment_id
    ) {
        try {
            commentService.updateCommentLike(comment_id);
            return ResponseEntity.ok("좋아요 추가 완료");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete_comment")
    public ResponseEntity<?> deleteComment(
            @RequestParam(value = "comment_id") Long comment_id
    ) {
        try {
            commentService.deleteComment(comment_id);
            return ResponseEntity.ok("삭제 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(description = "doc id를 주면, 해당 문서의 댓글을 전부 반환하는 API")
    @GetMapping("/get_document_comments")
    public ResponseEntity<?> getDocumentComments(
            @RequestParam(value = "doc_id") Long doc_id
    ) {
        List<Comment> comment = commentService.getDocumentComment(doc_id);
        return ResponseEntity.ok(comment);
    }

    @Operation(description = "comment id를 주면, 댓글의 좋아요 개수를 get 할 수 있는 API")
    @GetMapping("/get_comment_likes")
    public ResponseEntity<?> getCommentLikes(
            @RequestParam(value = "comment_id") Long comment_id
    ) {
        long commentLikes = commentService.getCommentLikes(comment_id);
        return ResponseEntity.ok(commentLikes);
    }
}
