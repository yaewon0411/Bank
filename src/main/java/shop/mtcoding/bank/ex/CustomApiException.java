package shop.mtcoding.bank.ex;

public class CustomApiException extends RuntimeException{
    public CustomApiException(String message){
        super(message);
    }
}
