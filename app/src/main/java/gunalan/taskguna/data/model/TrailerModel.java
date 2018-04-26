package gunalan.taskguna.data.model;

import java.util.ArrayList;

public class TrailerModel {


    private ArrayList<TrailerItemModel> results;
    private String id;


    public ArrayList<TrailerItemModel> getResults() {
        return results;
    }

    public void setResults(ArrayList<TrailerItemModel> results) {
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
