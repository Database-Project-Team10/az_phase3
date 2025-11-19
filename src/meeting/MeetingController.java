package src.meeting;

import src.meeting.exception.InvalidMeetingInputException;
import src.meeting.dto.MeetingRequestDto;
import src.meeting.exception.MeetingException;
import src.member.MemberService;
import src.utils.InputUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MeetingController {

    private final MeetingService meetingService;
    private final MemberService memberService;
    private final Scanner scanner;
    private final DateTimeFormatter formatter;

    public MeetingController(
            MeetingService meetingService,
            MemberService memberService,
            Scanner scanner
    ) {
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.scanner = scanner;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }


    public void showMeetingMenu(Long projectId) {
    	while (true) {
            printMenuHeader(projectId);

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인이 필요합니다.");
                return;
            }

            String choice = getMenuChoice();

            switch (choice) {
                case "1":
                    handleShowAllMeetings(projectId);
                    break;

                case "2":
                    handleCreateMeeting(projectId);
                    break;

                case "3":
                    handleUpdateMeeting(projectId);
                    break;

                case "4":
                    handleDeleteMeeting(projectId);
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
    private void printMenuHeader(Long projectId) {
        System.out.println("\n---------- 회의록 기능 ----------");
        System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
        System.out.println("현재 접속 중인 프로젝트: " + projectId);
        System.out.println("1. 전체 회의록 보기");
        System.out.println("2. 회의록 작성");
        System.out.println("3. 회의록 수정");
        System.out.println("4. 회의록 삭제");
        System.out.println("b. 뒤로 가기");
    }

    private String getMenuChoice() {
        System.out.print("메뉴를 선택하세요: ");
        return scanner.nextLine();
    }

    private void handleShowAllMeetings(Long projectId) {
        try {
            List<Meeting> meetings = meetingService.getMeetingsByProject(projectId);
            showMeetingList(meetings);
        } catch (MeetingException e) {
            printError(e);
        }
        pause();
    }

    private void handleCreateMeeting(Long projectId) {
        System.out.println("---------- 회의록 작성 ----------");
        
        try {
            String title = InputUtil.getInput(scanner, "회의록 제목");
            String description = InputUtil.getInput(scanner, "회의록 내용");

            LocalDateTime startTime = parseDateTime("회의 시작 시간 (YYYY-MM-DD HH:MM): ");
            LocalDateTime endTime = parseDateTime("회의 종료 시간 (YYYY-MM-DD HH:MM): ");
            
            MeetingRequestDto requestDto = new MeetingRequestDto(title, description, startTime, endTime);
            
            meetingService.createMeeting(projectId, requestDto);
            
            System.out.println("회의록이 성공적으로 작성되었습니다.");

        }catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 회의록 작성이 취소되었습니다.");
        } catch (MeetingException e) {
            printError(e);
        }
        
        pause();
    }

    private void handleUpdateMeeting(Long projectId) {
        System.out.println("---------- 회의록 수정 ----------");
        try {
            List<Meeting> meetings = meetingService.getMeetingsByProject(projectId);
            showMeetingList(meetings);
        } catch (MeetingException e) {
            printError(e);
            pause(); 
            return; 
        }
        
        try {
            Long meetingId = InputUtil.getLong(scanner, "수정할 회의록의 번호");
            
            Meeting targetMeeting = meetingService.getMeeting(meetingId);

            String newTitle = promptTitleUpdate(targetMeeting);
            String newDescription = promptDescriptionUpdate(targetMeeting);
            LocalDateTime newStartTime = promptStartTimeUpdate(targetMeeting);
            LocalDateTime newEndTime = promptEndTimeUpdate(targetMeeting);

            MeetingRequestDto requestDto = new MeetingRequestDto(newTitle, newDescription, newStartTime, newEndTime);
            
            meetingService.updateMeeting(targetMeeting.getId(), projectId, requestDto);
            
            System.out.println("회의록이 성공적으로 수정되었습니다.");

        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 회의록 수정이 취소되었습니다.");
        }catch (NumberFormatException e) {
            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
        } catch (MeetingException e) {
            printError(e);
        }
        
        pause();
    }

    private String promptTitleUpdate(Meeting meeting) {
        while (true) {
            System.out.print("제목을 수정하시겠습니까? (Y/N, 현재: " + meeting.getTitle() + "): ");
            String c = scanner.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 제목 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return meeting.getTitle();
            } else { System.out.println("잘못된 입력입니다."); }
        }
    }

    private String promptDescriptionUpdate(Meeting meeting) {
        while (true) {
            System.out.print("내용을 수정하시겠습니까? (Y/N): ");
            String c = scanner.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                System.out.print("수정할 내용 입력: ");
                return scanner.nextLine();
            } else if (c.equalsIgnoreCase("N")) {
                return meeting.getDescription();
            } else { System.out.println("잘못된 입력입니다."); }
        }
    }

    private LocalDateTime promptStartTimeUpdate(Meeting meeting) {
        while (true) {
            String current = (meeting.getStartTime() != null) ? meeting.getStartTime().format(formatter) : "없음";
            System.out.print("시작 시간을 수정하시겠습니까? (Y/N, 현재: " + current + "): ");
            String c = scanner.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                return parseDateTime("새 시작 시간 (YYYY-MM-DD HH:MM) : ");
            } else if (c.equalsIgnoreCase("N")) {
                return meeting.getStartTime();
            } else { System.out.println("잘못된 입력입니다."); }
        }
    }

    private LocalDateTime promptEndTimeUpdate(Meeting meeting) {
        while (true) {
            String current = (meeting.getEndTime() != null) ? meeting.getEndTime().format(formatter) : "없음";
            System.out.print("종료 시간을 수정하시겠습니까? (Y/N, 현재: " + current + "): ");
            String c = scanner.nextLine();
            if (c.equalsIgnoreCase("Y")) {
                return parseDateTime("새 종료 시간 (YYYY-MM-DD HH:MM): ");
            } else if (c.equalsIgnoreCase("N")) {
                return meeting.getEndTime();
            } else { System.out.println("잘못된 입력입니다."); }
        }
    }

    private void handleDeleteMeeting(Long projectId) {
        System.out.println("---------- 회의록 삭제 ----------");
        
        try {
            List<Meeting> meetings = meetingService.getMeetingsByProject(projectId);
            showMeetingList(meetings);

            Long meetingId = InputUtil.getLong(scanner, "삭제할 회의록의 ID 번호");

            meetingService.deleteMeeting(meetingId, projectId);
            System.out.println("회의록이 성공적으로 삭제되었습니다.");
        } catch (InputUtil.CancelException e) {
            System.out.println("\n[!] 회의록 삭제가 취소되었습니다.");
        } catch (MeetingException e) {
            printError(e);
        }

        pause();

    }

    private void printError(Exception e) {
        System.out.println("[오류] " + e.getMessage());
    }

    private void pause() {
        System.out.print("\n엔터키를 누르면 회의록 메뉴로 돌아갑니다.");
        scanner.nextLine();
    }

    private void showMeetingList(List<Meeting> meetingList) {
        System.out.println("---------- 회의록 목록 ----------");
        if (meetingList == null || meetingList.isEmpty()) {
            System.out.println("(등록된 회의록이 없습니다.)");
            return;
        }
        
        for (Meeting meeting : meetingList) {
            System.out.println(meeting.getId() + ". " + meeting.getTitle());
            String startTimeStr = (meeting.getStartTime() != null) ? meeting.getStartTime().format(formatter) : "N/A";
            String endTimeStr = (meeting.getEndTime() != null) ? meeting.getEndTime().format(formatter) : "N/A";
            System.out.println("   Time: " + startTimeStr + " ~ " + endTimeStr);
            System.out.println("   Desc: " + (meeting.getDescription() != null ? meeting.getDescription() : ""));
            System.out.println();
        }
    }

    private LocalDateTime parseDateTime(String prompt) {
        while (true) {
            try {
                String input = InputUtil.getInput(scanner, prompt);

                if (input.trim().isEmpty()) {
                    return null;
                }
                return LocalDateTime.parse(input, formatter);

            } catch (DateTimeParseException e) {
                System.out.println("오류: 날짜 형식이 잘못되었습니다. (YYYY-MM-DD HH:MM)");
            }
        }
    }
}