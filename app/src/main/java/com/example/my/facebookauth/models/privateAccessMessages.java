package com.example.my.facebookauth.models;

/**
 * Created by Owner on 2016-11-17.
 */

public class privateAccessMessages {

    private boolean primary;
    private boolean read;

    public privateAccessMessages() {

    }

    public privateAccessMessages(boolean primary, boolean read) {
        this.primary = primary;
        this.read = read;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean getPrimary() {
        return primary;
    }
    public boolean getRead() {
        return read;
    }
}
