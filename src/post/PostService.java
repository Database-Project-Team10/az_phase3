package src.post;

import java.util.List;
import java.util.Scanner;

public class PostService {

    private final PostRepository postRepository = new PostRepository();
    Scanner scanner = new Scanner(System.in);

    public List<Post> getPostList(Long projectId){
        return postRepository.findAllPostsByProjectId(projectId);
    }

    public List<Post> getMyPostList(Long projectId, Long memberId){
        return postRepository.findMyPostsByProjectIdAndMemberId(projectId, memberId);
    }
}
