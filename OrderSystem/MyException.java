package OrderSystem;

public class MyException extends RuntimeException{
    /**
     * 无参构造
     */
    public MyException() {}

    /**
     * 有参构造
     */
    public MyException(String message) {
        super(message);
    }


}
