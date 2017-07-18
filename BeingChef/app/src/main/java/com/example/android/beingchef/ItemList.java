package com.example.android.beingchef;

/**
 * Created by Pallav on 7/19/2017.
 */
import com.google.firebase.database.IgnoreExtraProperties;

public class ItemList {
    public String itemName="";
    public String itemDesc="";
    public String itemPrice="";
    //public String itembought="";

    public ItemList(){}

    public ItemList(String itemName,String itemDesc,String itemPrice){
        this.itemName=itemName;
        this.itemDesc=itemDesc;
        this.itemPrice=itemPrice;
        //his.itembought=itemBrought;
    }
}
