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
    AND first_crash_type IS NOT NULL
    AND first_crash_type != ''
    AND roadway_surface_cond IS NOT NULL
    AND roadway_surface_cond != ''
    AND injuries_fatal IS NOT NULL
    AND injuries_fatal != '';

prepared = FOREACH files GENERATE
    first_crash_type,
    roadway_surface_cond,
    (double)injuries_fatal AS fatalities;

grouped_data = GROUP prepared BY
    (first_crash_type, roadway_surface_cond);

fatality_count = FOREACH grouped_data
    GENERATE
    FLATTEN(group) AS
    (first_crash_type, roadway_surface_cond),
    SUM(prepared.fatalities) AS total_fatalities;

positive_fatalities = FILTER fatality_count BY
    total_fatalities > 0;

order_fatality = ORDER positive_fatalities
    BY total_fatalities DESC;

top10_fatality = LIMIT order_fatality 10;

STORE top10_fatality
INTO 'C:/BDAL/BDAProject/PIGFolder/FatalityRanking'
USING PigStorage('|');