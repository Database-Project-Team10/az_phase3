package src.link;

import java.util.List;
import src.link.exception.LinkAccessException;
import src.link.exception.InvalidLinkInputException;

public class LinkService {

    private final LinkRepository linkRepository = new LinkRepository();

    public boolean createLink(Link link) {
        validateUrl(link.getUrl());
        return linkRepository.save(link);
    }
    private void validateUrl(String url) {
        if (!url.matches("^https?://.+")) {
            throw new InvalidLinkInputException("URL 형식이 올바르지 않습니다. 'http://' 또는 'https://'로 시작해야 합니다.");
        }
    }
    
    public List<Link> getLinksByProject(Long projectId) {
        return linkRepository.findByProjectId(projectId);
    }
    
    public Link getLink(Long linkId) {
        return linkRepository.findById(linkId);
    }

    public boolean updateLink(Link updatedLink, Long expectedProjectId) {
        Link targetLink = linkRepository.findById(updatedLink.getId());
        if (targetLink == null || !targetLink.getProjectId().equals(expectedProjectId)) {
            throw new LinkAccessException("수정 권한이 없거나 유효하지 않은 링크 ID입니다.");
        }
        validateUrl(updatedLink.getUrl()); 
        return linkRepository.update(updatedLink);
    }

    public boolean deleteLink(Long linkId, Long expectedProjectId) {
        Link targetLink = linkRepository.findById(linkId);
        if (targetLink == null || !targetLink.getProjectId().equals(expectedProjectId)) {
            throw new LinkAccessException("삭제 권한이 없거나 유효하지 않은 링크 ID입니다.");
        }
        return linkRepository.delete(linkId);
    }
}