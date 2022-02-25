package com.system559.diningout.exception;

public class RecordNotFoundException extends RuntimeException{
    public RecordNotFoundException(String name, String field, String value) {
        super(String.format("Could not find record of type %s with value %s in field %s",name,value,field));
    }
}
