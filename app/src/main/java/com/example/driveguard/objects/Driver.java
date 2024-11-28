package com.example.driveguard.objects;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Driver implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private int overallScore;
    private String accountCreationDate;
}
