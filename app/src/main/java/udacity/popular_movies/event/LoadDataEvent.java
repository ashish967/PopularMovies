package udacity.popular_movies.event;

public class LoadDataEvent {
    public int mPage;

    public LoadDataEvent(int page) {
        this.mPage = page;
    }
}