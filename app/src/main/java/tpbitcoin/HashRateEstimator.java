package tpbitcoin;

import org.bitcoinj.core.Sha256Hash;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class HashRateEstimator {
    private final int duration; //duration of each experience, in milliseconds
    private final int numberOfTries; // number of experience

    /** Create a new object for estimating the number of SHA256 hashs the host can perform per second
    ** @param duration: (milli-seconds) duration of each experiment
     * @param numberOfTries : number of experiments to run
     */
    public HashRateEstimator(int duration, int numberOfTries) {
        this.duration = duration;
        this.numberOfTries = numberOfTries;
    }

    /**
     * @return : return the hashrate (hash/second)
     */
    public double estimate(){
        byte[] bytes;
        MessageDigest md = Sha256Hash.newDigest();
        Random random = new Random();
        bytes = new byte[256];
        long totalHashes = 0;
        for (int i=1 ; i <= numberOfTries ; i++){
            long startTime = System.currentTimeMillis();
            long count = 0;
            while (System.currentTimeMillis() - startTime < duration) {
                random.nextBytes(bytes);
                md.update(bytes);
                md.digest();
                count++;
            }
            totalHashes += count;
            System.out.println("Experiment " + i + " : " + count + " hashes in " + duration + "ms");
        }
        return  totalHashes / (numberOfTries * (duration / 1000.0));
    }
}
