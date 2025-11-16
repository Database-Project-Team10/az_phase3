package src.post;

import java.util.List;

public class PostService {

    private final PostRepository postRepository = new PostRepository();

    public List<Post> getPostList(Long projectId){
        return postRepository.findAllPostsByProjectId(projectId);
    }

    public List<Post> getMyPostList(Long projectId, Long memberId){
        return postRepository.findMyPostsByProjectIdAndMemberId(projectId, memberId);
    }

    public boolean createPost(Post post){
        return postRepository.save(post);
    }

    public Post getPost(Long postId){
        return postRepository.findById(postId);
    }

    public boolean updatePost(Post post){
        return postRepository.update(post);
    }
}
