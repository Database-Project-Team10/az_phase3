package src.reply;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository = new ReplyRepository();

    public List<Reply> getReplyList(Long postId){
        return replyRepository.findAllByPostId(postId);

    }
}
