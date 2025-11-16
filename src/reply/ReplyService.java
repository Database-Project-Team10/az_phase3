package src.reply;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository = new ReplyRepository();

    public List<ReplyResponseDto> getReplyList(Long postId){
        return replyRepository.findAllByPostId(postId);
    }

    public List<Reply> getMyReplyList(Long postId, Long memberId){
        return replyRepository.findMyReplyByPostIdAndMemberId(postId, memberId);
    }

    public Reply getReply(Long replyId){
        return replyRepository.findById(replyId);
    }

    public boolean createReply(Reply reply){
        return replyRepository.save(reply);
    }

    public boolean updateReply(Reply reply){
        return replyRepository.update(reply);
    }

    public boolean deleteReply(Long replyId){
        return replyRepository.delete(replyId);
    }
}
