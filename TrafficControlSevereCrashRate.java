import java.io.IOException;
import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TrafficControlSevereCrashRate {

    public static class SevereCrashMapper
            extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            // Skip header
            if (line.startsWith("CRASH_RECORD_ID")) {
                return;
            }

            String[] fields = line.split(",", -1);

            if (fields.length != 24) {
                return;
            }

            String trafficControl = fields[3].trim();
            String fatalText = fields[19].trim();
            String incapacitatingText = fields[20].trim();

            // Skip missing values
            if (trafficControl.isEmpty() || fatalText.isEmpty() || incapacitatingText.isEmpty()) {
                return;
            }

            try {

                double fatal =
                        Double.parseDouble(fatalText);

                double incapacitating =
                        Double.parseDouble(incapacitatingText);

                // severeCrash,totalCrash
                if (fatal > 0 || incapacitating > 0) {

                    context.write(
                            new Text(trafficControl),
                            new Text("1,1")
                    );

                } else {

                    context.write(
                            new Text(trafficControl),
                            new Text("0,1")
                    );
                }

            } catch (NumberFormatException e) {
                // Skip invalid rows
            }
        }
    }

    public static class SevereCrashReducer
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key,
                           Iterable<Text> values,
                           Context context)
                throws IOException, InterruptedException {

            int severeCrashes = 0;
            int totalCrashes = 0;

            for (Text value : values) {

                String[] parts =
                        value.toString().split(",");

                severeCrashes +=
                        Integer.parseInt(parts[0]);

                totalCrashes +=
                        Integer.parseInt(parts[1]);
            }

            if (totalCrashes > 0) {

                double severeCrashRate =
                        ((double) severeCrashes
                        / totalCrashes) * 100.0;

                String result =
                        String.format(
                                Locale.US,
                                "%.2f%%",
                                severeCrashRate
                        );

                context.write(
                        key,
                        new Text(result)
                );
            }
        }
    }

    public static void main(String[] args)
            throws Exception {

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "Traffic Control Wise Severe Crash Rate"
                );

        job.setJarByClass(
                TrafficControlSevereCrashRate.class
        );

        job.setMapperClass(
                SevereCrashMapper.class
        );

        job.setReducerClass(
                SevereCrashReducer.class
        );

        job.setOutputKeyClass(
                Text.class
        );

        job.setOutputValueClass(
                Text.class
        );

        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );

        System.exit(
                job.waitForCompletion(true)
                        ? 0
                        : 1
        );
    }
}