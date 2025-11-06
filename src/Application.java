package src;

import src.main.MainController;

public class Application {
    public static void main(String[] args) {
        MainController mainController = new MainController();

        mainController.run();

        System.out.println("프로그램을 종료합니다.");
    }
}
