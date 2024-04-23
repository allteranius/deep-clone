package demo.deepclone.exceptions;

public class NoSuitableConstructorException extends RuntimeException{
    public NoSuitableConstructorException(String message){
        super(message);
    }
}
