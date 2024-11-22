package com.example.driveguard.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Credentials {
    private int driverID;
    private String token;
    private int tripID;
    public Credentials(int driverID, String token, int tripID){
        this.driverID = driverID;
        this.token = token;
        this.tripID = tripID;
    }
    public Credentials(int driverID, String token){
        this.driverID = driverID;
        this.token = token;
        this.tripID = -1;
    }
}
