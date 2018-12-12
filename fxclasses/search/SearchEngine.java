package fxclasses.search;

import fxclasses.FxPerson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchEngine {
    private HashMap<Long, FxPerson> sourcelist;          // исходный список
    private HashMap<String, List<FxPerson>> sourcekeys;  // список для поиска - каждое слово разбирается на 1,2,3 буквы и записывается индекс
    private String searchValue;
    private TableView tableView;

    public SearchEngine(TableView tableView) {
        this.sourcekeys = new HashMap<>();
        this.sourcelist = new HashMap<>();
        this.searchValue = "";
        this.tableView = tableView;
    }

    public ObservableList<FxPerson> getFilteredList() {
        ObservableList<FxPerson> o = FXCollections.observableArrayList();
        if(searchValue.equals("")) {
            o.addAll(sourcelist.values());
        }
        else
            if(sourcekeys.get(searchValue.toLowerCase())==null)
                return o; // ничего не найдено
            else
                o.addAll(sourcekeys.get(searchValue.toLowerCase()));
        return o;
    }

    public void addPerson(FxPerson p) {
        String key;
        sourcelist.put(p.getId(), p);
        for(int i=0;i<p.getFirstname().length();i++) {
            key = p.getFirstname().toLowerCase().substring(0, i+1);
            getPersonByKey(p, key);
        }
        for(int i=0;i<p.getSecondname().length();i++) {
            key = p.getSecondname().toLowerCase().substring(0, i+1);
            getPersonByKey(p, key);
        }
        tableView.setItems(getFilteredList());
    }

    private void getPersonByKey(FxPerson p, String key) {
        if(sourcekeys.containsKey(key)) {
            sourcekeys.get(key).add(p);
        }
        else {
            List<FxPerson> lp = new ArrayList<>();
            lp.add(p);
            sourcekeys.put(key, lp);
        }
    }

    public void setSearchValue(String newSearchValue) {
        String oldSearchValue = searchValue;
        searchValue = newSearchValue;
        if(!oldSearchValue.equals(searchValue))
            tableView.setItems(getFilteredList());
    }

    public HashMap<Long, FxPerson> getSourcelist() {
        return sourcelist;
    }

}
