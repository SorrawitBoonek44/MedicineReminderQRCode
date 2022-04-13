package com.example.medicinereminderqrcode;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicinereminderqrcode.database.MyDatabase;
import com.example.medicinereminderqrcode.medicine.MedicineReminder;
import com.example.medicinereminderqrcode.medicine.MedicineReminderList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    private String ID = null;
    private final int SELECT_PICTURE = 200;
    private String username;
    private static final String TAG = "xxx";
    private DatabaseReference myDatabase;

    Bitmap bitmap = null;

    RecyclerView recyclerView;
    AlertDialog alertDialog;
    View promptView;
    TimePickerDialog timePickerDialog;

    FloatingActionButton addFab,scanFab,importFab,createFab;
    EditText nameEditText;
    NumberPicker portionNumberPicker;
    TextView timeTextView ,scanQrTextView,importQRTextView,createReTextView,dateTextView,emptyTextView;
    Button addReminder,generateQRCodeButton,saveImageButton;
    ImageView qrcodeImageview;
    Boolean optionFabVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUsername();
        MyDatabase database = new MyDatabase();
        myDatabase = database.getDatabase();

        setDate();
        setToolbar();
        setFab();
        getReminderListFromDB(username);
    }

    //set
    private void setUsername(){
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setEmptyTextView(Boolean b){
        emptyTextView = findViewById(R.id.emptyTextView);
        if(b){
            emptyTextView.setVisibility(View.VISIBLE);
        }else {
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint({"InflateParams", "ResourceAsColor"})
    private void setFab(){
        addFab = findViewById(R.id.add_fab);
        importFab = findViewById(R.id.import_qr_fab);
        scanFab = findViewById(R.id.scan_qr_fab);
        createFab = findViewById(R.id.create_reminder_fab);
        scanQrTextView  = findViewById(R.id.scan_qr_text);
        importQRTextView = findViewById(R.id.import_qr_text);
        createReTextView = findViewById(R.id.create_reminder_text);

        setOptionMenuClosed();

        addFab.setOnClickListener(view -> {
            if(optionFabVisible){
                setOptionMenuClosed();
            }else{
                setOptionMenuOpen();
            }
        });
        importFab.setOnClickListener(view -> {
            setOptionMenuClosed();
            chooseImage();
        });
        scanFab.setOnClickListener(view -> {
            setOptionMenuClosed();
            scanQRCode();
        });
        createFab.setOnClickListener(view -> {
            setOptionMenuClosed();
            promptView = getLayoutInflater().inflate(R.layout.createreminder_layout,null);
            setCreateReminderView();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(promptView).create();
            alertDialog = builder.show();
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setOptionMenuOpen(){

        addFab.setImageResource(R.drawable.ic_baseline_add_24);
        addFab.setRotation(45);
        scanFab.setVisibility(View.VISIBLE);
        createFab.setVisibility(View.VISIBLE);
        importFab.setVisibility(View.VISIBLE);
        scanQrTextView.setVisibility(View.VISIBLE);
        importQRTextView.setVisibility(View.VISIBLE);
        createReTextView.setVisibility(View.VISIBLE);

        optionFabVisible = true;
    }

    private void setOptionMenuClosed(){
        addFab.setImageResource(R.drawable.ic_baseline_add_alert_24);
        addFab.setRotation(0);
        //addFab.setBackgroundColor(R.color.design_default_color_secondary);
        scanFab.setVisibility(View.GONE);
        createFab.setVisibility(View.GONE);
        importFab.setVisibility(View.GONE);
        scanQrTextView.setVisibility(View.GONE);
        importQRTextView.setVisibility(View.GONE);
        createReTextView.setVisibility(View.GONE);

        optionFabVisible = false;
    }

    private void setTimepicker() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("DefaultLocale") TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, i, i1) -> setTime(i,i1);
        timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

    }

    @SuppressLint("SimpleDateFormat")
    private void setTime(int i, int i1){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,i);
        calendar.set(Calendar.MINUTE,i1);
        timeTextView.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));
    }

    private void setCreateReminderView(){
        nameEditText = promptView.findViewById(R.id.NameEditText);
        portionNumberPicker = promptView.findViewById(R.id.portionNumberPicker);
        timeTextView = promptView.findViewById(R.id.timeTextView);
        addReminder = promptView.findViewById(R.id.addReminderButton);
        generateQRCodeButton = promptView.findViewById(R.id.generateQRCodeButton);
        if (portionNumberPicker != null) {
            portionNumberPicker.setMinValue(0);
            portionNumberPicker.setMaxValue(10);
            portionNumberPicker.setWrapSelectorWheel(true);
        }

        timeTextView.setOnClickListener(v -> {
            setTimepicker();
            timePickerDialog.show();
        });
        addReminder.setOnClickListener(v -> {
            if(checkFillAllInfo()){
                addReminderToDBByUser(username, generateID(), createMedicineReminder());
                Toast.makeText(this,"successfully",Toast.LENGTH_SHORT).show();
            }
        });

        generateQRCodeButton.setOnClickListener(v -> {
            if(checkFillAllInfo()){
                generateQRCode();
                setQRCodeView();
            }
        });

    }

    private void setQRCodeView() {
        if(ID == null || bitmap == null){
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.qrcode_layout,null);
        qrcodeImageview = view.findViewById(R.id.qrcode_imageView);
        qrcodeImageview.setImageBitmap(bitmap);
        saveImageButton = view.findViewById(R.id.saveButton);
        saveImageButton.setOnClickListener(v -> {
            saveImage(bitmap,ID);
            Toast.makeText(MainActivity.this,"Save!",Toast.LENGTH_SHORT).show();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view).create();
        alertDialog = builder.show();
    }

    public void setRecyclerView(ArrayList<MedicineReminder> medicineReminders){
        setDate();
        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(medicineReminders);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    public void setDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText("Date: " + simpleDateFormat.format(new Date()));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_user);
        menuItem.setTitle(username);
        Toast.makeText(MainActivity.this,username+" is Logged in",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        addIDToDB(username,decoderQRCode(uriToBitmap(selectedImageUri)));
                        Toast.makeText(this,"successfully",Toast.LENGTH_SHORT).show();
                    } catch (FormatException | NotFoundException | ChecksumException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                addIDToDB(username,intentResult.getContents());
                Toast.makeText(this,"successfully",Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @SuppressLint("ResourceType")
    private Boolean checkFillAllInfo(){
        String MedicineName = nameEditText.getText().toString();
        double portion = portionNumberPicker.getValue();
        if (MedicineName.equals("") || portion == 0){
            Toast.makeText(MainActivity.this,"Please fill out all information",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //create random ID,Medicine Reminder,
    private String generateID(){
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    private MedicineReminder createMedicineReminder(){
        return new MedicineReminder(nameEditText.getText().toString(),portionNumberPicker.getValue(),timeTextView.getText().toString());
    }

    //Database
    public void getReminderListFromDB(String username){
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> reminderIDList = new ArrayList<>();
                MedicineReminderList medicineReminders= new MedicineReminderList();
                for (DataSnapshot postSnapshot : dataSnapshot.child(username).getChildren()) {
                    String s = postSnapshot.getValue(String.class);
                    reminderIDList.add(s);
                }
                for(String s : reminderIDList){
                    DataSnapshot snapshot = dataSnapshot.child("MedicineReminder").child(s);
                    MedicineReminder medicineReminder = snapshot.getValue(MedicineReminder.class);
                    if(medicineReminder != null){
                        medicineReminders.add(medicineReminder);
                    }
                }if(medicineReminders.size() != 0){
                    setRecyclerView(medicineReminders.getMedicineReminderArrayListSortByNotOutOfTime());
                    setEmptyTextView(false);
                    if(medicineReminders.getNotOutOfTimeList().size() != 0){
                        setAlarm(medicineReminders.getNotOutOfTimeList().get(0));
                    }
                    new Handler().postDelayed(() -> getReminderListFromDB(username),60000);
                }else{
                    setRecyclerView(new ArrayList<>());
                    setEmptyTextView(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void addReminderToDB( String ID, MedicineReminder medicineReminder){
        myDatabase.child("MedicineReminder").child(ID).setValue(medicineReminder);
    }

    public void addReminderToDBByUser(String username, String ID, MedicineReminder medicineReminder){
        addReminderToDB(ID,medicineReminder);
        addIDToDB(username,ID);
    }

    public void addIDToDB(String username,String ID) {
        myDatabase.child(username).child(ID).setValue(ID);
    }

    //Notification
    @SuppressLint("UnspecifiedImmutableFlag")
    private void setAlarm(MedicineReminder medicineReminder) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MyAlarmBroadCast.class);
        intent.putExtra("title", medicineReminder.getMedicineName());
        intent.putExtra("text", medicineReminder.toString());
        intent.putExtra("username",username);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Log.i(TAG, "setAlarm: "+ medicineReminder.getTime("HH:mm"));
        alarmManager.set(AlarmManager.RTC_WAKEUP, medicineReminder.getTimeInMillis(), pendingIntent);
    }

    //generate qrcode
    private void generateQRCode() {
        ID = generateID();
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(ID, null, QRGContents.Type.TEXT, dimen);
        try {
            addReminderToDB(ID, createMedicineReminder());
            bitmap = qrgEncoder.encodeAsBitmap();
            setQRCodeView();
        } catch (WriterException e) {
            Log.e("Tag", e.toString());
        }
    }

    //import qrcode
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    private Bitmap uriToBitmap(Uri selectedImageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
    }

    //scan qrcode
    private void scanQRCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setPrompt("Scan QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    //qrcode decoder
    private String decoderQRCode(Bitmap bitmap) throws ChecksumException, NotFoundException, FormatException {
        QRCodeReader qrCodeReader = new QRCodeReader();
        assert bitmap != null;
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        return qrCodeReader.decode(binaryBitmap).toString();
    }

    //save qrcode to pictures
    @SuppressLint("ObsoleteSdkInt")
    private void saveImage(Bitmap bitmap, String id) {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "QRCode/" + id);
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, this.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    this.getContentResolver().update(uri, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + "QRCode/");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}