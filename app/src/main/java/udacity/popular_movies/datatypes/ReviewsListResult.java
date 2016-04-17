package udacity.popular_movies.datatypes;

import java.util.ArrayList;

/**
 * Created by ashish-novelroots on 16/4/16.
 */
public class ReviewsListResult {

    String id;
    int page;
    ArrayList<ReviewType> results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<ReviewType> getResults() {
        return results;
    }

    public void setResults(ArrayList<ReviewType> results) {
        this.results = results;
    }
}
