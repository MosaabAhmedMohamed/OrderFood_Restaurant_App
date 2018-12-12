package com.example.mosaab.orderfoodserver.model;

public class Category {

    private String Name;
    private String Link;

    public Category()
    {

    }

    public Category(String name, String Link) {
        Name = name;
        Link = Link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
