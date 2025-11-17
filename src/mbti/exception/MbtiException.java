package src.mbti.exception;

public abstract class MbtiException extends RuntimeException {

  public MbtiException(String message) {
    super(message);
  }
}