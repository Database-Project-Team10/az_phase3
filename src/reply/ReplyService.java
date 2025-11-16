package src.reply;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository = new ReplyRepository();

    public List<Reply> getReplyList(Long postId){
        return replyRepository.findAllByPostId(postId);
    }

    public List<Reply> getMyReplyList(Long postId, Long memberId){
        return replyRepository.findMyReplyByPostIdAndMemberId(postId, memberId);
    }

    public boolean createReply(Reply reply){
        return replyRepository.save(reply);
    }
}
