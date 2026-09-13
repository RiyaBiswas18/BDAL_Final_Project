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

public class HighRiskConditionScore {

    public static class RiskMapper
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

            String weather = fields[5].trim();
            String lighting = fields[6].trim();
            String roadSurface = fields[9].trim();

            String injuriesText = fields[18].trim();
            String fatalText = fields[19].trim();
            String incapacitatingText = fields[20].trim();

            // Skip rows with missing required values
            if (weather.isEmpty() ||
                lighting.isEmpty() ||
                roadSurface.isEmpty() ||
                injuriesText.isEmpty() ||
                fatalText.isEmpty() ||
                incapacitatingText.isEmpty()) {
                return;
            }

            try {

                double injuries =
                        Double.parseDouble(injuriesText);

                double fatal =
                        Double.parseDouble(fatalText);

                double incapacitating =
                        Double.parseDouble(incapacitatingText);

                // Combination key
                String condition =
                        weather + " | " +
                        lighting + " | " +
                        roadSurface;

                /*
                 Values:total injuries,incapacitating injuries,fatalities,crash count
                */
                String outputValue =
                        injuries + "," +
                        incapacitating + "," +
                        fatal + ",1";

                context.write(
                        new Text(condition),
                        new Text(outputValue)
                );

            } catch (NumberFormatException e) {
                // Skip invalid numeric records
            }
        }
    }

    public static class RiskReducer
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key,
                           Iterable<Text> values,
                           Context context)
                throws IOException, InterruptedException {

            double totalInjuries = 0;
            double totalIncapacitating = 0;
            double totalFatal = 0;

            int totalCrashes = 0;

            for (Text value : values) {

                String[] parts =
                        value.toString().split(",");

                totalInjuries +=
                        Double.parseDouble(parts[0]);

                totalIncapacitating +=
                        Double.parseDouble(parts[1]);

                totalFatal +=
                        Double.parseDouble(parts[2]);

                totalCrashes +=
                        Integer.parseInt(parts[3]);
            }

            if (totalCrashes > 0) {

                double riskScore =
                        (totalInjuries
                        + (2 * totalIncapacitating)
                        + (5 * totalFatal))
                        / totalCrashes;

                String result =
                        String.format(
                                Locale.US,
                                "%.4f",
                                riskScore
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

        Configuration conf = new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "High Risk Condition Score"
                );

        job.setJarByClass(HighRiskConditionScore.class);
        job.setMapperClass(RiskMapper.class);
        job.setReducerClass(RiskReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );
        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}