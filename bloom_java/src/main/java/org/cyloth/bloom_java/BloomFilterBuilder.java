package org.cyloth.bloom_java;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

public class BloomFilterBuilder {
  private static final String USAGE = "usage: mvn exec:java "
      + "-Dexec.mainClass=\"org.cyloth.bloom_java.BloomFilterBuilder\" "
      + "-Dexec.args=\"<member_set_size> <path/to/member/set/file> <path/to/save/filter/file>\"";

  public static void main(String args[]) {
    if (args.length < 3) {
      System.out.println(USAGE);
      return;
    }

    int expectedElements = Integer.parseInt(args[0]);
    String memberFilePath = args[1];
    String saveFilePath = args[2];
    double falsePositiveRate = Math.pow(2, -20); // 2^(-20)

    // creating Guava BloomFilter
    System.out.println("Creating String-based Bloom filter...");
    BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.unencodedCharsFunnel(),
        expectedElements, falsePositiveRate);

    System.out.println("Adding members from " + memberFilePath);
    long time0 = System.nanoTime();
    FileReader fileReader;
    BufferedReader bufferedReader;
    try {
      fileReader = new FileReader(memberFilePath);
      bufferedReader = new BufferedReader(fileReader);
      for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
        bloomFilter.put(line.trim());
      }
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    double bloomFilterBuildTimeSec = (double) (System.nanoTime() - time0) / 1000000000.0;

    System.out.println("Saving Bloom filter to " + saveFilePath);
    time0 = System.nanoTime();
    try {
      OutputStream outputStream = new FileOutputStream(saveFilePath);
      bloomFilter.writeTo(outputStream);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    double bloomFilterSaveTimeSec = (double) (System.nanoTime() - time0) / 1000000000.0;

    System.out.println("Time to build Bloom filter (seconds): " + bloomFilterBuildTimeSec);
    System.out.println("Time to save Bloom filter (seconds): " + bloomFilterSaveTimeSec);
  }
}
