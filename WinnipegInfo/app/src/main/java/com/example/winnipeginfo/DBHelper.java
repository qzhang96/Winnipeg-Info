package com.example.winnipeginfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    //Define your database name
    private static final String DB_NAME="WinnipegInfo";
    private static final String COL_USER = "UserName";
    private static final String COL_ADDRESS="Address";
    private static final String COL_LOCATIOIN="Location";
    private static final String COL_WARD="Ward";
    private static final String COL_NBHD="Nbhd";
    //Define the database version
    private static final int DB_VERSION = 12;

    //Define your table name
    private static final String TABLE_COMMUNITY = "Community";
    //Create constants defining your column names
    private static final String COL_NAME="Name";
    private static  final String COL_COMPLEX_ID="ComplexID";


    //Define your table name
    private static final String TABLE_CYCLING = "Cycling";
    //Create constants defining your column names
    private static final String COL_ID="Id";
    private static final String COL_STNAME="StName";
   //LOCATION
    private static final String COL_TYPE="Type";
    private static final String COL_CITY_AREA="CityArea";
    //ward
    //nbhd


    private static final String TABLE_OPEN_SPACE = "OpenSpace";
    private static final String COL_PARKID="ParkID";
    private static final String COL_PARK_NAME="ParkName";
    //location
    private static final  String COL_CATEGORY="Category";
    private static final String COL_DISTRICT="District";
    //nbhd
    //ward
    private static final String COL_AREA_HA="AreaHa";
    private static final String COL_WATER_AREA="WaterArea";
    private static final String COL_LAND_AREA="LandArea";


    //Define your create statement in typical sql format
    //CREATE TABLE {Tablename} (
    //Colname coltype
    //)
    private static final String TABLE_COMMUNITY_CREATE =
            "CREATE TABLE " + TABLE_COMMUNITY + " (" +
                    COL_NAME + " TEXT NOT NULL," +
                    COL_ADDRESS + " TEXT NOT NULL," +
                    COL_COMPLEX_ID + " TEXT NOT NULL," +
                    COL_USER + " TEXT NOT NULL,"+
                    " CONSTRAINT ComplexID_Unique UNIQUE (" +
                    COL_COMPLEX_ID + "));";

    private static final String TABLE_CYCLING_CREATE =
            "CREATE TABLE " + TABLE_CYCLING + " (" +
                    COL_ID+ " TEXT NOT NULL," +
                    COL_STNAME + " TEXT NOT NULL," +
                    COL_LOCATIOIN + " TEXT NOT NULL," +
                    COL_TYPE + " TEXT NOT NULL," +
                    COL_CITY_AREA + " TEXT NOT NULL," +
                    COL_WARD+ " TEXT NOT NULL," +
                    COL_NBHD + " TEXT NOT NULL," +
                    COL_USER + " TEXT NOT NULL," +
                    " CONSTRAINT ID_Unique UNIQUE (" +
                    COL_ID + "));";
    private static final String TABLE_OPEN_SPACE_CREATE =
            "CREATE TABLE "+ TABLE_OPEN_SPACE + " (" +
                    COL_PARKID + " TEXT NOT NULL," +
                    COL_PARK_NAME + " TEXT NOT NULL," +
                    COL_LOCATIOIN + " TEXT NOT NULL," +
                    COL_CATEGORY + " TEXT NOT NULL," +
                    COL_DISTRICT + " TEXT NOT NULL," +
                    COL_NBHD + " TEXT NOT NULL," +
                    COL_WARD + " TEXT NOT NULL," +
                    COL_AREA_HA + " TEXT NOT NULL," +
                    COL_WATER_AREA + " TEXT NOT NULL," +
                    COL_LAND_AREA + " TEXT NOT NULL," +
                    COL_USER + " TEXT NOT NULL," +
                    " CONSTRAINT Park_ID_Unique UNIQUE (" +
                    COL_PARKID + "));";



    //Drop table statement
    private static final String DROP_COMMUNITY_TABLE = "DROP TABLE IF EXISTS " + TABLE_COMMUNITY;
    private static final String DROP_CYCLING_TABLE = "DROP TABLE IF EXISTS " + TABLE_CYCLING;
    private static final String DROP_OPEN_SPACE_TABLE = "DROP TABLE IF EXISTS " + TABLE_OPEN_SPACE;
    //constructor
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_COMMUNITY_CREATE);
        db.execSQL(TABLE_CYCLING_CREATE);
        db.execSQL(TABLE_OPEN_SPACE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //drop the table and recreate it
        db.execSQL(DROP_COMMUNITY_TABLE);
        db.execSQL(DROP_CYCLING_TABLE);
        db.execSQL(DROP_OPEN_SPACE_TABLE);
        onCreate(db);
    }

    //Insert values using content values
    public void insertValuesToCommunity(String name, String address, String complexId, String user)
    {
        //get an instance of a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //create an instance of ContentValues to add to the database
        //the ContentValues class is used to store sets of values that
        //are easier to process
        ContentValues insertValues = new ContentValues();
        //Add values to the ContentValues:
        //insertValues.put(ColumnName, value);
        insertValues.put(COL_NAME, name);
        insertValues.put(COL_ADDRESS, address);
        insertValues.put(COL_COMPLEX_ID,complexId );
        insertValues.put(COL_USER, user);
        //insert the values into the table
        db.insert(TABLE_COMMUNITY, null, insertValues);
        //close the database
        db.close();
    }
        //Insert values using content values
    public void insertValuesToCycling(String id,String st_name,String location,
                             String type,String city_area,String ward,String nbhd,String user)
    {
        //get an instance of a writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create an instance of ContentValues to add to the database
        //the ContentValues class is used to store sets of values that
        //are easier to process
        ContentValues insertValues = new ContentValues();
        //Add values to the ContentValues:
        //insertValues.put(ColumnName, value);
        insertValues.put(COL_ID, id);
        insertValues.put(COL_STNAME,st_name);
        insertValues.put(COL_LOCATIOIN,location);
        insertValues.put(COL_TYPE,type);
        insertValues.put(COL_CITY_AREA,city_area);
        insertValues.put(COL_WARD,ward);
        insertValues.put(COL_NBHD,nbhd);
        insertValues.put(COL_USER, user);
        //insert the values into the table
        db.insert(TABLE_CYCLING, null, insertValues);
        //close the database
        db.close();
    }

    public void insertValuesToOpenSpace
            (String park_id, String park_name,String location,
             String category,String district, String nbhd,
             String ward,String area_ha,String water_area,String land_area,String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues insertValues = new ContentValues();
        insertValues.put(COL_PARKID,park_id);
        insertValues.put(COL_PARK_NAME,park_name);
        insertValues.put(COL_LOCATIOIN,location);
        insertValues.put(COL_CATEGORY,category);
        insertValues.put(COL_DISTRICT,district);
        insertValues.put(COL_NBHD,nbhd);
        insertValues.put(COL_WARD,ward);
        insertValues.put(COL_AREA_HA,area_ha);
        insertValues.put(COL_WATER_AREA,water_area);
        insertValues.put(COL_LAND_AREA,land_area);
        insertValues.put(COL_USER,user);
        db.insert(TABLE_OPEN_SPACE, null, insertValues);
        //close the database
        db.close();
    }


    public void deleteRow(String tableName,String colName, String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName+ " WHERE "+colName+"='"+value+"'");
        db.close();
    }

    public void saveExecForCommunity(String name, String address, String complexId, String user)
    {
        //Open your writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //Formulate your statement
        String insertStatement = "INSERT INTO 'Community' VALUES ( " + name + ", " + address + "," + complexId + "," + user +");";

        //Execute your statement
        db.execSQL(insertStatement);

        db.close();

    }

    public void saveExecForCycling(String id, String st_name, String location,
                                    String type, String city_area, String ward, String nbhd, String user)
    {
        //Open your writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //Formulate your statement
        String insertStatement = "INSERT INTO 'Cycling' VALUES ( " + id + ", " + st_name + "," + location + "," +
                 type + ", " + city_area + "," + ward + "," + nbhd +" ," + user +");";

        //Execute your statement
        db.execSQL(insertStatement);

        db.close();

    }
    public void saveExecForOpenSpace(String park_id, String park_name,String location,
                                     String category,String district, String nbhd,
                                     String ward,String area_ha,String water_area,String land_area,String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertStatement ="INSERT INTO 'OpenSpace' VALUES (" + park_id + ", " + park_name + ", " + location + ", " + category
                + ", " + district + ", " + nbhd + ", " +ward + ", "  + area_ha + ", " +water_area + ", " +
                land_area + ", " + user +" );";
        //Execute your statement
        db.execSQL(insertStatement);

        db.close();

    }

    //Load the data in the table
    public ArrayList<Community> loadCommunityData(String user){

        ArrayList<Community> data= new ArrayList<Community>();
        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();
        //create an array of the table names
        String[] columns = {COL_NAME, COL_ADDRESS,COL_COMPLEX_ID,COL_USER};
        String selection = COL_USER+" =?";
        String[] selectionArgs = {user};
        //Create a cursor item for querying the database
        Cursor c = db.query(TABLE_COMMUNITY,	//The name of the table to query
                columns,				//The columns to return
                selection,					//The columns for the where clause
                selectionArgs,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order



        //Move to the first row
        c.moveToFirst();

        //For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            data.add(new Community(c.getString(0),c.getString(1),c.getString(2)));
            c.moveToNext();
        }

        //close the cursor
        c.close();
        //close the database
        db.close();

        return data;
    }

    //Load the data in the table
    public ArrayList<Cycling> loadCyclingData(String user){

        ArrayList<Cycling> data= new ArrayList<Cycling>();
        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();
        //create an array of the table names
        String[] columns = {COL_ID, COL_STNAME,COL_LOCATIOIN,COL_TYPE,COL_CITY_AREA,COL_WARD,COL_NBHD,COL_USER};
        String selection = COL_USER+" =?";
        String[] selectionArgs = {user};
        //Create a cursor item for querying the database
        Cursor c = db.query(TABLE_CYCLING,	//The name of the table to query
                columns,				//The columns to return
                selection,					//The columns for the where clause
                selectionArgs,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order



        //Move to the first row
        c.moveToFirst();

        //For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            data.add(new Cycling(c.getString(0),c.getString(1),c.getString(2),
                    c.getString(3),c.getString(4),c.getString(5),c.getString(6)));
            c.moveToNext();
        }

        //close the cursor
        c.close();
        //close the database
        db.close();

        return data;
    }
    public ArrayList<OpenSpace> loadOpenSpaceData(String user){

        ArrayList<OpenSpace> data= new ArrayList<OpenSpace>();
        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();
        //create an array of the table names
        String[] columns = {COL_PARKID,COL_PARK_NAME, COL_LOCATIOIN,COL_CATEGORY,COL_DISTRICT,COL_NBHD,COL_WARD,COL_AREA_HA,
                COL_WATER_AREA,COL_LAND_AREA,COL_USER};
        String selection = COL_USER+" =?";
        String[] selectionArgs = {user};
        //Create a cursor item for querying the database
        Cursor c = db.query(TABLE_OPEN_SPACE,	//The name of the table to query
                columns,				//The columns to return
                selection,					//The columns for the where clause
                selectionArgs,					//The values for the where clause
                null,					//Group the rows
                null,					//Filter the row groups
                null);					//The sort order



        //Move to the first row
        c.moveToFirst();

        //For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            data.add(new OpenSpace(c.getString(0),c.getString(1),c.getString(2),
                    c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),
                    c.getColumnName(8),c.getString(9)));
            c.moveToNext();
        }

        //close the cursor
        c.close();
        //close the database
        db.close();

        return data;
    }

    public long getTaskCount(String tableName) {
        return DatabaseUtils.queryNumEntries(this.getReadableDatabase(), tableName);
    }

}
