package com.bixkit.shoppinglistapp;

/**
 * Created by Se on 02-May-16.
 */

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Se on 02-May-16.
 */
public class Product implements Parcelable {
    private String productName;
    private int productQuantity;
    private String listQuantity;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getListQuantity() {
        return listQuantity;
    }

    public void setListQuantity(String listQuantity) {
        this.listQuantity = listQuantity;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Product() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeInt(productQuantity);
        dest.writeString(listQuantity);
    }

    //creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Product createFromParcel(Parcel in){
            return new Product(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };


    public Product(String productName, int productQuantity, String listQuantity){
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.listQuantity = listQuantity;
    }

    public Product(Parcel in) {
        productName = in.readString();
        productQuantity = in.readInt();
        listQuantity = in.readString();
    }

    @Override
    public String toString(){
        if (productQuantity == 0){
            return listQuantity + " " + productName;
        } else {
            return productQuantity + " " + productName;
        }
    }
}
