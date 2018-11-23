package com.example.winnipeginfo;

public class Community {
    private String name;
    private String address;
    private String complexId;
    public Community(String name, String addres,String complexId)
    {
        this.name = name;
        this.address = addres;
        this.complexId=complexId;
    }

    public String getName(){return name;}
    public String getAddress(){return address;}

   public String getComplexId() { return complexId; }

    public void setName(String name){
        this.name=name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setComplexId(String complexId) { this.complexId = complexId; }

    @Override
    public String toString()
    {
        return "Name: "+name+"\n\n"+"Address: "+address+"\n\n";
    }
}
