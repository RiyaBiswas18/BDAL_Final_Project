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
    AND crash_month IS NOT NULL
    AND crash_month != '';

prepared = FOREACH files GENERATE
    (int)crash_month AS crash_month;

grouped_month = GROUP prepared BY crash_month;

month_count = FOREACH grouped_month
    GENERATE
    group AS crash_month,
    COUNT(prepared) AS total_crashes;

order_month = ORDER month_count BY crash_month ASC;

STORE order_month
INTO 'C:/BDAL/BDAProject/PIGFolder/MonthlyCrashTrend'
USING PigStorage('|');