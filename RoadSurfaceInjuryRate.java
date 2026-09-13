import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RoadSurfaceInjuryRate {

    public static class InjuryRateMapper
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
            // Column 9 = ROADWAY_SURFACE_COND
            String roadSurface = fields[9].trim();

            // Column 18 = INJURIES_TOTAL
            String injuryText = fields[18].trim();

            if (roadSurface.isEmpty() || injuryText.isEmpty()) {
                return;
            }

            try {
                double injuries = Double.parseDouble(injuryText);

                // injuryCrash,totalCrash
                if (injuries > 0) {
                    context.write(
                        new Text(roadSurface),
                        new Text("1,1")
                    );
                } else {
                    context.write(
                        new Text(roadSurface),
                        new Text("0,1")
                    );
                }

            } catch (NumberFormatException e) {
                // Skip invalid rows
            }
        }
    }

    public static class InjuryRateReducer
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key,
                           Iterable<Text> values,
                           Context context)
                throws IOException, InterruptedException {

            int injuryCrashes = 0;
            int totalCrashes = 0;

            for (Text value : values) {
                String[] parts = value.toString().split(",");
                injuryCrashes += Integer.parseInt(parts[0]);
                totalCrashes += Integer.parseInt(parts[1]);
            }

            if (totalCrashes > 0) {

                double injuryRate =
                        ((double) injuryCrashes / totalCrashes) * 100.0;

                String result =
                        String.format("%.2f%%", injuryRate);

                context.write(key, new Text(result));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(
                conf,
                "Road Surface Wise Injury Rate"
        );

        job.setJarByClass(RoadSurfaceInjuryRate.class);
        job.setMapperClass(InjuryRateMapper.class);
        job.setReducerClass(InjuryRateReducer.class);
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