package subatom.eden_beta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import subatom.eden_beta.R;
import subatom.eden_beta.model.Student;

public class NameSetter extends Activity {
    RadioGroup rgGender, rgAcads;
    RadioButton rbGender, rbAcads;
    Spinner spinAge;

    public String user_name = null;
    public String STUDENT_ID;
    EditText txtName;
    Button btnProceed;

    DatabaseReference mStudentRef = FirebaseDatabase.getInstance().getReference("students");
    String key = mStudentRef.push().getKey();

    static NameSetter INSTANCE; //to pass the key value directly to the Statistics activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        INSTANCE = this;
        txtName = (EditText)findViewById(R.id.txtName);
        btnProceed = (Button)findViewById(R.id.btnProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    mStudentRef.child(key).setValue(s);
                    Intent i = new Intent(NameSetter.this, MainActivity.class);

                    startActivity(i);

                }
                else{
                    Toast.makeText(NameSetter.this, "All fields must be filled", Toast.LENGTH_LONG).show();
                }
            }
        });

        rgGender = (RadioGroup)findViewById(R.id.radioGender);
        rgAcads = (RadioGroup)findViewById(R.id.radioLearning);
        spinAge = (Spinner)findViewById(R.id.spinner);
    }

    public static NameSetter getActivityInstance() {return INSTANCE;}
    public String getKey() {return this.key;}



}
