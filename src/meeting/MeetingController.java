package src.meeting;

import src.meeting.exception.InvalidMeetingInputException;
import src.meeting.exception.MeetingException;
import src.member.MemberService;

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
            System.out.println("\n---------- 회의록 기능 ----------");

            if (!memberService.isLoggedIn()) {
                System.out.println("로그인이 필요합니다.");
                return;
            }
            
            System.out.println("현재 로그인: " + memberService.getCurrentUser().getEmail());
            System.out.println("현재 접속 중인 프로젝트: " + projectId);
            System.out.println("1. 전체 회의록 보기");
            System.out.println("2. 회의록 작성");
            System.out.println("3. 회의록 수정");
            System.out.println("4. 회의록 삭제");
            System.out.println("b. 뒤로 가기");

            System.out.print("메뉴를 선택하세요: ");
            String choice = scanner.nextLine();
            Long meetingId = null;
            String title = null;
            String description = null;
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            switch (choice) {
                case "1": // 회의록 목록
                    List<Meeting> meetings = meetingService.getMeetingsByProject(projectId);
                    showMeetingList(meetings);
                    System.out.print("\n엔터키를 누르면 회의록 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "2": // 회의록 작성
                    System.out.println("---------- 회의록 작성 ----------");
                    
                    try {
                        System.out.print("회의록 제목: ");
                        title = scanner.nextLine();
                        System.out.print("회의록 내용 (없으면 엔터): ");
                        description = scanner.nextLine();

                        if (title.trim().isEmpty()) {
                            throw new InvalidMeetingInputException("회의록 제목은 비워둘 수 없습니다.");
                        }

                        startTime = parseDateTime("회의 시작 시간 (예: 2025-11-17 14:30): ");
                        endTime = parseDateTime("회의 종료 시간 (예: 2025-11-17 15:30): ");
                        
                        Meeting newMeeting = new Meeting(projectId, title, description, startTime, endTime);
                        
                        if (meetingService.createMeeting(newMeeting)) {
                            System.out.println("회의록이 성공적으로 작성되었습니다.");
                        } else {
                            System.out.println("회의록 작성에 실패했습니다.");
                        }
                    } catch (MeetingException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    
                    System.out.print("\n엔터키를 누르면 회의록 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "3": // 회의록 수정
                    System.out.println("---------- 회의록 수정 ----------");
                    showMeetingList(meetingService.getMeetingsByProject(projectId));
                    System.out.print("수정할 회의록의 번호를 입력하세요: ");
                    
                    try {
                        meetingId = Long.parseLong(scanner.nextLine());
                        Meeting targetMeeting = meetingService.getMeeting(meetingId);

                        if (targetMeeting == null) {
                            System.out.println("오류: 해당 회의록을 찾을 수 없습니다.");
                            break;
                        }

                        String newTitle;
                        while (true) {
                            System.out.print("제목을 수정하시겠습니까? (Y/N, 현재: " + targetMeeting.getTitle() + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 제목 입력: ");
                                newTitle = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newTitle = targetMeeting.getTitle();
                                break;
                            } else { System.out.println("잘못된 입력입니다."); }
                        }

                        String newDescription;
                        while (true) {
                            System.out.print("내용을 수정하시겠습니까? (Y/N): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                System.out.print("수정할 내용 입력: ");
                                newDescription = scanner.nextLine();
                                break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                newDescription = targetMeeting.getDescription();
                                break;
                            } else { System.out.println("잘못된 입력입니다."); }
                        }
                        
                        LocalDateTime newStartTime = targetMeeting.getStartTime();
                        while (true) {
                            System.out.print("시작 시간을 수정하시겠습니까? (Y/N, 현재: " + targetMeeting.getStartTime().format(formatter) + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                newStartTime = parseDateTime("새 시작 시간 (YYYY-MM-DD HH:MM): ");
                                if (newStartTime != null) break; 
                            } else if (choice.equalsIgnoreCase("N")) {
                                break;
                            } else { System.out.println("잘못된 입력입니다."); }
                        }

                        LocalDateTime newEndTime = targetMeeting.getEndTime();
                        while (true) {
                            System.out.print("종료 시간을 수정하시겠습니까? (Y/N, 현재: " + targetMeeting.getEndTime().format(formatter) + "): ");
                            choice = scanner.nextLine();
                            if (choice.equalsIgnoreCase("Y")) {
                                newEndTime = parseDateTime("새 종료 시간 (YYYY-MM-DD HH:MM): ");
                                if (newEndTime != null) break;
                            } else if (choice.equalsIgnoreCase("N")) {
                                break;
                            } else { System.out.println("잘못된 입력입니다."); }
                        }

                        if (newTitle.trim().isEmpty()) {
                            throw new InvalidMeetingInputException("회의록 제목은 비워둘 수 없습니다.");
                        }

                        Meeting updatedMeeting = new Meeting(
                                targetMeeting.getId(), 
                                targetMeeting.getProjectId(), 
                                newTitle, 
                                newDescription,
                                newStartTime,
                                newEndTime
                            );
                            
                            
                            if (meetingService.updateMeeting(updatedMeeting, projectId)) {
                                System.out.println("회의록이 성공적으로 수정되었습니다.");
                            } else {
                                System.out.println("회의록 수정에 실패했습니다.");
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                        } catch (MeetingException e) {
                            System.out.println("[오류] " + e.getMessage());
                        }
                        System.out.print("\n엔터키를 누르면 회의록 메뉴로 돌아갑니다.");
                        scanner.nextLine();
                        break;

                case "4": // 회의록 삭제
                    System.out.println("---------- 회의록 삭제 ----------");
                    showMeetingList(meetingService.getMeetingsByProject(projectId));
                    System.out.print("삭제할 회의록의 ID 번호를 입력하세요: ");

                    try {
                        meetingId = Long.parseLong(scanner.nextLine());
                        
                        if (meetingService.deleteMeeting(meetingId, projectId)) {
                            System.out.println("회의록이 성공적으로 삭제되었습니다.");
                        } else {
                            System.out.println("회의록 삭제에 실패했습니다.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("오류: 유효한 ID 번호를 입력하세요.");
                    } catch (MeetingException e) {
                        System.out.println("[오류] " + e.getMessage());
                    }
                    System.out.print("\n엔터키를 누르면 회의록 메뉴로 돌아갑니다.");
                    scanner.nextLine();
                    break;

                case "b":
                    return;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }

    private void showMeetingList(List<Meeting> meetingList) {
        System.out.println("---------- 회의록 목록 ----------");
        if (meetingList == null || meetingList.isEmpty()) {
            System.out.println("(등록된 회의록이 없습니다.)");
            return;
        }
        
        for (Meeting meeting : meetingList) {
            System.out.println(meeting.getId() + ". " + meeting.getTitle());
            System.out.println("   Time: " + (meeting.getStartTime() != null ? meeting.getStartTime().format(formatter) : "N/A") +
                               " ~ " + (meeting.getEndTime() != null ? meeting.getEndTime().format(formatter) : "N/A"));
            System.out.println("   Desc: " + meeting.getDescription());
            System.out.println();
        }
    }

    private LocalDateTime parseDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            
            if (input.trim().isEmpty()) {
                 return null;
            }
            
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("오류: 날짜 형식이 잘못되었습니다. (YYYY-MM-DD HH:MM)");
            }
        }
    }
}