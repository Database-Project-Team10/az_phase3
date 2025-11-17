package src.reply;

import src.post.exception.InvalidPostException;
import src.reply.exception.InvalidReplyException;
import src.reply.exception.ReplyNotFoundException;
import src.reply.exception.UnauthorizedReplyAccessException;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository = new ReplyRepository();

    public List<ReplyResponseDto> getReplyList(Long postId){
        if (postId == null){
            throw new InvalidReplyException("게시물 ID가 필요합니다.");
        }
        return replyRepository.findAllByPostId(postId);
    }

    public List<Reply> getMyReplyList(Long postId, Long memberId){
        if (postId == null){
            throw new InvalidReplyException("게시물 ID가 필요합니다.");
        }
        if (memberId == null){
            throw new InvalidReplyException("회원 ID가 필요합니다.");
        }
        return replyRepository.findMyReplyByPostIdAndMemberId(postId, memberId);
    }

    public Reply getReply(Long replyId){
        Reply reply = replyRepository.findById(replyId);
        if (reply == null){
            throw new ReplyNotFoundException();
        }

        return reply;
    }

    public boolean createReply(Reply reply){
        if (reply.getPostId() == null){
            throw new InvalidReplyException("게시물 ID가 필요합니다.");
        }
        if (reply.getMemberId() == null){
            throw new InvalidReplyException("회원 ID가 필요합니다.");
        }
        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            throw new InvalidPostException("댓글 내용은 필수입니다.");
        }
        return replyRepository.save(reply);
    }

    public boolean updateReply(Reply reply){
        Reply original =  replyRepository.findById(reply.getId());
        if (original == null){
            throw new ReplyNotFoundException();
        }

        if (!original.getMemberId().equals(reply.getMemberId())){
            throw new UnauthorizedReplyAccessException();
        }

        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            throw new InvalidPostException("댓글 내용은 필수입니다.");
        }

        return replyRepository.update(reply);
    }

    public boolean deleteReply(Long replyId, Long memberId){
        Reply original =  replyRepository.findById(replyId);
        if (original == null){
            throw new ReplyNotFoundException();
        }

        if (!original.getMemberId().equals(memberId)){
            throw new UnauthorizedReplyAccessException();
        }
        return replyRepository.delete(replyId);
    }
}
