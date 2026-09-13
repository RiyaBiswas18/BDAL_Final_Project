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
    AND prim_contributory_cause IS NOT NULL
    AND prim_contributory_cause != '';

grouped_cause = GROUP files BY prim_contributory_cause;

cause_count = FOREACH grouped_cause
    GENERATE
    group AS primary_cause,
    COUNT(files) AS total_crashes;

order_cause = ORDER cause_count BY total_crashes DESC;

top10_cause = LIMIT order_cause 10;

STORE top10_cause
INTO 'C:/BDAL/BDAProject/PIGFolder/Top10PrimaryCauses'
USING PigStorage('|');