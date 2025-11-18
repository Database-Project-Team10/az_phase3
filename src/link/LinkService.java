package src.link;

import java.util.List;
import src.link.exception.LinkAccessException;
import src.link.exception.InvalidLinkInputException;
import src.link.exception.LinkNotFoundException;

public class LinkService {

    private final LinkRepository linkRepository = new LinkRepository();
    
    private void validateUrl(String url) {
        if (!url.matches("^https?://.+")) {
            throw new InvalidLinkInputException("URL 형식이 올바르지 않습니다. 'http://' 또는 'https://'로 시작해야 합니다.");
        }
    }
    
    public void createLink(Link link) {
        if (link.getTitle().trim().isEmpty() || link.getUrl().trim().isEmpty()) {
            throw new InvalidLinkInputException("제목과 URL은 비워둘 수 없습니다.");
        }
        
        validateUrl(link.getUrl());
        linkRepository.save(link);
    }
    
    public List<Link> getLinksByProject(Long projectId) {
        return linkRepository.findByProjectId(projectId);
    }
    
    public Link getLink(Long linkId) {
        Link link = linkRepository.findById(linkId);
        if (link == null) {
            throw new LinkNotFoundException("ID " + linkId + "에 해당하는 링크를 찾을 수 없습니다.");
        }
        return link;
    }
    public void updateLink(Link updatedLink, Long expectedProjectId) {
        if (updatedLink.getTitle().trim().isEmpty() || updatedLink.getUrl().trim().isEmpty()) {
            throw new InvalidLinkInputException("제목과 URL은 비워둘 수 없습니다.");
        }
        Link targetLink = linkRepository.findById(updatedLink.getId());
        if (targetLink == null) {
            throw new LinkNotFoundException("수정할 링크(ID: " + updatedLink.getId() + ")를 찾을 수 없습니다.");
        }
        
        if (!targetLink.getProjectId().equals(expectedProjectId)) {
            throw new LinkAccessException("해당 링크는 이 프로젝트에 속하지 않아 수정할 수 없습니다.");
        }
        
        validateUrl(updatedLink.getUrl()); 
        linkRepository.update(updatedLink);
    }

    public void deleteLink(Long linkId, Long expectedProjectId) {
        Link targetLink = linkRepository.findById(linkId);
        if (targetLink == null) {
            throw new LinkNotFoundException("삭제할 링크(ID: " + linkId + ")를 찾을 수 없습니다.");
        }
        if (!targetLink.getProjectId().equals(expectedProjectId)) {
            throw new LinkAccessException("해당 링크는 이 프로젝트에 속하지 않아 삭제할 수 없습니다.");
        }
        
        linkRepository.delete(linkId);
    }
}