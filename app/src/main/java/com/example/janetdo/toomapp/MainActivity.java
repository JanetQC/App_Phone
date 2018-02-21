package com.example.janetdo.toomapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.FirebaseInstanceIDService;
import com.example.janetdo.toomapp.Helper.Incident;
import com.example.janetdo.toomapp.Helper.Item;
import com.example.janetdo.toomapp.Helper.Catalog;
import com.example.janetdo.toomapp.Helper.ListHolder;
import com.example.janetdo.toomapp.Helper.Problem;
import com.example.janetdo.toomapp.Helper.User;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView fancySearchView;
    private Catalog catalog;
    private Catalog problemCatalog;
    private Catalog incidentCatalog;
    private List<Item> salesCatalog;
    private Button btnViewProblem;
    private Button btnMap;
    private Button btnWorker;
    private Button btnProblem;
    private Button btnScan;
    private ImageView msgIcon;
    private TextView numberMsg;
    private CloudantService cloudantService;
    private TableLayout tableLayout;
    private List<Item> searchResults;
    private Switch workerSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cloudantService = new CloudantService();
        fetchMainViewElements();
        setDefaultView();
        checkCurrentState();
        createItemCatalog();
        createSalesItemCatalog();

        FirebaseInstanceIDService service = new FirebaseInstanceIDService();
        FirebaseMessaging.getInstance().subscribeToTopic("worker");


    }

    private void createItemCatalog() {
        try {
            catalog = new Catalog(getAssets().open("rawData.json"));
            catalog.initSortiment();
            problemCatalog = new Catalog(getAssets().open("problems.json"));
            incidentCatalog = new Catalog(getAssets().open("flaws.json"));
            problemCatalog.readProblems();
            incidentCatalog.readFlaws();
        } catch (Exception e) {
            System.out.println("Cannot read JSON file: " + e);
        }

    }

    private void createSalesItemCatalog() {
        List<Item> items = catalog.getCatalog();
        salesCatalog = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getSalesPrice() != 0) {
                salesCatalog.add(items.get(i));
            }
        }
    }

    private void checkCurrentState() {
        if (!User.isUser()) {
            workerSwitch.setChecked(true);
        }
    }

    private void fetchMainViewElements() {
        workerSwitch = (Switch) findViewById(R.id.switch1);
        fancySearchView = (SearchView) findViewById(R.id.fancySearch);
        btnViewProblem = (Button) findViewById(R.id.btnViewProblem);
        btnMap = (Button) findViewById(R.id.btnMap);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnWorker = (Button) findViewById(R.id.btnWorker);
        btnProblem = (Button) findViewById(R.id.btnProblem);
        msgIcon = (ImageView) findViewById(R.id.msg_icon);
        numberMsg = findViewById(R.id.numberMsg);
        tableLayout = findViewById(R.id.searchLayout);

        numberMsg.setVisibility(View.GONE);
        btnViewProblem.setVisibility(View.GONE);
        msgIcon.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);


        setSearchListeners();
    }

    private void setDefaultView() {
        workerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isAdmin) {

                if (isAdmin) {
                    showAllProblems(buttonView);
                } else {
                    btnMap.setVisibility(View.VISIBLE);
                    btnWorker.setVisibility(View.VISIBLE);
                    btnProblem.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.VISIBLE);
                    btnViewProblem.setVisibility(View.GONE);
                    msgIcon.setVisibility(View.GONE);
                    numberMsg.setVisibility(View.GONE);
                    tableLayout.setVisibility(View.GONE);
                    User.setIsUser(true);
                }
            }
        });

        fancySearchView.setSubmitButtonEnabled(true);
        fancySearchView.setOnQueryTextListener(this);

    }

    public void clickBtn(View view) {
        System.out.println("I was clicked!!!!");
        System.out.println(view.getId());
    }

    private void setSearchListeners() {
        fancySearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tableLayout.setVisibility(View.GONE);
                isSearch(false);
                return false;
            }
        });
        fancySearchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTableSearch("");
                tableLayout.setVisibility(View.VISIBLE);
                isSearch(true);
            }

        });
        ImageView greenRect = findViewById(R.id.rectimage);
        greenRect.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.GONE);
                isSearch(false);
            }

        });
    }

    private void checkForNewMessages() {
        cloudantService.checkUnreadMessages();
        numberMsg.setText(Integer.toString(cloudantService.getNewProblems().size()));
        cloudantService.setMessagesToRead();
    }

    public void showTableSearch(String s) {
        tableLayout.removeAllViews();
        String search = s.trim().toLowerCase();
        searchResults = new ArrayList<>();
        searchResults = catalog.searchItemString(search);


        for (int i = 0; i < searchResults.size(); i++) {
           final int position = i;
            TableRow tableRow = new TableRow(this);
            tableRow.setId(i);
            tableRow.setClickable(true);
            tableRow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int rowId = v.getId();
                    System.out.println("Row clicked: " + v.getId());
                    showPopupWindow(searchResults.get(rowId));


                }
            });

            Item item = searchResults.get(i);
            TextView view1 = new TextView(this);
            view1.setText(item.getName());

            ImageView compass = new ImageView(this);
            compass.setImageDrawable(getDrawable(R.drawable.compass));
            compass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(position);
                    Item item = searchResults.get(position);
                    String category = item.getCategory();
                }
            });

            ImageView info = new ImageView(this);
            info.setImageDrawable(getDrawable(R.drawable.info));

            view1.setTextSize(25);
            view1.setPadding(30, 10, 0, 10);
            info.setPadding(0, 10, 20, 10);
            compass.setPadding(0, 10, 0, 10);
            compass.setScaleX(1.5f);
            compass.setScaleY(1.5f);
            info.setScaleX(1.8f);
            info.setScaleY(1.8f);

            tableRow.addView(view1);
            tableRow.addView(compass);
            tableRow.addView(info);
            tableRow.setGravity(Gravity.LEFT);
            TableRow.LayoutParams params = new TableRow.LayoutParams(1000, 50);
            tableRow.setLayoutParams(params);
            tableLayout.addView(tableRow);
        }
    }

    private void showPopupWindow(Item item) {
        TableLayout searchLayout = (TableLayout)
                findViewById(R.id.searchLayout);

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, 900, 400, focusable);

        popupWindow.showAtLocation(searchLayout, Gravity.CENTER, 0, 0);

        ImageView itemPic;
        TextView price = popupView.findViewById(R.id.price);
        TextView name = popupView.findViewById(R.id.textName);
        TextView desc = popupView.findViewById(R.id.textDesc);

        price.setTextColor(getResources().getColor(R.color.orange));
        price.setText(Double.toString(item.getPrice()) + " €");
        itemPic = popupView.findViewById(R.id.itemPic);
        itemPic.setBackground(setItemPic(item));

        desc.setText(item.getDescription());
        name.setText(item.getName());
        price.setTextSize(40);
        name.setTextSize(35);
        desc.setTextSize(25);


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private Drawable setItemPic(Item item) {
        ImageView view = new ImageView(this);
        Drawable drawable = null;
        switch (item.getCategory().toLowerCase()) {
            case "bodenbelag":
                drawable = getDrawable(R.drawable.bodenbelag);
                break;
            case "pflanzen":
                drawable = getDrawable(R.drawable.pflanzen);
                break;
            case "lacke":
                drawable = getDrawable(R.drawable.category_paint);
                break;
            case "garten":
                drawable = getDrawable(R.drawable.garten);
                break;
                /*
            case "zement":
                drawable = getDrawable(R.drawable.zement);
                break;
            case "bauzubehör":
                drawable = getDrawable(R.drawable.bauzubehoer);
                break;
            case "styroporleisten":
                drawable = getDrawable(R.drawable.styroporleisten);
                break;
            case "baustoffe":
                drawable = getDrawable(R.drawable.baustoffe);
                break;
            case "dämmstoffe":
                drawable = getDrawable(R.drawable.daemmstoffe);
                break;
                */
            default:
                drawable = getDrawable(R.drawable.sonstiges);


        }
        return drawable;

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        showTableSearch(s);

        tableLayout.setVisibility(View.VISIBLE);
        isSearch(true);

        return true;
    }

    private void isSearch(boolean isSearch) {
        int visible = View.VISIBLE;
        if (isSearch) {
            visible = View.GONE;
        }
        if (User.isUser()) {
            btnMap.setVisibility(visible);
            btnWorker.setVisibility(visible);
            btnProblem.setVisibility(visible);
            btnScan.setVisibility(visible);

        } else {
            msgIcon.setVisibility(visible);
            btnViewProblem.setVisibility(visible);
        }
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public void openMap(View view) throws Exception {
        Intent intent = new Intent(this, MapActivity.class);
        ListHolder holder = new ListHolder();
        holder.initItemList(salesCatalog);
        intent.putExtra("salesPrice", holder);
        startActivity(intent);
    }

    public void openWorker(View view) {
        Intent intent = new Intent(this, WorkerActivity.class);
        startActivity(intent);
    }

    public void openProblem(View view) {
        Intent intent = new Intent(this, IncidentActivity.class);
        startActivity(intent);
    }

    public void openScannerView(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void viewIncidentList(View view) {
         // List<Incident> incidents = cloudantService.getAllIncidents();
        List<Incident> incidents = incidentCatalog.getIncidents();
        Intent intent = new Intent(this, IncidentActivity.class);
        intent.putExtra("layout", R.layout.test);

        ListHolder holder = new ListHolder();
        holder.initIncidentList(incidents);
        intent.putExtra("incidentsList", holder);
        startActivity(intent);
    }

    public void showAllProblems(View view) {
          //  List<Problem> problems = cloudantService.getAllProblems();
        List<Problem> problems = problemCatalog.getProblems();
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("layout", R.layout.test);

        ListHolder holder = new ListHolder();
        holder.initProblemList(problems);
        intent.putExtra("problemsList", holder);
        startActivity(intent);
    }

    public void goHome(View view) {
        Toast.makeText(getApplicationContext(), "Ihre Nachricht wurde versandt.",
                Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);
        isSearch(false);
    }

    public void clickArea(View view) {
        tableLayout.setVisibility(View.GONE);
        isSearch(false);
    }

}
