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

    public class FieldChecker implements View.OnClickListener{
        public void onClick(View v) {

            int gend = rgGender.getCheckedRadioButtonId();
            int acad = rgAcads.getCheckedRadioButtonId();

            if(!(txtName.getText().toString().isEmpty()) && gend != -1 && acad != -1 ){
                String name = txtName.getText().toString().trim();

                int age = Integer.parseInt(spinAge.getSelectedItem().toString());

                rbGender = (RadioButton) findViewById(rgGender.getCheckedRadioButtonId());
                rbAcads = (RadioButton) findViewById(rgAcads.getCheckedRadioButtonId());
                boolean excel = rbAcads.getText().equals("No") ? false : true;
                String gender = rbGender.getText().toString();

                //STUDENT_ID = mStudentRef.push().getKey();
                //Student s = new Student(name, age, gender, excel);
                //mRootRef.child(STUDENT_ID).setValue(s);

                /*mGenderRef.setValue(rbGender.getText());
                mExcelRef.setValue(rbAcads.getText().equals("No") ? false : true);
                mNameRef.setValue(name);
                mAgeRef.setValue(spinAge.getSelectedItem());
*/
                Intent i = new Intent(NameSetter.this, MainActivity.class);
                //i.putExtra("student_id", STUDENT_ID);
                i.putExtra("student_name", name);
                i.putExtra("student_age", age);
                i.putExtra("student_gender", gender);
                i.putExtra("student_excel", excel);
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


    EditText txtName;
    Button btnProceed;
    TextView txtNameWarning;
    private int brightness = 255;
    private ContentResolver cResolver;
    private Window window;

    //private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //public String STUDENT_ID = mRootRef.push().getKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        txtName = (EditText)findViewById(R.id.txtName);
        btnProceed = (Button)findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new FieldChecker());

        rgGender = (RadioGroup)findViewById(R.id.radioGender);
        rgAcads = (RadioGroup)findViewById(R.id.radioLearning);
        spinAge = (Spinner)findViewById(R.id.spinner);

    }

    protected void onStart() {
        super.onStart();
    }
}
