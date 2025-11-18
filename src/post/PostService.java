package src.post;

import src.post.dto.PostRequestDto;
import src.post.exception.InvalidPostException;
import src.post.exception.PostNotFoundException;
import src.post.exception.PostNotInProjectException;
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
    public void createPost(Long projectId, Long memberId, PostRequestDto requestDto) {

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
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new PostNotFoundException();
        }

        if (!post.getProjectId().equals(projectId)) {
            throw new  PostNotInProjectException();
        }

        return post;
    }

    /**
     * 게시글 수정
     */
    public void updatePost(Long projectId, Long postId, Long memberId, PostRequestDto requestDto) {

        Post original = postRepository.findById(postId);
        if (original == null) {
            throw new PostNotFoundException();
        }

        if (!original.getProjectId().equals(projectId)) {
            throw new PostNotInProjectException();
        }

        // 작성자 본인만 수정 가능
        if (!original.getMemberId().equals(memberId)) {
            throw new UnauthorizedPostAccessException();
        }

        if (requestDto.getTitle() == null || requestDto.getTitle().trim().isEmpty()) {
            throw new InvalidPostException("게시글 제목은 필수입니다.");
        }
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new InvalidPostException("게시글 내용은 필수입니다.");
        }

        Post post = new Post(original.getId(), projectId, memberId, requestDto.getTitle(), requestDto.getContent(), original.getCreatedAt());

        postRepository.update(post);
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