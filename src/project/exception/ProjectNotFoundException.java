package src.project.exception;

public class ProjectNotFoundException extends ProjectException {
    public ProjectNotFoundException() {
      super("해당 프로젝트를 찾을 수 없습니다.");
    }
}
