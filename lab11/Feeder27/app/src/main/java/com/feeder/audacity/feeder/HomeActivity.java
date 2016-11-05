package com.feeder.audacity.feeder;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public JSONArray jsonData = null;
    public CaldroidFragment caldroidFragment;
    public boolean syncDone = false;
    HashMap<Date, List<Deadline> > deadlineMap;
    List<Deadline> deadlines = null;
    boolean deadlinesync = false;
    RecyclerView recList = null;
    ListView listView = null;
    List<Deadline> dynamicList = null;
    ArrayAdapter<String> itemsAdapter = null;
    DeadlineAdapter ca = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    // ############ Caldroid ################
        caldroidFragment = new CaldroidFragment();
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        // Get data
        GetData sync = new GetData(Constants.GET_COURSES,this);
        sync.execute();

        //List Addapter
        recList = (RecyclerView) findViewById(R.id.deadline_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


//        listView = (ListView) findViewById(R.id.list_view);
//        itemsAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1,dynamicList);
//        listView.setAdapter(itemsAdapter);
        dynamicList = new ArrayList<>();
        ca = new DeadlineAdapter(dynamicList);
        recList.setAdapter(ca);


        final CaldroidListener listener = new CaldroidListener() {



            @Override
            public void onSelectDate(Date date, View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Toast.makeText(getApplicationContext(), formatter.format(date),
                        Toast.LENGTH_SHORT).show();

                if(deadlinesync) {
                    List<Deadline> temp = deadlineMap.get(date);
                    DeadlineAdapter deadlineAdapter;
                    System.out.println(temp);
                    if (temp != null) {
                        System.out.println("Yeha");
                        dynamicList.clear();
                        dynamicList.addAll(temp);
                        ca.notifyDataSetChanged();
    //                    dynamicList.addAll()
    //                    DeadlineAdapter ca = new DeadlineAdapter(createList(30));
    //                    recList.swapAdapter(ca,true);
                    }
                }
            }
            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Toast.makeText(getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                Toast.makeText(getApplicationContext(),
                        "Caldroid view is created",
                        Toast.LENGTH_SHORT).show();
            }

        };

        caldroidFragment.setCaldroidListener(listener);

    }

    private List<Deadline> createList(int size) {

        List<Deadline> result = new ArrayList<Deadline>();
        for (int i=1; i <= size; i++) {
            Deadline ci = new Deadline();
            ci.deadlineName = Deadline.NAME_PREFIX + i;
            ci.courseName = Deadline.COURSE_PREFIX + i;
            result.add(ci);
        }

        return result;
    }
    private List<String> createStringList(int size) {

        List<String> result = new ArrayList<String>();
        for (int i=1; i <= size; i++) {
            result.add("" + i);
        }
        return result;
    }



    public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.DeadlineViewHolder> {

        private List<Deadline> deadlineList;

        public DeadlineAdapter(List<Deadline> deadlineList) {
            this.deadlineList = deadlineList;
        }

        @Override
        public int getItemCount() {
            return deadlineList.size();
        }

        @Override
        public void onBindViewHolder(DeadlineViewHolder deadlineViewHolder, int i) {
            Deadline ci = deadlineList.get(i);
            deadlineViewHolder.courseName.setText(ci.courseName);
            deadlineViewHolder.assignmentName.setText(ci.deadlineName);
        }

        @Override
        public DeadlineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout, viewGroup, false);

            return new DeadlineViewHolder(itemView);
        }

        public class DeadlineViewHolder extends RecyclerView.ViewHolder {
            protected TextView assignmentName;
            protected TextView courseName;

            public DeadlineViewHolder(View v) {
                super(v);
                assignmentName =  (TextView) v.findViewById(R.id.title);
                courseName = (TextView)  v.findViewById(R.id.courseName);
            }
        }
    }

    public void onBackgroundTaskCompleted(String message, JSONArray jsonData) {
        syncDone = true;
        if(jsonData != null) {
            if (message.equals(Constants.GET_FEEDBACKS)) {
                this.jsonData = jsonData;
                deadlines = null;
                HashMap<Date, Drawable> feedbacks = new HashMap<>();
                List<Deadline> temp = null;
                Deadline tempDeadline = null;
                deadlineMap = new HashMap<>();

                for (int i = 0; i < jsonData.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonData.getJSONObject(i);
                        JSONObject fields = jsonObject.getJSONObject("fields");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat formattertime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        Date date = formatter.parse(fields.optString("deadline").substring(0, 11));
                        Date time = formattertime.parse(fields.optString("deadline"));
                        temp = deadlineMap.get(date);
                        tempDeadline = new Deadline();
                        tempDeadline.courseName = Integer.toString(fields.optInt("course"));
                        tempDeadline.deadlineName = fields.optString("fb_name");
                        tempDeadline.date = time;
                        if (temp == null) {
                            temp = new ArrayList<>();
                            deadlineMap.put(date, temp);
                        }
                        temp.add(tempDeadline);
                        feedbacks.put(date, new ColorDrawable(fields.optInt("course")));
                        caldroidFragment.setBackgroundDrawableForDates(feedbacks);
                        caldroidFragment.refreshView();
                        deadlinesync = true;
                    }
                    catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(deadlineMap);
            } else if (message.equals(Constants.GET_COURSES)) {
                System.out.println(jsonData);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_courses) {

        } else if (id == R.id.nav_assignments) {

        } else if (id == R.id.nav_feedbacks) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
