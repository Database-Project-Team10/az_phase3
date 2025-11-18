package src.link;

import src.link.dto.LinkRequestDto;
import src.link.exception.InvalidLinkInputException;
import src.link.exception.LinkAccessException;
import src.link.exception.LinkNotFoundException;

import java.util.List;

public class LinkService {

    private final LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }
    
    private void validateUrl(String url) {
        if (!url.matches("^https?://.+")) {
            throw new InvalidLinkInputException("URL 형식이 올바르지 않습니다. 'http://' 또는 'https://'로 시작해야 합니다.");
        }
    }
    
    public Link createLink(Long projectId, LinkRequestDto requestDto) {

        if (requestDto.getTitle().trim().isEmpty() || requestDto.getUrl().trim().isEmpty()) {
            throw new InvalidLinkInputException("제목과 URL은 비워둘 수 없습니다.");
        }

        validateUrl(requestDto.getUrl());

        Link link = new Link(projectId, requestDto.getTitle(), requestDto.getUrl());
        return linkRepository.save(link);
    }
    
    public List<Link> getLinksByProject(Long projectId) {
        return linkRepository.findByProjectId(projectId);
    }
    
    public Link getLink(Long linkId) {
        Link link = linkRepository.findById(linkId);
        if (link == null) {
            throw new LinkNotFoundException();
        }
        return link;
    }


    public void updateLink(Long linkId, Long projectId, LinkRequestDto requestDto) {

        Link targetLink = linkRepository.findById(linkId);
        if (targetLink == null) {
            throw new LinkNotFoundException();
        }

        if (requestDto.getTitle().trim().isEmpty() || requestDto.getUrl().trim().isEmpty()) {
            throw new InvalidLinkInputException("제목과 URL은 비워둘 수 없습니다.");
        }

        
        if (!targetLink.getProjectId().equals(projectId)) {
            throw new LinkAccessException("해당 링크는 이 프로젝트에 속하지 않아 수정할 수 없습니다.");
        }
        
        validateUrl(requestDto.getUrl());

        Link link  = new Link(linkId, projectId, requestDto.getTitle(), requestDto.getUrl());

        linkRepository.update(link);
    }


    public void deleteLink(Long linkId, Long expectedProjectId) {
        Link targetLink = linkRepository.findById(linkId);
        if (targetLink == null) {
            throw new LinkNotFoundException();
        }
        if (!targetLink.getProjectId().equals(expectedProjectId)) {
            throw new LinkAccessException("해당 링크는 이 프로젝트에 속하지 않아 삭제할 수 없습니다.");
        }
        
        linkRepository.delete(linkId);
    }
}