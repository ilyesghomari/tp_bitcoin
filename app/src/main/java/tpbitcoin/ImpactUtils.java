package tpbitcoin;

import java.math.BigInteger;

public class ImpactUtils {


    private static final long AVERAGE_BLOCK_TIME_SECONDS = 600; // 10 minutes in seconds
    /**
     * computes the expected time (in seconds) for mining a block
     * @param hashrate: miner hashing capacity (hash/s)
     * @param difficultyAsInteger: block difficulty, as BigInteger (256 bits integer)
     * @return expected time in seconds before finding a correct block
     */
    // TODO
    public static long expectedMiningTime(long hashrate, BigInteger difficultyAsInteger){
        double nombreMoyenhash = 1.0 / calculerProbabiliteMinage(difficultyAsInteger);
        return (long) (nombreMoyenhash / hashrate);

    }

    /**
     * Compute the total hashrate of the network given current difficulty level
     * @param difficultyAsInteger: difficulty level as 256bits integer
     * @return hashrate of the network in GH/s
     */
    // TODO
    public static long  globalHashRate(BigInteger difficultyAsInteger){
        return (long) (Math.pow(2, 22) / difficultyAsInteger.doubleValue());
    }

    /**
     * Compute the total energy consumption of the network
     * assuming each miner has the same hashrate, and consume the same power
     * @param minerHashrate: the hashrate of each miner, in GH/s
     * @param minerPower: the power consumption of each miner, in Watts
     * @param networkHashrate : the global hashrate of the network
     * @param duration : in second
     * @return energy consumed during duration, in kWh
     */
    // TODO
    public static long globalEnergyConsumption(long minerHashrate, long minerPower, long networkHashrate, long duration){
        double totalPower = (double) (networkHashrate / minerHashrate) * minerPower;
        return (long) (totalPower * (duration / 3600.0));
    }

    private static double calculerProbabiliteMinage(BigInteger difficultyAsInteger) {
        BigInteger nombreMaxHashs = BigInteger.valueOf(2).pow(256);
        return difficultyAsInteger.doubleValue() / nombreMaxHashs.doubleValue();
    }


    public static double networkHashrate(BigInteger difficultyAsInteger) {
        BigInteger hashrate = BigInteger.valueOf(2).pow(256).divide(difficultyAsInteger.multiply(BigInteger.valueOf(AVERAGE_BLOCK_TIME_SECONDS)));
        return hashrate.doubleValue();
    }

    public static double energyConsumedLast24h(double c, double p) {
        double powerInKW = p / 1000.0;
        double hashPerDay = c * 1_000_000_000 * 24 * 60 * 60;
        return hashPerDay * powerInKW / 1_000;
    }





}
