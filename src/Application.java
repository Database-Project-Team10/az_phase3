package src;

import src.main.MainController;

public class Application {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();   // DI 컨테이너

        MainController mainController = appConfig.mainController();
        mainController.run();

        System.out.println("프로그램을 종료합니다.");
    }
}