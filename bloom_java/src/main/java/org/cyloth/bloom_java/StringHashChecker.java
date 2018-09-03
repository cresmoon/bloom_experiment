package org.cyloth.bloom_experiment_java;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by loc on 5/17/18.
 */

public class StringHashChecker {
  private static final String USAGE = "usage: mvn exec:java "
      + "-Dexec.mainClass=\"org.cyloth.bloom_experiment_java.StringHashChecker\" "
      + "-Dexec.args=\"<member_set_size> <path/to/member/set/file> <path/to/test/set/file>\"";

  public static void main(String args[]) {
    if (args.length < 3) {
      System.out.println(USAGE);
      return;
    }

    int expectedElements = Integer.parseInt(args[0]);
    String memberFilePath = args[1];
    String testFilePath = args[2];
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

    System.out.println("Time to build Bloom filter (seconds): " + bloomFilterBuildTimeSec);
    System.out.println("Number of testing queries: " + lineCount);
    System.out.println("Total testing time (milliseconds): " + totalTestingTimeMsec);
  }
}
