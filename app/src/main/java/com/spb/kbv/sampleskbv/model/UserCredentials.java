package com.spb.kbv.sampleskbv.model;

import lombok.Data;

@Data
public class UserCredentials {
    private String email;
    private String firstName;
    private boolean isAuthorized;

    public boolean isUserAuthorized() {
        return isAuthorized;
    }
}
