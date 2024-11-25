package com.example.driveguard.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Credentials {
    private int driverId;
    private String token;
    private int tripID;
    public Credentials(int driverID, String token, int tripID){
        this.driverId = driverID;
        this.token = token;
        this.tripID = tripID;
    }
    public Credentials(int driverID, String token){
        this.driverId = driverID;
        this.token = token;
        this.tripID = -1;
    }
}
