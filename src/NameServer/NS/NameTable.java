package NameServer.NS;

import NameServer.NS.SingleName;

import java.util.ArrayList;
import java.util.List;

public class NameTable {

    ArrayList<SingleName> table = new ArrayList<>();

    NameTable( ) {

    }

    NameTable( ArrayList<String> stringList) {
        this.table = formatTable( stringList );

    }

    public ArrayList<SingleName> formatTable (ArrayList<String> oriTable) {
        ArrayList<SingleName> formatedTable = new ArrayList<>();
        SingleName pair = null;

        for (int i=0; i<oriTable.size(); i+=2 ) {
            //if (oriTable.get(i).equals("")) { continue; }
            pair = new SingleName(oriTable.get(i), oriTable.get(i+1));
            formatedTable.add(pair);
        }
        //table = formatedTable;
        return formatedTable;
    }


    public List<String> slice (Integer start, Integer end) {
        List<String> fb = new ArrayList<String>();
        for (int i=0; i<table.size(); i++) {
            SingleName tmp = table.get(i);
            if (tmp.key<=end && tmp.key>=start) {
                fb.add(tmp.key.toString());
                fb.add(tmp.value);
                table.remove(i);
                i--;
            }
        }
        return fb;
    }

    public void append (ArrayList<String> oriTable) {
        ArrayList<SingleName> formatedTable = formatTable(oriTable);
        table.addAll( formatedTable );
    }

    public  void printTable() {
        for (SingleName each : table) {
            System.out.println(each.key.toString() + " " + each.value);
        }
    }

    public String lookup (Integer lookupKey) {
        for (SingleName each : table) {
            if (each.key.equals(lookupKey)) { return each.value; }
        }
        return null;
    }

    public void insert (Integer key, String value) {
        //table.add(table.size(), new SingleName(key,value));
        for (int i=0; i<table.size(); i++) {
            if (key<table.get(i).key) { table.add(i, new SingleName(key,value)); return; }
        }
        table.add(table.size(), new SingleName(key,value));
    }

    public String delete (Integer key) {
        for (int i=0; i<table.size(); i++) {
            if (key.equals(table.get(i).key)) { table.remove(i); return "successful deletion";}
        }
        return "KeyNotFound";
    }
}
