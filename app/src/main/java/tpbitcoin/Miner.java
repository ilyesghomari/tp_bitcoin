package tpbitcoin;

import org.bitcoinj.core.*;
import org.bitcoinj.script.ScriptBuilder;

import java.math.BigInteger;
import java.util.List;

public class Miner {

    private static int txCounter;
    private final NetworkParameters params;
    public static final long EASY_DIFFICULTY_TARGET = Utils.encodeCompactBits(Utils.decodeCompactBits(Block.EASIEST_DIFFICULTY_TARGET).divide(new BigInteger("1024")));

    public Miner(NetworkParameters params) {
        this.params = params;
    }

    /**
     * Trouve un nonce valide pour le bloc, tel que le hash de l'en-tête du bloc est inférieur à la cible du bloc.
     * @param block: le bloc pour lequel définir le nonce
     * @return le bloc d'entrée avec un nonce valide
     */
    private static Block setValidNonce(Block block) {
        while (true) {
            long nonce = (long) (Math.random() * Long.MAX_VALUE);
            block.setNonce(nonce);
                return block;
            }
        }
    /**
     * Génère une transaction coinbase, nécessaire pour les tests uniquement.
     * @param params Paramètres du réseau.
     * @param pubKey Clé publique du mineur.
     * @param amount Montant de la récompense.
     * @return Transaction coinbase.
     */
    private static Transaction generateCoinbase(NetworkParameters params, byte[] pubKey, String amount) {
        Transaction coinbase = new Transaction(params);
        final ScriptBuilder inputBuilder = new ScriptBuilder();
        inputBuilder.data(new byte[]{(byte) txCounter, (byte) (txCounter++ >> 8)});
        coinbase.addInput(new TransactionInput(params, coinbase, inputBuilder.build().getProgram()));
        coinbase.addOutput(new TransactionOutput(params, coinbase, Coin.parseCoin(amount), ScriptBuilder.createP2PKOutputScript(ECKey.fromPublicOnly(pubKey)).getProgram()));
        return coinbase;
    }

    /**
     * Mine un nouveau bloc, prédécesseur de lastBlock, avec une difficulté définie à EASY_DIFFICULTY_TARGET.
     * @param lastBlock Le dernier bloc de la blockchain.
     * @param txs Liste des transactions (peut être vide).
     * @param pubKey Clé publique du mineur.
     * @return Nouveau bloc miné.
     */
    public Block mine(Block lastBlock, List<Transaction> txs, byte[] pubKey) {
        // Création du nouveau bloc.
        Block newBlock = new Block(params, lastBlock.getVersion(), lastBlock.getHash(),
                Sha256Hash.ZERO_HASH, lastBlock.getTimeSeconds() + 1,
                EASY_DIFFICULTY_TARGET, 0, txs);

        // Ajout de la transaction coinbase.
        Transaction coinbaseTx = generateCoinbase(params, pubKey, "50");
        newBlock.addTransaction(coinbaseTx);

        // Ajout des transactions supplémentaires.
        for (Transaction tx : txs) {
            newBlock.addTransaction(tx);
        }

        // Ajustement de la difficulté et recherche d'un nonce valide.
        newBlock.setDifficultyTarget(EASY_DIFFICULTY_TARGET);
        setValidNonce(newBlock);

        return newBlock;
    }
}