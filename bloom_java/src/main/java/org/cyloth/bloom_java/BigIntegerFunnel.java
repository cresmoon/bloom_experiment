package org.cyloth.bloom_experiment_java;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import java.math.BigInteger;

public class BigIntegerFunnel implements Funnel<BigInteger> {
  public void funnel(BigInteger from, PrimitiveSink into) {
    into.putBytes(from.toByteArray());
  }
}
