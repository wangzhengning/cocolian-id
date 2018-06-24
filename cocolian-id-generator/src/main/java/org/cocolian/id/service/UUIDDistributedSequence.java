package org.cocolian.id.service;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @description:基于jdk自带UUID
 * @author: zn.wang , Created in 22:05 2018/6/24.
 */
public class UUIDDistributedSequence{

    public Long sequence(String sequenceName) {
        return ConvertUuidToBigInteger.getBigIntegerFromUuid(UUID.randomUUID()).longValue();
    }

    /**
     * @see {https://gist.github.com/berezovskyi/2c4d2a07fa2f35e5e04c#file-gistfile1-java}
     */
    private final static class ConvertUuidToBigInteger{

        public static final BigInteger B = BigInteger.ONE.shiftLeft(64); // 2^64
        public static final BigInteger L = BigInteger.valueOf(Long.MAX_VALUE);

        public static BigInteger convertToBigInteger(UUID id) {
            BigInteger lo = BigInteger.valueOf(id.getLeastSignificantBits());
            BigInteger hi = BigInteger.valueOf(id.getMostSignificantBits());

            // If any of lo/hi parts is negative interpret as unsigned

            if (hi.signum() < 0){
                hi = hi.add(B);
            }

            if (lo.signum() < 0){
                lo = lo.add(B);
            }

            return lo.add(hi.multiply(B));
        }

        public static UUID convertFromBigInteger(BigInteger x) {
            BigInteger[] parts = x.divideAndRemainder(B);
            BigInteger hi = parts[0];
            BigInteger lo = parts[1];

            if (L.compareTo(lo) < 0){
                lo = lo.subtract(B);
            }

            if (L.compareTo(hi) < 0){
                hi = hi.subtract(B);
            }

            return new UUID(hi.longValueExact(), lo.longValueExact());
        }

        private static BigInteger getBigIntegerFromUuid(UUID uuid) {
            BigInteger value1 = BigInteger.valueOf(uuid.getMostSignificantBits());
            BigInteger value2 = BigInteger.valueOf(uuid.getLeastSignificantBits());
            if (value1.compareTo(value2) < 0) {
                return value2.multiply(value2).add(value1);
            }
            return value1.multiply(value1).add(value1).add(value2);
        }

    }

}
