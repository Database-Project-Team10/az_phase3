package src.post;

import src.post.exception.InvalidPostException;
import src.post.exception.PostNotFoundException;
import src.post.exception.UnauthorizedPostAccessException;

import java.util.List;

public class PostService {

    private final PostRepository postRepository = new PostRepository();

    /**
     * 프로젝트 내 전체 게시글 조회
     */
    public List<Post> getPostList(Long projectId) {
        if (projectId == null) {
            throw new InvalidPostException("프로젝트 ID가 필요합니다.");
        }
        return postRepository.findAllPostsByProjectId(projectId);
    }

    /**
     * 프로젝트 내 내가 쓴 게시글 조회
     */
    public List<Post> getMyPostList(Long projectId, Long memberId) {
        if (projectId == null) {
            throw new InvalidPostException("프로젝트 ID가 필요합니다.");
        }
        if (memberId == null) {
            throw new InvalidPostException("회원 ID가 필요합니다.");
        }
        return postRepository.findMyPostsByProjectIdAndMemberId(projectId, memberId);
    }

    /**
     * 게시글 생성
     */
    public void createPost(Long projectId, Long memberId, PostCreateRequestDto requestDto) {

        if (projectId == null) {
            throw new InvalidPostException("프로젝트 ID가 필요합니다.");
        }
        if (memberId == null) {
            throw new InvalidPostException("회원 ID가 필요합니다.");
        }
        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new InvalidPostException("게시글 제목은 필수입니다.");
        }
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new InvalidPostException("게시글 내용은 필수입니다.");
        }
        Post post = new Post(projectId, memberId, requestDto.getTitle(), requestDto.getContent());

        postRepository.save(post);
    }

    /**
     * 게시글 단건 조회
     */
    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            throw new PostNotFoundException();
        }

        return post;
    }

    /**
     * 프로젝트 내부 게시글 단건 조회
     */
    public Post getPostInProject(Long projectId, Long postId) {
        Post post = postRepository.findByIdAndProjectId(projectId, postId);

        if (post == null) {
            throw new PostNotFoundException();
        }

        return post;
    }

    /**
     * 게시글 수정
     */
    public void updatePost(Post updatedPost) {

        Post original = postRepository.findById(updatedPost.getId());
        if (original == null) {
            throw new PostNotFoundException();
        }

        // 작성자 본인만 수정 가능
        if (!original.getMemberId().equals(updatedPost.getMemberId())) {
            throw new UnauthorizedPostAccessException();
        }

        if (updatedPost.getTitle() == null || updatedPost.getTitle().trim().isEmpty()) {
            throw new InvalidPostException("게시글 제목은 필수입니다.");
        }
        if (updatedPost.getContent() == null || updatedPost.getContent().trim().isEmpty()) {
            throw new InvalidPostException("게시글 내용은 필수입니다.");
        }

        postRepository.update(updatedPost);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(Long postId, Long memberId) {

        Post original = postRepository.findById(postId);
        if (original == null) {
            throw new PostNotFoundException();
        }

        if (!original.getMemberId().equals(memberId)) {
            throw new UnauthorizedPostAccessException();
        }

        postRepository.delete(postId);
    }
}