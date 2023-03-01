package com.ehealthkiosk.kiosk.model;


public class TestTypes {

    public String text;
    public String testId;
    public int drawable;
    public String color;

    public TestTypes(String id, String t, int d, String c)
    {
        text=t;
        drawable=d;
        color=c;
        testId = id;
    }
}