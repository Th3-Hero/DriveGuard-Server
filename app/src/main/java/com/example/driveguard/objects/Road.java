package com.example.driveguard.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Road {
    private ServerLocation serverLocation;
    private String type;
    private String name;
    private int speedLimit;
}
