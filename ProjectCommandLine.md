cd..

cd..

cd Hadoop

cd sbin

hdfs namenode -format

start-all.cmd

jps

stop-all.cmd

start-all.cmd

jps

hdfs dfs -ls /

hdfs dfs -mkdir /Chicago

hdfs dfs -mkdir /chicago/input 

hdfs dfs -ls /Chicago

hdfs dfs -put "C:\\BDAL\\BDAProject\\ChicagoTrafficCrashes.csv" /chicago/input/

hdfs dfs -ls /chicago/input

hdfs dfs -mkdir /chicago/output

hdfs dfs -ls /Chicago



**MAPREDUCE**

**Task 1 (Weather-Wise Crash Count)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . WeatherWiseCrash.java



jar -cvf WeatherWiseCrash.jar WeatherWiseCrash.class WeatherWiseCrash$CrashMapper.class WeatherWiseCrash$CrashReducer.class



hadoop jar WeatherWiseCrash.jar WeatherWiseCrash /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/weather\_wise\_crash



hdfs dfs -ls /chicago/output/weather\_wise\_crash



hdfs dfs -cat /chicago/output/weather\_wise\_crash/part-r-00000



**Task 2 (Road Surface-Wise Total Injuries)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . RoadSurfaceInjury.java



jar -cvf RoadSurfaceInjury.jar RoadSurfaceInjury.class RoadSurfaceInjury$InjuryMapper.class RoadSurfaceInjury$InjuryReducer.class



hadoop jar RoadSurfaceInjury.jar RoadSurfaceInjury /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/road\_surface\_injury



hdfs dfs -ls /chicago/output/road\_surface\_injury



hdfs dfs -cat /chicago/output/road\_surface\_injury/part-r-00000



**Task 3 (Crash Type-Wise Average Speed)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . CrashTypeAverageSpeed.java



jar -cvf CrashTypeAverageSpeed.jar CrashTypeAverageSpeed.class CrashTypeAverageSpeed$SpeedMapper.class CrashTypeAverageSpeed$SpeedReducer.class



hadoop jar CrashTypeAverageSpeed.jar CrashTypeAverageSpeed /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/crash\_type\_avg\_speed



hdfs dfs -cat /chicago/output/crash\_type\_avg\_speed/part-r-00000



**Task 4 (Road Surface-Wise Injury Rate)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . RoadSurfaceInjuryRate.java



jar -cvf RoadSurfaceInjuryRate.jar RoadSurfaceInjuryRate.class RoadSurfaceInjuryRate$InjuryRateMapper.class RoadSurfaceInjuryRate$InjuryRateReducer.class



hadoop jar RoadSurfaceInjuryRate.jar RoadSurfaceInjuryRate /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/road\_surface\_injury\_rate



hdfs dfs -ls /chicago/output/road\_surface\_injury\_rate



hdfs dfs -cat /chicago/output/road\_surface\_injury\_rate/part-r-00000



**Task 5 (High-Risk Condition Score)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . HighRiskConditionScore.java



jar -cvf HighRiskConditionScore.jar HighRiskConditionScore.class HighRiskConditionScore$RiskMapper.class HighRiskConditionScore$RiskReducer.class



hadoop jar HighRiskConditionScore.jar HighRiskConditionScore /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/high\_risk\_condition\_score



hdfs dfs -ls /chicago/output/high\_risk\_condition\_score



hdfs dfs -cat /chicago/output/high\_risk\_condition\_score/part-r-00000



**Task 6 (Traffic Control-Wise Severe Crash Rate)**



javac -classpath "%HADOOP\_HOME%\\share\\hadoop\\common\\\*;%HADOOP\_HOME%\\share\\hadoop\\mapreduce\\\*" -d . TrafficControlSevereCrashRate.java



jar -cvf TrafficControlSevereCrashRate.jar TrafficControlSevereCrashRate.class TrafficControlSevereCrashRate$SevereCrashMapper.class TrafficControlSevereCrashRate$SevereCrashReducer.class



hadoop jar TrafficControlSevereCrashRate.jar TrafficControlSevereCrashRate /chicago/input/ChicagoTrafficCrashes.csv /chicago/output/traffic\_control\_severe\_rate



hdfs dfs -ls /chicago/output/traffic\_control\_severe\_rate



hdfs dfs -cat /chicago/output/traffic\_control\_severe\_rate/part-r-00000



**PIGANALYSIS**

**Task 1 (Top 10 Accident-Prone Streets)**



cd C:\\BDAL\\BDAProject\\PIGFolder



pig -x local Top10AccidentStreets.pig



dir Top10AccidentStreets.txt\\\*



type Top10AccidentStreets.txt\\part-r-00000



**Task 2 (Top 10 Primary Causes of Crashes)**



pig -x local Top10PrimaryCauses.pig



dir Top10PrimaryCauses.txt\\\*



type Top10PrimaryCauses.txt\\part-r-00000



**Task 3 (Top 10 Dangerous Hours by Total Injuries)**



pig -x local Top10DangerousHours.pig



dir Top10DangerousHours.txt\\\*



type Top10DangerousHours.txt\\part-r-00000



**Task 4 (Monthly Crash Trend)**



pig -x local MonthlyCrashTrend.pig



dir MonthlyCrashTrend.txt\\\*



type MonthlyCrashTrend.txt\\part-r-00000



**Task 5 (Day-of-Week Crash Analysis)**



pig -x local DayOfWeekCrashAnalysis.pig



dir DayOfWeekCrashAnalysis.txt\\\*



type DayOfWeekCrashAnalysis.txt\\part-r-00000



**Task 6 (Top Crash Types)**



pig -x local TopCrashTypes.pig



dir TopCrashTypes.txt\\\*



type TopCrashTypes.txt\\part-r-00000



**Task 7 (Fatality Ranking by Crash Type and Road Condition)**



pig -x local FatalityRanking.pig



dir FatalityRanking\\\*



type FatalityRanking\\part-r-00000



**Task 8 (Most Severe Injury Distribution)**



pig -x local SevereInjuryDistribution.pig



dir SevereInjuryDistribution\\\*



type SevereInjuryDistribution\\part-r-00000





