package com.example.winnipeginfo;

public class OpenSpace {
    private String park_id;
    private String park_name;
    private String location;
    private String category;
    private String district;
    private String nbhd;
    private String ward;
    private String area_ha;
    private String water_area;
    private String land_area;

    public OpenSpace(String park_id, String park_name,String location,
                     String category,String district, String
                             nbhd,String ward,String area_ha,String water_area,String land_area)
    {
        this.park_id=park_id;
        this.park_name=park_name;
        this.location=location;
        this.category=category;
        this.district=district;
        this.nbhd=nbhd;
        this.ward=ward;
        this.area_ha=area_ha;
        this.water_area=water_area;
        this.land_area=land_area;
    }

    public void setPark_id(String park_id) {
        this.park_id = park_id;
    }


    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public void setArea_ha(String area_ha) {
        this.area_ha = area_ha;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setLand_area(String land_area) {
        this.land_area = land_area;
    }

    public void setWater_area(String water_area) {
        this.water_area = water_area;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public void setNbhd(String nbhd) {
        this.nbhd = nbhd;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNbhd() {
        return nbhd;
    }

    public String getWard() {
        return ward;
    }

    public String getLocation() {
        return location;
    }

    public String getArea_ha() {
        return area_ha;
    }

    public String getCategory() {
        return category;
    }


    public String getDistrict() {
        return district;
    }

    public String getLand_area() {
        return land_area;
    }

    public String getPark_id() {
        return park_id;
    }

    public String getPark_name() {
        return park_name;
    }

    public String getWater_area() {
        return water_area;
    }

    @Override
    public String toString()
    {
        return  "Park ID: "+park_id+"\n\n"+"Park Name: " +park_name +"\n\n"+"Location: "+location+"\n\n"+"Category: "+category+"\n\n"+
               "District: "+district+"\n\n"+"NBHD: "+nbhd+"\n\n"+"Area Ha: "+area_ha+"\n\n"+
                "Water Area: "+water_area+"\n\n"+"Land Area: "+land_area;
    }
}
