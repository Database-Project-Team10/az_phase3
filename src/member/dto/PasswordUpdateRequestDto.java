package src.member.dto;

public class PasswordUpdateRequestDto {
    String newPassword;
    String confirmNewPassword;

    public PasswordUpdateRequestDto(String newPassword, String confirmNewPassword) {
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }
}
