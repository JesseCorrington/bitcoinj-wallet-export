import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.*;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class BitcoinManager {
    private NetworkParameters networkParams = MainNetParams.get();
    private Wallet wallet = null;


    public void load(String filename) throws Exception{
        // Try to read the wallet from storage, create a new one if not possible.
        File walletFile = new File(filename);

        if (!walletFile.exists()) {
            // Stop here, because the caller might want to create an encrypted wallet and needs to supply a password.
            throw new Exception("No wallet file found at: " + walletFile);
        }

        try {
            this.wallet = loadWalletFromFile(walletFile);
        }
        catch (Exception e) {
            throw new Exception("Failed to load wallet file: " + walletFile + " -- " + e.getMessage());
        }

        wallet.cleanup();
    }

    public Wallet loadWalletFromFile(File f) throws Exception {
        try {
            FileInputStream stream = null;

            try {
                stream = new FileInputStream(f);

                Wallet wallet = new Wallet(networkParams);
                //wallet.addExtension(new LastWalletChangeExtension());

                Protos.Wallet walletData = WalletProtobufSerializer.parseToProto(stream);
                WalletProtobufSerializer reader = new WalletProtobufSerializer();
                wallet = reader.readWallet(stream);

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
            throw new Exception("Could not open file", e);
        }
    }

    public String exportPrivateKey(String password) throws Exception {
        char[] utf16Password = password.toCharArray();
        KeyParameter aesKey = null;
        ECKey decryptedKey = null;
        DumpedPrivateKey dumpedKey = null;

        try {
            ECKey ecKey = wallet.getIssuedReceiveKeys().get(0);

            Date creationDate = new Date(ecKey.getCreationTimeSeconds() * 1000);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timestamp = format.format(creationDate);

            aesKey = aesKeyForPassword(utf16Password);
            decryptedKey = ecKey.decrypt(wallet.getKeyCrypter(), aesKey);
            return decryptedKey.getPrivateKeyEncoded(networkParams).toString() + "\t" + timestamp;
        } catch (KeyCrypterException e) {
            throw new Exception("Wrong Password");
        } finally {
            wipeAesKey(aesKey);

            if (decryptedKey != null) {
                // TODO:
                //decryptedKey.clearPrivateKey();
            }
        }
    }

    /* --- Encryption/decryption --- */

    private KeyParameter aesKeyForPassword(char[] utf16Password) throws Exception {
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
