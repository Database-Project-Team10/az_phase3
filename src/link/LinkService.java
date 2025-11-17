package src.link;

import java.util.List;

public class LinkService {

    private final LinkRepository linkRepository = new LinkRepository();

    public boolean createLink(Link link) {
        return linkRepository.save(link);
    }

    public List<Link> getLinksByProject(Long projectId) {
        return linkRepository.findByProjectId(projectId);
    }
    
    public Link getLink(Long linkId) {
        return linkRepository.findById(linkId);
    }

    public boolean updateLink(Link link) {
        return linkRepository.update(link);
    }

    public boolean deleteLink(Long linkId) {
        return linkRepository.delete(linkId);
    }
}