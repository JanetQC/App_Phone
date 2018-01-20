package com.example.janetdo.toomapp.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by janetdo on 26.12.17.
 */

public class ItemCatalog {
private InputStream stream;
private List<Item> catalog = new ArrayList<>();

    public ItemCatalog(InputStream stream) throws Exception{
        this.stream = stream;
        this.initSortiment();
    }

    public void initSortiment() throws Exception {
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();

        String json;
        json = new String(buffer, "UTF-8");
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            String id = "";
            String name = "";
            String description = "";
            String quantity = "";
            String category ="";
            double salePrice = 0;
            double price = 0;
            int aisle = 0;

            id = obj.getString("id");
            name = obj.getString("name");
            description = obj.getString("description");
            category = obj.getString("category");
            price = obj.getDouble("price");
            aisle = obj.getInt("aisle");

            if (obj.has("salePrice")) {
                salePrice = obj.getDouble("salePrice");
            }
            if (obj.has("quantity")) {
                quantity = obj.getString("quantity");
            }


            Item item = new Item(id, name, description, quantity,category, price, salePrice, aisle);
            catalog.add(item);

        }

    }
    public List<Item> searchItemString(final String searchValue){
        List<Item> selectedResults = new ArrayList<>();
        if(searchValue.isEmpty() || searchValue.equals("")){
            return catalog;
        }

        for(int i= 0; i < catalog.size(); i++){
            Item item = catalog.get(i);
            if(item.getDescription().toLowerCase().contains(searchValue) || item.getName().toLowerCase().contains(searchValue)){
                selectedResults.add(item);
            }
        }
     //   selectedResults = catalog.stream().filter(ele -> ele.getDescription().toLowerCase().contains(searchValue.toLowerCase()) ||
       //                                 ele.getName().toLowerCase().contains(searchValue.toLowerCase())).collect(Collectors.<Item>toList());

        return selectedResults;
    }

    public List<Item> searchCategory(final String category){
        List<Item> selectedResults = new ArrayList<>();
     //   selectedResults = catalog.stream().filter(ele -> ele.getCategory().contains(category)).collect(Collectors.<Item>toList());

        return selectedResults;
    }

    public List<Item> getCatalog() {
        return catalog;
    }
}
