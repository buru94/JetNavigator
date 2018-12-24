package com.example.pimz.jetnavigator;


import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

public class Session  extends AppCompatActivity {
    private static Session session;


    private Session() {
    }

    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    private String AuthCode = null;
    private int Pos_product_trans = 0;
    private int  Pos_ignore_hold = 0;
    private String Svc_enddate = null;
    private String Svc_version = null;
    private String UserName = null;
    private ArrayList Name = null;
    private Integer Page= null;
    private int is_ecn_use = 0;




    public String getAuthCode() {
        return AuthCode;
    }

    public void setAuthCode(String AuthCode) {
        this.AuthCode = AuthCode;
    }

    public Integer getPos_product_trans() {
        return Pos_product_trans;
    }

    public void setPos_product_trans(Integer pos_product_trans) {
        this.Pos_product_trans = Pos_product_trans;
    }

    public Integer getPos_ignore_hold() {
        return Pos_ignore_hold;
    }

    public void setPos_ignore_hold(Integer Pos_ignore_hold) {
        this.Pos_ignore_hold = Pos_ignore_hold;
    }

    public String getSvc_enddate() {
        return Svc_enddate;
    }

    public void setSvc_enddate(String Svc_enddate) {
        this.Svc_enddate = Svc_enddate;
    }

    public String getSvc_version() {
        return Svc_version;
    }

    public void setSvc_version(String Svc_version) {
        this.Svc_version = Svc_version;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) { this.UserName = UserName; }


    public ArrayList getName() {
        return Name;
    }

    public void setName(ArrayList Name) {
        this.Name = Name;
    }

    public Integer getPage() {
        return Page;
    }

    public void setPage(Integer Page) {
        this.Page = Page;
    }

    public Integer getIs_ecn_use() {
        return is_ecn_use;
    }

    public void setIs_ecn_use(Integer is_ecn_use) {
        this.is_ecn_use = is_ecn_use;
    }

}