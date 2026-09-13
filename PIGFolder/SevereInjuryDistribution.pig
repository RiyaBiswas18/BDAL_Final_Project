infiles = LOAD 'C:/BDAL/BDAProject/ChicagoTrafficCrashes.csv'
USING PigStorage(',')
AS
(
    crash_record_id:chararray,
    crash_date:chararray,
    posted_speed_limit:chararray,
    traffic_control_device:chararray,
    device_condition:chararray,
    weather_condition:chararray,
    lighting_condition:chararray,
    first_crash_type:chararray,
    trafficway_type:chararray,
    roadway_surface_cond:chararray,
    road_defect:chararray,
    crash_type:chararray,
    damage:chararray,
    prim_contributory_cause:chararray,
    street_direction:chararray,
    street_name:chararray,
    num_units:chararray,
    most_severe_injury:chararray,
    injuries_total:chararray,
    injuries_fatal:chararray,
    injuries_incapacitating:chararray,
    crash_hour:chararray,
    crash_day_of_week:chararray,
    crash_month:chararray
);

files = FILTER infiles BY
    crash_record_id != 'CRASH_RECORD_ID'
    AND most_severe_injury IS NOT NULL
    AND most_severe_injury != '';

grouped_injury = GROUP files BY most_severe_injury;

injury_count = FOREACH grouped_injury
    GENERATE
    group AS injury_type,
    COUNT(files) AS total_crashes;

order_injury = ORDER injury_count
    BY total_crashes DESC;

STORE order_injury
INTO 'C:/BDAL/BDAProject/PIGFolder/SevereInjuryDistribution'
USING PigStorage('|');