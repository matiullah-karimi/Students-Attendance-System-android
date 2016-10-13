package com.karimi.attendance;

/**
 * Created by ahmadjavidsapand on 9/27/16.
 */
public class StudentDetails {
    private String name;
    private String id;

    public StudentDetails(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
