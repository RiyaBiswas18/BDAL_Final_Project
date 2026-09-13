import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CrashTypeAverageSpeed {

    public static class SpeedMapper
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

            String crashType = fields[7].trim();
            String speedText = fields[2].trim();

            if (crashType.isEmpty() || speedText.isEmpty()) {
                return;
            }

            try {
                double speed = Double.parseDouble(speedText);

                context.write(
                        new Text(crashType),
                        new Text(speed + ",1")
                );

            } catch (NumberFormatException e) {
                // Skip invalid speed values
            }
        }
    }

    public static class SpeedReducer
            extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key,
                           Iterable<Text> values,
                           Context context)
                throws IOException, InterruptedException {

            double totalSpeed = 0;
            int count = 0;

            for (Text value : values) {

                String[] parts = value.toString().split(",");

                double speed = Double.parseDouble(parts[0]);
                int recordCount = Integer.parseInt(parts[1]);

                totalSpeed += speed;
                count += recordCount;
            }

            if (count > 0) {

                double averageSpeed = totalSpeed / count;

                context.write(
                        key,
                        new Text(String.format("%.2f", averageSpeed))
                );
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(
                conf,
                "Crash Type Wise Average Speed"
        );

        job.setJarByClass(CrashTypeAverageSpeed.class);
        job.setMapperClass(SpeedMapper.class);
        job.setReducerClass(SpeedReducer.class);
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