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
    AND crash_hour IS NOT NULL
    AND crash_hour != ''
    AND injuries_total IS NOT NULL
    AND injuries_total != '';

prepared = FOREACH files GENERATE
    (int)crash_hour AS crash_hour,
    (double)injuries_total AS injuries_total;

grouped_hour = GROUP prepared BY crash_hour;

hour_injuries = FOREACH grouped_hour
    GENERATE
    group AS crash_hour,
    SUM(prepared.injuries_total) AS total_injuries;

order_hour = ORDER hour_injuries BY total_injuries DESC;

top10_hour = LIMIT order_hour 10;

STORE top10_hour
INTO 'C:/BDAL/BDAProject/PIGFolder/Top10DangerousHours'
USING PigStorage('|');