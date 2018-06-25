package se.fredin.fxkcamel.jobengine.utils;

import java.util.List;

public class Dataset {

    public static final int HEADER_INDEX = 0;

    private List<String> header;
    private List<List<String>> records;

    public Dataset(List<List<String>> records) {
        setHeader(records.get(HEADER_INDEX));
        records.remove(HEADER_INDEX);
        this.records = records;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setRecords(List<List<String>> records) {
        this.records = records;
    }

    public List<List<String>> getRecords() {
        return records;
    }

    public int indexOf(String requestedField) {
        try {
            return header.indexOf(requestedField);
        } catch(Exception ex) {
            throw new RuntimeException("Requested field=" + requestedField + " does not exist in header, available fields=" + headerAsString());
        }
    }

    public String get(String requestedField, List<String> record) {
        return record.get(indexOf(requestedField));
    }

    public String headerAsString() {
        return recordAsString(this.header);
    }


    public String recordAsString(List<String> record) {
        String s = "[";
        for(String field : record) {
            s += field;
            if(record.indexOf(field) < record.size() - 1) {
                s += ",";
            }
        }
        s += "]";
        return s;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "header=" + header +
                '}';
    }
}
