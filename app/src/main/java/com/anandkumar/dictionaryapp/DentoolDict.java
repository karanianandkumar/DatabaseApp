package com.anandkumar.dictionaryapp;

/**
 * Created by Anand on 2/19/2016.
 */
public class DentoolDict {
    private int ID;
    private String WORD;
    private String DEFINATION;

    public DentoolDict(){};

    public DentoolDict(String WORD,String DEFINATION){
        super();
        this.WORD=WORD;
        this.DEFINATION=DEFINATION;
    }

    public int getID() {
        return ID;
    }

    public String getWORD() {
        return WORD;
    }

    public void setWORD(String WORD) {
        this.WORD = WORD;
    }

    public String getDEFINATION() {
        return DEFINATION;
    }

    public void setDEFINATION(String DEFINATION) {
        this.DEFINATION = DEFINATION;
    }

    public void setID(int ID) {
        this.ID = ID;

    }

    @Override
    public String toString() {
        return "ROW [id=" + ID + ", Word=" + WORD + ", Defination=" + DEFINATION
                + "]";
    }
}
