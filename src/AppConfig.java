package src;

import src.document.DocumentController;
import src.document.DocumentRepository;
import src.document.DocumentService;
import src.link.LinkController;
import src.link.LinkRepository;
import src.link.LinkService;
import src.main.MainController;
import src.matching.MatchingController;
import src.matching.MatchingRepository;
import src.matching.MatchingService;
import src.mbti.MbtiRepository;
import src.mbti.member.MemberMbtiController;
import src.mbti.member.MemberMbtiRepository;
import src.mbti.member.MemberMbtiService;
import src.mbti.project.ProjectMbtiController;
import src.mbti.project.ProjectMbtiRepository;
import src.mbti.project.ProjectMbtiService;
import src.meeting.MeetingController;
import src.meeting.MeetingRepository;
import src.meeting.MeetingService;
import src.member.MemberController;
import src.member.MemberRepository;
import src.member.MemberService;
import src.participant.ParticipantRepository;
import src.participant.ParticipantService;
import src.post.PostController;
import src.post.PostRepository;
import src.post.PostService;
import src.project.ProjectController;
import src.project.ProjectDetailController;
import src.project.ProjectRepository;
import src.project.ProjectService;
import src.reply.ReplyController;
import src.reply.ReplyRepository;
import src.reply.ReplyService;
import src.techspec.TechspecRepository;
import src.techspec.member.MemberTechspecController;
import src.techspec.member.MemberTechspecRepository;
import src.techspec.member.MemberTechspecService;
import src.techspec.project.ProjectTechspecController;
import src.techspec.project.ProjectTechspecRepository;
import src.techspec.project.ProjectTechspecService;

import java.util.Scanner;

public class AppConfig {

    // =====================
    // Scanner (싱글톤)
    // =====================
    private final Scanner scanner = new Scanner(System.in);

    // =====================
    // Repository (필드로 선언하여 싱글톤 보장)
    // =====================
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final MemberRepository memberRepository = new MemberRepository();
    private final ParticipantRepository participantRepository = new ParticipantRepository();
    private final PostRepository postRepository = new PostRepository();
    private final ReplyRepository replyRepository = new ReplyRepository();
    private final LinkRepository linkRepository = new LinkRepository();
    private final DocumentRepository documentRepository = new DocumentRepository();
    private final MeetingRepository meetingRepository = new MeetingRepository();
    private final MbtiRepository mbtiRepository = new MbtiRepository();
    private final TechspecRepository techspecRepository = new TechspecRepository();
    private final MemberMbtiRepository memberMbtiRepository = new MemberMbtiRepository();
    private final MemberTechspecRepository memberTechspecRepository = new MemberTechspecRepository();
    private final ProjectMbtiRepository projectMbtiRepository = new ProjectMbtiRepository();
    private final ProjectTechspecRepository projectTechspecRepository = new ProjectTechspecRepository();
    private final MatchingRepository matchingRepository = new MatchingRepository();

    // =====================
    // Service (상태 보존을 위한 필드 선언)
    // =====================
    private MemberService memberService;
    private ProjectService projectService;
    private ParticipantService participantService;
    private MatchingService matchingService;
    private PostService postService;
    private ReplyService replyService;
    private LinkService linkService;
    private DocumentService documentService;
    private MeetingService meetingService;
    private ProjectTechspecService projectTechspecService;
    private ProjectMbtiService projectMbtiService;
    private MemberTechspecService memberTechspecService;
    private MemberMbtiService memberMbtiService;


    // =====================
    // Repository Accessor
    // =====================
    public ProjectRepository projectRepository() { return projectRepository; }
    public MemberRepository memberRepository() { return memberRepository; }
    public ParticipantRepository participantRepository() { return participantRepository; }
    public PostRepository postRepository() { return postRepository; }
    public ReplyRepository replyRepository() { return replyRepository; }
    public LinkRepository linkRepository() { return linkRepository; }
    public DocumentRepository documentRepository() { return documentRepository; }
    public MeetingRepository meetingRepository() { return meetingRepository; }
    public MbtiRepository mbtiRepository() { return mbtiRepository; }
    public TechspecRepository techspecRepository() { return techspecRepository; }
    public MemberMbtiRepository memberMbtiRepository() { return memberMbtiRepository; }
    public MemberTechspecRepository memberTechspecRepository() { return memberTechspecRepository; }
    public ProjectMbtiRepository projectMbtiRepository() { return projectMbtiRepository; }
    public ProjectTechspecRepository projectTechspecRepository() { return projectTechspecRepository; }
    public MatchingRepository matchingRepository() { return matchingRepository; }


    // =====================
    // Service Accessor (싱글톤 패턴 적용: 없으면 만들고, 있으면 재사용)
    // =====================

    public MemberService memberService() {
        if (this.memberService == null) {
            this.memberService = new MemberService(memberRepository());
        }
        return this.memberService;
    }

    public ProjectService projectService() {
        if (this.projectService == null) {
            this.projectService = new ProjectService(
                    projectRepository(),
                    participantRepository(),
                    techspecRepository(),
                    projectTechspecRepository(),
                    projectMbtiRepository()
            );
        }
        return this.projectService;
    }

    public ParticipantService participantService() {
        if (this.participantService == null) {
            this.participantService = new ParticipantService(
                    participantRepository(),
                    projectService() // 싱글톤 ProjectService 주입
            );
        }
        return this.participantService;
    }

    public MatchingService matchingService() {
        if (this.matchingService == null) {
            this.matchingService = new MatchingService(matchingRepository());
        }
        return this.matchingService;
    }

    public PostService postService() {
        if (this.postService == null) {
            this.postService = new PostService(postRepository());
        }
        return this.postService;
    }

    public ReplyService replyService() {
        if (this.replyService == null) {
            this.replyService = new ReplyService(replyRepository());
        }
        return this.replyService;
    }

    public LinkService linkService() {
        if (this.linkService == null) {
            this.linkService = new LinkService(linkRepository());
        }
        return this.linkService;
    }

    public DocumentService documentService() {
        if (this.documentService == null) {
            this.documentService = new DocumentService(documentRepository());
        }
        return this.documentService;
    }

    public MeetingService meetingService() {
        if (this.meetingService == null) {
            this.meetingService = new MeetingService(meetingRepository());
        }
        return this.meetingService;
    }

    public ProjectTechspecService projectTechspecService() {
        if (this.projectTechspecService == null) {
            this.projectTechspecService = new ProjectTechspecService(
                    techspecRepository(),
                    projectTechspecRepository()
            );
        }
        return this.projectTechspecService;
    }

    public ProjectMbtiService projectMbtiService() {
        if (this.projectMbtiService == null) {
            this.projectMbtiService = new ProjectMbtiService(
                    mbtiRepository(),
                    projectMbtiRepository()
            );
        }
        return this.projectMbtiService;
    }

    public MemberTechspecService memberTechspecService() {
        if (this.memberTechspecService == null) {
            this.memberTechspecService = new MemberTechspecService(
                    techspecRepository(),
                    memberTechspecRepository()
            );
        }
        return this.memberTechspecService;
    }

    public MemberMbtiService memberMbtiService() {
        if (this.memberMbtiService == null) {
            this.memberMbtiService = new MemberMbtiService(
                    mbtiRepository(),
                    memberMbtiRepository()
            );
        }
        return this.memberMbtiService;
    }


    // =====================
    // Controller
    // =====================
    // Controller는 상태를 가지지 않으므로 매번 생성해도 되지만,
    // 내부에 주입되는 Service들이 싱글톤이므로 데이터가 유지됩니다.

    public MatchingController matchingController() {
        return new MatchingController(
                matchingService(),
                scanner
        );
    }

    public MemberTechspecController memberTechspecController() {
        return new MemberTechspecController(
                memberTechspecService(),
                scanner
        );
    }

    public MemberMbtiController memberMbtiController() {
        return new MemberMbtiController(
                memberMbtiService(),
                scanner
        );
    }

    public MemberController memberController() {
        return new MemberController(
                memberService(), // ★ 여기서 주입되는 memberService가 이미 생성된 객체입니다.
                memberTechspecController(),
                memberMbtiController(),
                matchingController()
        );
    }

    public ProjectTechspecController projectTechspecController() {
        return new ProjectTechspecController(
                projectTechspecService(),
                scanner
        );
    }

    public ProjectMbtiController projectMbtiController() {
        return new ProjectMbtiController(
                projectMbtiService(),
                scanner
        );
    }

    public ReplyController replyController() {
        return new ReplyController(
                memberService(),
                replyService(),
                scanner
        );
    }

    public PostController postController() {
        return new PostController(
                replyController(),
                memberService(),
                scanner,
                postService()
        );
    }

    public LinkController linkController() {
        return new LinkController(linkService(), memberService(), scanner);
    }

    public DocumentController documentController() {
        return new DocumentController(documentService(), memberService(), scanner);
    }

    public MeetingController meetingController() {
        return new MeetingController(meetingService(), memberService(), scanner);
    }

    public ProjectDetailController projectDetailController() {
        return new ProjectDetailController(
                postController(),
                linkController(),
                documentController(),
                meetingController(),
                projectTechspecController(),
                projectMbtiController(),
                projectService(),
                memberService() // ★ Project 쪽에서도 동일한 memberService 객체를 사용하게 됩니다.
        );
    }

    public ProjectController projectController() {
        return new ProjectController(
                memberService(),
                projectService(),
                participantService(),
                projectDetailController(),
                projectMbtiController(),
                projectTechspecController()
        );
    }

    public MainController mainController() {
        return new MainController(memberController(), projectController());
    }
}