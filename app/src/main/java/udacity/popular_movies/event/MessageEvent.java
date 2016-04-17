package udacity.popular_movies.event;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class MessageEvent {

    boolean success;
    String message;

    public MessageEvent(boolean success, String message){

        this.success=success;
        this.message=message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
