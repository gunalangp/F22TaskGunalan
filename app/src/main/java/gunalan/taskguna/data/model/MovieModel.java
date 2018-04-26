package gunalan.taskguna.data.model;

import java.util.ArrayList;

public class MovieModel {

   private ArrayList<MovieDetailModel> results;
   private String total_results;
   private String total_pages;
   private String page;


    public ArrayList<MovieDetailModel> getResults() {
        return results;
    }

    public void setResults(ArrayList<MovieDetailModel> results) {
        this.results = results;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
