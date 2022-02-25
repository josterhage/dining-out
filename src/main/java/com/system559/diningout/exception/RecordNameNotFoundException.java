package com.system559.diningout.exception;

public class RecordNameNotFoundException extends RecordNotFoundException{
    public RecordNameNotFoundException(String name, String value) {
        super(name,"name",value);
    }
}
