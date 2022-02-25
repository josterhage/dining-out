package com.system559.diningout.exception;

public class RecordIdNotFoundException extends RecordNotFoundException {
    public RecordIdNotFoundException(String name,String value) {
        super(name,"id",value);
    }
}
