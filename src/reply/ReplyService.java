package src.reply;

import src.post.exception.InvalidPostException;
import src.reply.dto.ReplyRequestDto;
import src.reply.dto.ReplyResponseDto;
import src.reply.exception.InvalidReplyException;
import src.reply.exception.ReplyNotFoundException;
import src.reply.exception.ReplyNotInPostException;
import src.reply.exception.UnauthorizedReplyAccessException;

import java.util.List;

public class ReplyService {

    private final ReplyRepository replyRepository;
    public ReplyService(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

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

    public void createReply(Long postId, Long memberId, ReplyRequestDto requestDto){
        if (postId == null){
            throw new InvalidReplyException("게시물 ID가 필요합니다.");
        }
        if (memberId == null){
            throw new InvalidReplyException("회원 ID가 필요합니다.");
        }
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new InvalidPostException("댓글 내용은 필수입니다.");
        }

        Reply reply = new Reply(postId, memberId, requestDto.getContent());

        replyRepository.save(reply);
    }

    public void updateReply(Long postId, Long replyId, Long memberId, ReplyRequestDto requestDto){
        Reply original =  replyRepository.findById(replyId);
        if (original == null){
            throw new ReplyNotFoundException();
        }

        if (!original.getPostId().equals(postId)){
            throw new ReplyNotInPostException();
        }

        if (!original.getMemberId().equals(memberId)){
            throw new UnauthorizedReplyAccessException();
        }

        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new InvalidPostException("댓글 내용은 필수입니다.");
        }

        Reply reply = new Reply(
                original.getId(),
                original.getPostId(),
                original.getMemberId(),
                requestDto.getContent(),
                original.getCreatedAt()
        );

        replyRepository.update(reply);
    }

    public void deleteReply(Long replyId, Long memberId){
        Reply original =  replyRepository.findById(replyId);
        if (original == null){
            throw new ReplyNotFoundException();
        }

        if (!original.getMemberId().equals(memberId)){
            throw new UnauthorizedReplyAccessException();
        }
        replyRepository.delete(replyId);
    }
}
