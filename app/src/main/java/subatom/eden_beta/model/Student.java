package subatom.eden_beta.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by JSP on 11/27/2017.
 */

public class Student implements Serializable{
    private String id;
    private String name;
    private int age;
    private boolean excel;
    private String gender;

    public Student(String id, String name, int age, String gender, boolean excel) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.excel = excel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isExcel() {
        return excel;
    }

    public void setExcel(boolean excel) {
        this.excel = excel;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }
}
