package src.utils;

import java.util.Scanner;

public class InputUtil {
    public static class CancelException extends RuntimeException {
    }
    public static String getInput(Scanner scanner, String prompt) {
        System.out.print(prompt + " (취소: q): ");
        String input = scanner.nextLine().trim();

        if ("q".equalsIgnoreCase(input)) {
            throw new CancelException(); // 비상 탈출!
        }
        return input;
    }

    public static Long getLong(Scanner scanner, String prompt) {
        while (true) {
            try {
                String input = getInput(scanner, prompt);
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("올바른 숫자를 입력해주세요.");
            }
        }
    }
}