import bloomfilter.mutable.BloomFilter
import scala.math.pow
import scala.io.Source

/**
  * Based on https://github.com/alexandrnikitin/bloom-filter-scala
  * Discussion: https://alexandrnikitin.github.io/blog/bloom-filter-for-scala/
  * Created by loc on 5/16/18.
  */

object HashChecker {

  val USAGE = "usage: sbt \"run <member_set_size> <path/to/member/set/file> <path/to/test/set/file>\""

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println(USAGE); return
    }

    val expectedElements = args(0).toInt
    val memberFilePath = args(1)
    val testFilePath = args(2)

    val falsePositiveRate = pow(2, -20) // 2^(-20)
    val bf = BloomFilter[String](expectedElements, falsePositiveRate)

    println("Adding members from " + memberFilePath)
    var time0 = System.nanoTime
    for (line <- Source.fromFile(memberFilePath).getLines()) {
      bf.add(line.stripLineEnd)
    }
    val bloomFilterBuildTimeSec = (System.nanoTime - time0) / 1000000000.0

    println("Testing membership from " + testFilePath)
    time0 = System.nanoTime
    var lineCount = 0
    for (line <- Source.fromFile(testFilePath).getLines()) {
      val testVal = line.stripLineEnd
      val testMembership = bf.mightContain(testVal)
      if (!testMembership) println("Membership of " + testVal + " is " + testMembership)
      lineCount += 1
    }
    val totalTestingTimeMsec = (System.nanoTime - time0) / 1000000.0

    println("Time to build Bloom filter (seconds): " + bloomFilterBuildTimeSec)
    println("Number of testing queries: " + lineCount)
    println("Total testing time (milliseconds): " + totalTestingTimeMsec)

    bf.dispose()
  }
}
