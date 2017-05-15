import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import java.nio.ByteBuffer;
import java.util.Date;



public class LastWalletChangeExtension implements WalletExtension {
    static final String EXTENSION_ID = LastWalletChangeExtension.class.getName();

    private Date lastWalletChangeDate;

    public Date getLastWalletChangeDate() {
        return lastWalletChangeDate;
    }

    public void setLastWalletChangeDate(Date date) {
        lastWalletChangeDate = date;
    }


    /** Returns a Java package/class style name used to disambiguate this extension from others. */
    public String getWalletExtensionID() {
        return EXTENSION_ID;
    }

    /**
     * If this returns true, the mandatory flag is set when the wallet is serialized and attempts to load it without
     * the extension being in the wallet will throw an exception. This method should not change its result during
     * the objects lifetime.
     */
    public boolean isWalletExtensionMandatory() {
        return false;
    }

    /** Returns bytes that will be saved in the wallet. */
    public byte[] serializeWalletExtension() {
        long timestamp = (lastWalletChangeDate != null) ? lastWalletChangeDate.getTime() : 0;
        return ByteBuffer.allocate(8).putLong(timestamp).array();
    }

    /** Loads the contents of this object from the wallet. */
    public void deserializeWalletExtension(Wallet containingWallet, byte[] data) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(data).flip();
        long timestamp = buffer.getLong();
        lastWalletChangeDate = (timestamp > 0) ? new Date(timestamp) : null;
    }

    public String toString() {
        return "LastWalletChangeExtension: date = " + lastWalletChangeDate;
    }
}
