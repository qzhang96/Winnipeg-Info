package com.example.winnipeginfo;

public class Cycling {
    private String id;
    private String st_name;
    private String location;
    private String type;
    private String city_area;
    private String ward;
    private String nbhd;

    public Cycling(String id,String st_name,String location,
                   String type,String city_area,String ward,String nbhd)
    {
        this.id=id;
        this.st_name=st_name;
        this.location=location;
        this.type=type;
        this.city_area=city_area;
        this.ward=ward;
        this.nbhd=nbhd;
    }

    public String getId() {
        return id;
    }

    public String getSt_name() {
        return st_name;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getCity_area() {
        return city_area;
    }

    public String getWard() {
        return ward;
    }

    public String getNbhd() {
        return nbhd;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSt_name(String st_name) {
        this.st_name = st_name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCity_area(String city_area) {
        this.city_area = city_area;
    }

    public void setNbhd(String nbhd) {
        this.nbhd = nbhd;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    @Override
    public String toString()
    {
        return "St Name: "+st_name+"\n\n"+"Location: "+location+"\n\n" + "Type: " +type
                +"\n\n"+ "City Area: " +city_area+"\n\n"+"Ward: "+ward+"\n\n"+"NBHD: "+nbhd ;
    }
}
