package me.tatocaster.ibsuoid.model;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by tatocaster on 2015-05-03.
 */

@Parcel
public class User {

    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("id_number")
    private int id;

    @ParcelConstructor
    public User(String name, String email, int id){
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
