package com.example.janetdo.toomapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.janetdo.toomapp.Helper.Catalog;
import com.example.janetdo.toomapp.Helper.CloudantService;
import com.example.janetdo.toomapp.Helper.Incident;
import com.example.janetdo.toomapp.Helper.Item;
import com.example.janetdo.toomapp.Helper.ListHolder;
import com.example.janetdo.toomapp.Helper.Problem;
import com.example.janetdo.toomapp.Helper.Properties;
import com.example.janetdo.toomapp.Helper.SearchAdapter;
import com.example.janetdo.toomapp.Helper.User;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView fancySearchView;
    private static Catalog catalog;
    private Catalog problemCatalog;
    private Catalog incidentCatalog;
    private static List<Item> salesCatalog;
    private Button btnMap;
    private Button btnWorker;
    private Button btnProblem;
    private Button btnScan;
    private CloudantService cloudantService;
    private ListView searchList;
    private List<Item> searchResults;
    private Switch workerSwitch;
    private NetworkInfo activeNetwork;
    public static Context context;

    private Properties props;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();
        setContentView(R.layout.activity_main);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        cloudantService = new CloudantService();

        initViewElements();
        setDefaultView();
        // checkCurrentState();
        createItemCatalog();
        createSalesItemCatalog();

        FirebaseMessaging.getInstance().subscribeToTopic("client");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("worker");
        Properties.getInstance().setAdmin(false);


    }

    public static Context getContext() {
        return context;
    }

    private void createItemCatalog() {
        try {
            catalog = new Catalog(getAssets().open("product_data.json"));
            catalog.initSortiment();
            problemCatalog = new Catalog(getAssets().open("problems.json"));
            incidentCatalog = new Catalog(getAssets().open("flaws.json"));
            problemCatalog.readProblems();
            incidentCatalog.readFlaws();
        } catch (Exception e) {
            System.out.println("Cannot read JSON file: " + e);
        }

    }
    @Override
    protected void onResume() {
        workerSwitch.setChecked(false);
        super.onResume();
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

    private void initViewElements() {
        workerSwitch = (Switch) findViewById(R.id.switch1);
        workerSwitch.setChecked(false);
        fancySearchView = (SearchView) findViewById(R.id.fancySearch);
        btnMap = (Button) findViewById(R.id.btnMap);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnWorker = (Button) findViewById(R.id.btnWorker);
        btnProblem = (Button) findViewById(R.id.btnProblem);
        searchList = findViewById(R.id.searchList);
        searchList.setVisibility(View.GONE);
        initPressListener(btnWorker);
        initPressListener(btnProblem);
        initPressListener(btnMap);
        initPressListener(btnScan);
        initSearchListener();
    }

    private void initPressListener(Button button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }


    private void setDefaultView() {
        workerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean userIsAdmin) {

                if (userIsAdmin) {
                    FirebaseMessaging.getInstance().subscribeToTopic("worker");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("client");
                    Properties.getInstance().setAdmin(true);
                    showAllProblems(buttonView);
                    System.out.println("WORKER VIEW");
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic("client");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("worker");
                    btnMap.setVisibility(View.VISIBLE);
                    btnWorker.setVisibility(View.VISIBLE);
                    btnProblem.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.GONE);
                    Properties.getInstance().setAdmin(false);
                }
            }
        });

        fancySearchView.setSubmitButtonEnabled(true);
        fancySearchView.setOnQueryTextListener(this);

    }

    private void initSearchListener() {
        RelativeLayout relativeLayout = findViewById(R.id.mainLayout);
        ImageView greenRect = findViewById(R.id.rectimage);
        fancySearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchList.setVisibility(View.GONE);
                isSearch(false);
                return false;
            }
        });
        fancySearchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTableSearch("");
                searchList.setVisibility(View.VISIBLE);
                isSearch(true);
            }

        });

        relativeLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        greenRect.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.setVisibility(View.GONE);
                isSearch(false);
                hideKeyboard(v);
            }

        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showTableSearch(String s) {
        String searchVal = s.trim().toLowerCase();
        searchResults = new ArrayList<>();
        searchResults = catalog.searchItemString(searchVal);
        String[] stuff = new String[searchResults.size()];
        ListView lView = (ListView) findViewById(R.id.searchList);
        SearchAdapter customizedAdapter;
        customizedAdapter = new SearchAdapter(MainActivity.this, searchResults, stuff);
        lView.setAdapter(customizedAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopupWindow(searchResults.get(i), i);
                View rowView = adapterView.getAdapter().getView(i, view, null);
                System.out.println("start");

            }
        });
    }

    public static void setNavigationOnClickListener(ImageView view, int position) {
        System.out.println(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position: ");
                Intent intent = new Intent(MainActivity.getContext(), MapActivity.class);
                ListHolder holder = new ListHolder();
                holder.initItemList(salesCatalog);
                intent.putExtra("salesPrice", holder);
                Item item = catalog.getCatalog().get(position);
                intent.putExtra("category", item.getCategory());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.getContext().startActivity(intent);

            }
        });

    }

    private void showPopupWindow(Item item, int position) {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, focusable);
        RelativeLayout relativeLayout = findViewById(R.id.mainLayout);
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        ImageView itemPic;
        TextView price = popupView.findViewById(R.id.price);
        TextView name = popupView.findViewById(R.id.textName);
        TextView desc = popupView.findViewById(R.id.textDesc);
        ImageView navi = popupView.findViewById(R.id.navi);
        setNavigationOnClickListener(navi, position);


        if (item.getSalesPrice() != 0.0) {
            TextView salesPrice = popupView.findViewById(R.id.salesPrice);
            salesPrice.setVisibility(View.VISIBLE);

            salesPrice.setText(String.format("%.2f", item.getSalesPrice()) + " €");
            salesPrice.setTextSize(40);
            salesPrice.setTextColor(getResources().getColor(R.color.darkRed));
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        price.setText(String.format("%.2f", item.getPrice()) + " €");
        price.setTextColor(getResources().getColor(R.color.black));

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
                hideKeyboard(v);
                return true;
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        showTableSearch(s);
        searchList.setVisibility(View.VISIBLE);
        isSearch(true);

        return true;
    }

    private void isSearch(boolean isSearch) {
        int visible = View.VISIBLE;
        if (isSearch) {
            visible = View.GONE;
        }
        //TODO: correct logic?
        btnWorker.setVisibility(visible);
        btnProblem.setVisibility(visible);
        btnScan.setVisibility(visible);
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

    public void showAllProblems(View view) {
        List<Incident> incidents;
        List<Problem> problems;
        boolean isWiFi = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        if (isWiFi) {
            incidents = cloudantService.getAllIncidents();
            problems = cloudantService.getAllProblems();
        } else {
            incidents = incidentCatalog.getIncidents();
            problems = problemCatalog.getProblems();
        }
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("layout", R.layout.incident_list);

        ListHolder holder = new ListHolder();
        holder.initProblemList(problems);
        holder.initIncidentList(incidents);
        intent.putExtra("problemsList", holder);
        startActivity(intent);
    }

    private Drawable setItemPic(Item item) {
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
            case "zement":
                drawable = getDrawable(R.drawable.zement);
                break;
            case "bauzubehoer":
                drawable = getDrawable(R.drawable.bauzubehoer);
                break;
            case "styroporleisten":
                drawable = getDrawable(R.drawable.styroporleisten);
                break;
            case "baustoffe":
                drawable = getDrawable(R.drawable.baustoffe);
                break;
            case "daemmungen":
                drawable = getDrawable(R.drawable.daemmstoffe);
                break;
            case "leuchten":
                drawable = getDrawable(R.drawable.lampen);
                break;
            default:
                drawable = getDrawable(R.drawable.sonstiges);


        }
        return drawable;

    }


}
