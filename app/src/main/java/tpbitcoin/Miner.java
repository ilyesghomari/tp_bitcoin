package tpbitcoin;

import org.bitcoinj.core.*;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.store.MemoryBlockStore;

import java.math.BigInteger;
import java.util.List;

public class Miner {

    private static int  txCounter;
    private final NetworkParameters params;
    public static final long EASY_DIFFICULTY_TARGET = Utils.encodeCompactBits(Utils.decodeCompactBits(Block.EASIEST_DIFFICULTY_TARGET).divide(new BigInteger("1024")));
    public static final long SOMEWHAT_HARDER_DIFFICULTY_TARGET = Utils.encodeCompactBits(Utils.decodeCompactBits(Block.EASIEST_DIFFICULTY_TARGET).divide(new BigInteger("65536")));

    public Miner(NetworkParameters params){
        this.params = params;
    }

    /**
     * find a valid a nonce, e.g such that hash of block header is smaller than the block's target
     * @param block: the block on which to set the nonce
     * @return input block with a valid nonce
     */
    // TODO
    private static Block setValidNonce(Block block){
        while (true) {
            long nonce = (long) (Math.random() * Long.MAX_VALUE);
            block.setNonce(nonce);
            Sha256Hash hash = block.getHash();
            BigInteger difficultyTarget = block.getDifficultyTargetAsInteger();
            if (Sha256Hash.wrap(hash.getBytes()).compareTo(Sha256Hash.wrap(difficultyTarget.toByteArray())) < 0) {
                return block;
            }
        }
    }

    /* borrowed from bitcoinj.core, not the real thing, for testing only
     * needed for creating a fake coinbase that pass bitcoinj basic verification
     */
    private  static  Transaction generateCoinbase(NetworkParameters params, byte[] pubKey, String amount){
        Transaction coinbase = new Transaction(params);
        final ScriptBuilder inputBuilder = new ScriptBuilder();
        inputBuilder.data(new byte[]{(byte) txCounter, (byte) (txCounter++ >>8)});
        coinbase.addInput(new TransactionInput(params, coinbase,
                inputBuilder.build().getProgram()));
        coinbase.addOutput(new TransactionOutput(params, coinbase, Coin.parseCoin(amount),
                ScriptBuilder.createP2PKOutputScript(ECKey.fromPublicOnly(pubKey)).getProgram()));
        return coinbase;
    }

        /*public boolean isCompressedPulicKey(byte[] pubKey){
            return pubKey.length == 33 && (pubKey[0] == 0x02 || pubKey[0] == 0x03);
        }*/

    /**
     * Create a new block, predecessor of lastBlock. Difficulty of the new bloc kis set to EASY_DIFFICULTY
     * @param lastBlock: the last block of the blockchain
     * @param txs: a list of transactions (may be empty)
     * @param pubKey: the public key of the miner
     * @return
     */
    // TODO
    public Block mine(Block lastBlock, List<Transaction> txs, byte[] pubKey){

        Block newBlock = new Block(params, lastBlock.getVersion(), lastBlock.getHash(),
                (Sha256Hash) null, lastBlock.getTimeSeconds() +1,
                lastBlock.getDifficultyTarget(), 10000, txs );

        ECKey key = ECKey.fromPublicOnly(pubKey);
        Transaction coinbaseTx = generateCoinbase(params, key.getPubKey(), "50");
        newBlock.addTransaction(coinbaseTx);
        for (Transaction tx : txs) {
            newBlock.addTransaction(tx);
        }
        newBlock.setDifficultyTarget(EASY_DIFFICULTY_TARGET);
        setValidNonce(newBlock);
        return newBlock;

    }
}




