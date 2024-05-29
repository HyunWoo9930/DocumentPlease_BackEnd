package org.example.docuementplease.service;

import org.example.docuementplease.domain.Comment;
import org.example.docuementplease.domain.Documents;
import org.example.docuementplease.domain.User;
import org.example.docuementplease.repository.CommentRepository;
import org.example.docuementplease.repository.DocumentRepository;
import org.example.docuementplease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, DocumentRepository documentRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
    }

    public void createComment(Long doc_id, String user_name, String content) {
        User user = userRepository.findByUsername(user_name).orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        Documents documents = documentRepository.findById(doc_id).orElseThrow(() -> new NotFoundException("문서가 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUserName(user.getNick_name());
        comment.setDocuments(documents);
        commentRepository.save(comment);
    }

    public void updateComment(Long comment_id, String new_content) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
        comment.setContent(new_content);
        commentRepository.save(comment);
    }

    public void updateCommentLike(Long comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new NotFoundException("댓글이 없습니다."));
        int like = comment.getLikeCount() + 1;
        comment.setLikeCount(like);
        commentRepository.save(comment);
    }

    public void deleteComment(Long comment_id) {
        commentRepository.deleteById(comment_id);
        if(commentRepository.findById(comment_id).isPresent()) {
            throw new RuntimeException("삭제되지 않았습니다.");
        }
    }
}
