package src.member.dto;

public class UpdatePasswordRequestDto {
    String newPassword;
    String confirmNewPassword;

    public UpdatePasswordRequestDto(String newPassword, String confirmNewPassword) {
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
