import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RoadSurfaceInjury {

    public static class InjuryMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private Text surface = new Text();
        private IntWritable injuryValue = new IntWritable();

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

            String roadSurface = fields[9].trim();
            String injuryText = fields[18].trim();

            // Skip blank values
            if (roadSurface.isEmpty() || injuryText.isEmpty()) {
                return;
            }

            try {
                int injuries = (int) Double.parseDouble(injuryText);

                surface.set(roadSurface);
                injuryValue.set(injuries);

                context.write(surface, injuryValue);

            } catch (NumberFormatException e) {
                // Skip invalid numeric rows
            }
        }
    }

    public static class InjuryReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key,
                           Iterable<IntWritable> values,
                           Context context)
                throws IOException, InterruptedException {

            int totalInjuries = 0;

            for (IntWritable val : values) {
                totalInjuries += val.get();
            }

            result.set(totalInjuries);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(
                conf,
                "Road Surface Wise Total Injury Analysis"
        );

        job.setJarByClass(RoadSurfaceInjury.class);
        job.setMapperClass(InjuryMapper.class);
        job.setReducerClass(InjuryReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

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