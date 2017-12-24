package subatom.eden_beta;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.MediaStore;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NameSetter extends Activity {

    public class Proceed implements View.OnClickListener{
        public void onClick(View v) {

            int gend = rgGender.getCheckedRadioButtonId();
            int acad = rgAcads.getCheckedRadioButtonId();

            if(!(txtName.getText().toString().isEmpty()) && gend != -1 && acad != -1 ){
                user_name = txtName.getText().toString().trim();

                int age = Integer.parseInt(spinAge.getSelectedItem().toString());

                rbGender = (RadioButton) findViewById(rgGender.getCheckedRadioButtonId());
                rbAcads = (RadioButton) findViewById(rgAcads.getCheckedRadioButtonId());
                boolean excel = rbAcads.getText().equals("No") ? false : true;
                String gender = rbGender.getText().toString();
                Student s = new Student(STUDENT_ID, user_name, age, gender, excel);
                Intent i = new Intent(NameSetter.this, MainActivity.class);
                i.putExtra("student_id", STUDENT_ID);
                i.putExtra("student_name", user_name);
                startActivity(i);

            }
            else{
                Toast.makeText(NameSetter.this, "All fields must be filled", Toast.LENGTH_LONG).show();
            }
        }
    }

    RadioGroup rgGender, rgAcads;
    RadioButton rbGender, rbAcads;
    Spinner spinAge;


    public String user_name = null;
    public String STUDENT_ID;
    EditText txtName;
    Button btnProceed;
    TextView txtNameWarning;
    private int brightness = 255;
    private ContentResolver cResolver;
    private Window window;

    /*DatabaseReference mStudentRef = FirebaseDatabase.getInstance().getReference("students");*/
    /*DatabaseReference mNameRef = mRootRef.child("name");
    DatabaseReference mAgeRef = mRootRef.child("age");
    DatabaseReference mGenderRef = mRootRef.child("gender");
    DatabaseReference mExcelRef = mRootRef.child("excel");*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        txtName = (EditText)findViewById(R.id.txtName);
        btnProceed = (Button)findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new Proceed());

        rgGender = (RadioGroup)findViewById(R.id.radioGender);
        rgAcads = (RadioGroup)findViewById(R.id.radioLearning);
        spinAge = (Spinner)findViewById(R.id.spinner);

    }

    protected void onStart() {
        super.onStart();
    }
}
