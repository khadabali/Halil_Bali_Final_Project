package com.example.halil_bali_final_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatePicker extends BaseActivity {

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private List<NASAImage> imageList = new ArrayList<>();
    private Adapter adapter;
    private ProgressBar progressBar;
    private String yes, no, alertTitle, clearList, noElements;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues cValues = new ContentValues();
    TextView nameText;
    Button changeName;
    Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datepicker);

        //sets title of page
        setTitle(R.string.DatePickerPage);
        //loads Base activity
        Load();

        nameText = findViewById(R.id.name);
        String newTextView = nameText.getText().toString();

        Intent dataSent = getIntent();
        //getting data from previous page
        String dataName = dataSent.getStringExtra("name");

        //sets the TextView to show the name set from MainActivity to this activity
        //if a name is not set, it just adds an ! at the end
        if (dataName == null) {
            nameText.setText(newTextView + "!");
        }
        //its a name is set, it adds the name with ! at the end
        else {
            nameText.setText(newTextView + " " + dataName + "!");
        }

        changeName = findViewById(R.id.changeName);
        //button click goes to previous activity
        changeName.setOnClickListener(click -> {
            setResult(0, dataSent);
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        });

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        // setting up the listview and listening for changes
        ListView listView = findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        // setup progress bar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        // strings for the alert
        yes = getString(R.string.yes);
        no = getString(R.string.no);
        alertTitle = getString(R.string.alertTitle);

        // initialize database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);

        // load from database
        loadData();

        // upon clicking an item in the listview
        listView.setOnItemClickListener((adapterView, view, pos, l) -> {

            // return image position
            NASAImage nasaImage = imageList.get(pos);

            // gather information of nasaImage
            Bundle b = new Bundle();
            b.putString("DATE", nasaImage.getDate());
            b.putString("TITLE", nasaImage.getTitle());
            b.putString("URL", nasaImage.getUrl());

            // sends the information and user to the fragment
            Intent intent = new Intent(this, EmptyActivity.class);
            intent.putExtras(b);
            startActivity(intent);


        });

        // on long click
        listView.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            // get nasaImage position within the list
            NASAImage nasaImage = imageList.get(pos);

            // alert title
            alert.setTitle(alertTitle)
                    // message
                    .setMessage(nasaImage.getTitle())
                    // yes button to delete nasaImage
                    .setPositiveButton(yes, (click, arg) -> {
                        // delete item and update database
                        deleteImage(nasaImage);
                        imageList.remove(nasaImage);
                        adapter.notifyDataSetChanged();
                    })
                    // no button to not delete nasaImage, does nothing
                    .setNegativeButton(no, (click, arg) -> {
                    })
                    .setView(getLayoutInflater().inflate(R.layout.alert_view, null))
                    .create()
                    .show();
            return true;
        });

        clearList = getString(R.string.clear);
        noElements = getString(R.string.noElements);
        clear = findViewById(R.id.clear_list);
        //clears list of all items
        clear.setOnClickListener(click -> {
            if (imageList.size() > 0) {
                //goes through number of images and deletes them from db
                for(int i = 0; i < imageList.size(); i++){
                    NASAImage image = imageList.get(i);
                    deleteImage(image);
                }
                //clears listview
                imageList.clear();
                //updates db
                adapter.notifyDataSetChanged();
                Toast.makeText(this, clearList, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, noElements, Toast.LENGTH_SHORT).show();
            }

        });

    }

    // initializes the date picker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String stringMonth = getMonthFormat(month);
            String date = makeDateString(year, stringMonth, day);
            String ymd = makeYMD(year, month, day);
            dateButton.setText(date);

            // NASA api url
            final String baseUrl = "https://api.nasa.gov/planetary/apod?api_key=";
            // generated NASA api key
            final String apiKey = "SUmqddAa2liUbdkcKxHvt2Umf2A6Z1a8rNuGkJsc";
            // url to NASA image of the day JSON file
            // with the chosen date
            String parseUrl = baseUrl + apiKey + "&date=" + ymd;
            Log.d("onDateSet()", parseUrl);

            // execute async task after date is picked
            NASA nasa = new NASA();
            nasa.execute(parseUrl);
            // nasa.execute("https://cataas.com/cat?json=true");
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int style = AlertDialog.THEME_HOLO_DARK;


        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        // setting the maximum and minimum date limits in the date picker
        datePickerDialog.getDatePicker().setMinDate(minDate());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    // date minimum is 1995-06-16
    private long minDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(1995, 5, 16);
        return cal.getTimeInMillis();
    }

    // gets today's date
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(year, getMonthFormat(month), day);
    }

    // turns the date into the format yyyy-mmm-dd
    // month is a three letter abbreviation
    private String makeDateString(int year, String month, int day) {
        return month + " " + day + " " + year;
    }

    // format date into yyyy-mm-dd
    private String makeYMD(int year, int month, int day) {
        return year + "-" + month + "-" + day;
    }

    // formats the month into a three letter abbreviation
    private String getMonthFormat(int month) {
        switch (month) {
            case 1:
                return getString(R.string.JAN);
            case 2:
                return getString(R.string.FEB);
            case 3:
                return getString(R.string.MAR);
            case 4:
                return getString(R.string.APR);
            case 5:
                return getString(R.string.MAY);
            case 6:
                return getString(R.string.JUN);
            case 7:
                return getString(R.string.JUL);
            case 8:
                return getString(R.string.AUG);
            case 9:
                return getString(R.string.SEP);
            case 10:
                return getString(R.string.OCT);
            case 11:
                return getString(R.string.NOV);
            case 12:
                return getString(R.string.DEC);
        }
        return null;
    }

    // date picker view
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    //    parses through the NASA image of the date selected JSON
    private class NASA extends AsyncTask<String, Integer, String> {

        // set up progress bar max
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(50);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // get response from host
                InputStream response = request(strings[0]);

                // parse through JSON objects
                String result = parser(response);
                JSONObject obj = new JSONObject(result);

//                get JSON details
                String date = obj.getString("date");
                String url = obj.getString("hdurl");
                String title = obj.getString("title");

//                log JSON details
                Log.d("NASA", date);
                Log.d("NASA", url);
                Log.d("NASA", title);

                // prepare JSON details and insert into database
                cValues.put(DatabaseHelper.COL_DATE, date);
                cValues.put(DatabaseHelper.COL_URL, url);
                cValues.put(DatabaseHelper.COL_TITLE, title);
                long newId = db.insert(DatabaseHelper.TABLE_NAME, null, cValues);

                // increment progress by 1 and sleep for 10 milliseconds
                for (int i = 0; i < 50; i++) {
                    try {
                        progressBar.incrementProgressBy(1);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // create object from JSON details
                // and put the object into imageList
                NASAImage nasaImage = new NASAImage(date, url, title, newId);
                imageList.add(nasaImage);
            } catch (IOException e) {
                Log.d("NASA", "Issue with request/response");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.d("NASA", "Issue parsing JSON");
                e.printStackTrace();
            }

            return null;
        }

        // progress bar
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        // notifies the adapter of changes upon success of doInBackground()
        @Override
        protected void onPostExecute(String s) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
            super.onPostExecute(s);

        }
    }

    //    send request to host
    public InputStream request(String x) throws IOException {
        URL url = new URL(x);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection.getInputStream();
    }

    //    parses through JSON
    public String parser(InputStream response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(response));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    // adapter
    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public NASAImage getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // listview layout
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View v = getLayoutInflater().inflate(R.layout.activity_nasaimage, viewGroup, false);
            TextView title = v.findViewById(R.id.imageTitle);
            TextView date = v.findViewById(R.id.imageDate);
            NASAImage nasaImage = getItem(position);
            title.setText(nasaImage.getTitle());
            date.setText(nasaImage.getDate());
            return v;
        }
    }

    // method to delete image from database
    private void deleteImage(NASAImage i) {
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COL_ID + "= ?", new String[]{Long.toString(i.getId())});
    }

    // load data from database
    private void loadData() {
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // identify columns in database
        String[] columns = {DatabaseHelper.COL_ID,
                DatabaseHelper.COL_DATE,
                DatabaseHelper.COL_URL,
                DatabaseHelper.COL_TITLE};

        // set cursor
        Cursor cursor = db.query(false, DatabaseHelper.TABLE_NAME, columns,
                null, null, null, null, null, null);

        // index columns
        int idColumn = cursor.getColumnIndex(DatabaseHelper.COL_ID);
        int dateColumn = cursor.getColumnIndex(DatabaseHelper.COL_DATE);
        int urlColumn = cursor.getColumnIndex(DatabaseHelper.COL_URL);
        int titleColumn = cursor.getColumnIndex(DatabaseHelper.COL_TITLE);

        // loop through database
        while (cursor.moveToNext()) {
            // gather information from database
            long id = cursor.getLong(idColumn);
            String date = cursor.getString(dateColumn);
            String url = cursor.getString(urlColumn);
            String title = cursor.getString(titleColumn);

            // put information into nasaImage object
            // and put that object into imageList
            imageList.add(new NASAImage(date, url, title, id));
        }
        // close cursor
        cursor.close();
    }


}