package com.example.driveguard.objects;

import java.io.Serializable;

public class ServerEvent implements Serializable{
    private String eventTime;
    private ServerLocation location;
    private ServerEventType eventType;
    private ServerEventSeverity severity;
}
