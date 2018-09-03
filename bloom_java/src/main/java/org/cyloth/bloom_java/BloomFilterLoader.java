package org.cyloth.bloom_experiment_java;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

public class BloomFilterLoader {
  private static final String USAGE = "usage: mvn exec:java "
      + "-Dexec.mainClass=\"org.cyloth.bloom_experiment_java.BloomFilterLoader\" "
      + "-Dexec.args=\"<path/to/load/filter/file> <path/to/test/set/file>\"";

  public static void main(String args[]) {
    if (args.length < 2) {
      System.out.println(USAGE);
      return;
    }

    String loadFilePath = args[0];
    String testFilePath = args[1];

    System.out.println("Loading String-based Bloom filter from " + loadFilePath);
    long time0 = System.nanoTime();
    BloomFilter<String> bloomFilter;
    FileReader fileReader;
    BufferedReader bufferedReader;
    try {
      // load Bloom filter from file
      InputStream inputStream = new FileInputStream(loadFilePath);
      bloomFilter = BloomFilter.readFrom(inputStream, Funnels.unencodedCharsFunnel());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    double bloomFilterLoadTimeSec = (double) (System.nanoTime() - time0) / 1000000000.0;

    System.out.println("Testing membership from " + testFilePath);
    time0 = System.nanoTime();
    int lineCount = 0;
    try {
      fileReader = new FileReader(testFilePath);
      bufferedReader = new BufferedReader(fileReader);
      for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
        String testVal = line.trim();
        boolean testMembership = bloomFilter.mightContain(testVal);
        if (!testMembership) {
          System.out.println("Membership of " + testVal + " is " + testMembership);
        }
        lineCount += 1;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    double totalTestingTimeMsec = (double) (System.nanoTime() - time0) / 1000000.0;

    System.out.println("Time to load Bloom filter (seconds): " + bloomFilterLoadTimeSec);
    System.out.println("Number of testing queries: " + lineCount);
    System.out.println("Total testing time (milliseconds): " + totalTestingTimeMsec);
  }
}
