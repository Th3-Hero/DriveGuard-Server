
alter table driving_event
    rename column weather to weather_type;
alter table driving_event
    add column weather_severity varchar(32) not null default 'UNKNOWN';