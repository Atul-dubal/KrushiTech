package apcoders.in.krushitech.models;

import android.util.Log;

public class User_Farmer_Model {
    String user_id, full_name, email, phone_number, gender, state,user_avatar_url,user_type;

//    public User_Farmer_Model(String user_id, String full_name, String email, String phone_number, String gender, String state) {
//        this.full_name = full_name;
//        this.user_id = user_id;
//        this.email = email;
//        this.phone_number = phone_number;
//        this.gender = gender;
//        this.state = state;
//    }

    public User_Farmer_Model(String user_id, String full_name, String email, String phone_number, String gender,String user_type, String state, String user_avatar_url) {
        this.user_id = user_id;
        this.full_name = full_name;
        this.email = email;
        this.phone_number = phone_number;
        this.gender = gender;
        this.user_type = user_type;
        this.state = state;
        this.user_avatar_url = user_avatar_url;
    }

    public User_Farmer_Model() {

    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_avatar_url() {
        return user_avatar_url;
    }

    public void setUser_avatar_url(String user_avatar_url) {
        this.user_avatar_url = user_avatar_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void displayData() {
        Log.d("User Id :", this.user_id);
        Log.d("Full Name :", this.full_name);
        Log.d("Phone Number :", this.phone_number);
        Log.d("Email :", this.email);
        Log.d("Gender :", this.gender);
        Log.d("State :", this.state);

    }
}
