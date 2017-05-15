import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.*;
import org.spongycastle.crypto.params.KeyParameter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class WalletReader {
    private NetworkParameters networkParams = MainNetParams.get();
    private Wallet wallet = null;

    public class KeyPair {
        public String address;
        public String privateKey;

        public String toString() {
            return "Address: " + address + ", " + "Private Key: " + privateKey;
        }
    }

    public List<KeyPair> exportPrivateKeys(String password) throws Exception{
        List<ECKey> keys = wallet.getImportedKeys();

        List<KeyPair> keyPairs = new ArrayList<KeyPair>();

        for (int i = 0; i < keys.size(); i++) {
            ECKey eckey = keys.get(i);

            KeyPair keyPair = new KeyPair();
            keyPair.address = eckey.toAddress(networkParams).toString();
            keyPair.privateKey = exportPrivateKey(eckey, password);

            keyPairs.add(keyPair);
        }

        return keyPairs;
    }

    public void load(String filename) throws Exception {
        File walletFile = new File(filename);
        if (!walletFile.exists()) {
            throw new Exception("No wallet file found at: " + walletFile);
        }

        try {
            this.wallet = loadWalletFromFile(walletFile);
        }
        catch (UnreadableWalletException e) {
            throw new Exception("Failed to load wallet file: " + walletFile + " -- " + e.getMessage());
        }

        wallet.cleanup();
    }


    private Wallet loadWalletFromFile(File file) throws UnreadableWalletException {
        try {
            FileInputStream stream = null;

            try {
                WalletProtobufSerializer reader = new WalletProtobufSerializer();
                Wallet wallet = reader.readWallet(new FileInputStream(file));

                if (!wallet.isConsistent()) {
                    System.out.print("Loaded an inconsistent wallet");
                }

                return wallet;
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            throw new UnreadableWalletException("Could not open file", e);
        }
    }

    private String exportPrivateKey(ECKey ecKey, String password) throws Exception {
        KeyParameter aesKey = null;

        try {
            aesKey = passwordToAESKey(password.toCharArray());
            ECKey decryptedKey = ecKey.decrypt(wallet.getKeyCrypter(), aesKey);
            return decryptedKey.getPrivateKeyEncoded(networkParams).toString();
        } catch (KeyCrypterException e) {
            throw new Exception("Wrong Password");
        } finally {
            wipeAesKey(aesKey);
        }
    }

    private KeyParameter passwordToAESKey(char[] utf16Password) throws Exception {
        KeyCrypter keyCrypter = wallet.getKeyCrypter();

        if (keyCrypter == null) {
            throw new Exception("Wallet is not protected.");
        }

        return deriveKeyAndWipePassword(utf16Password, keyCrypter);
    }

    private KeyParameter deriveKeyAndWipePassword(char[] utf16Password, KeyCrypter keyCrypter)
            throws Exception {

        if (utf16Password == null) {
            throw new Exception("No password provided.");
        }

        try {
            return keyCrypter.deriveKey(CharBuffer.wrap(utf16Password));
        } finally {
            Arrays.fill(utf16Password, '\0');
        }
    }

    private void wipeAesKey(KeyParameter aesKey) {
        if (aesKey != null) {
            Arrays.fill(aesKey.getKey(), (byte) 0);
        }
    }
}
