package parkour.hadoop;

import java.io.IOException;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class ClojureAvroKeyValueInputFormat<K, V>
    extends FileInputFormat<AvroKey<K>, AvroValue<V>> {
  private static class Vars {
    private static final String NS = "parkour.conf";
    private static final Var configuration = RT.var(NS, "configuration");
    static {
      RT.var("clojure.core", "require").invoke(Symbol.intern(NS));
    }
  }

  @Override
  public RecordReader<AvroKey<K>, AvroValue<V>>
    createRecordReader(InputSplit split, TaskAttemptContext context)
      throws IOException, InterruptedException {

    // Get `Configuration` via `parkour.conf/ig` to avoid caring at compile-time
    // if `TaskAttempContext` is an interface or a class.
    Configuration conf = (Configuration) Vars.configuration.invoke(context);
    Schema ks = AvroJob.getInputKeySchema(conf);
    Schema vs = AvroJob.getInputValueSchema(conf);
    return new ClojureAvroKeyValueRecordReader<K, V>(ks, ks);
  }
}
