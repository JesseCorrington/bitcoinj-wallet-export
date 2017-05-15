import java.util.List;

public class Main {
    public static void main(String[] args) {
        BitcoinManager mgr = new BitcoinManager();

        if (args.length < 1) {
            System.out.println("Usage: <wallet-file> <password>");
            return;
        }

        String filename = args[0];
        String password = args.length >= 2? args[1] : "";

        try {
            mgr.load(filename);

            List<BitcoinManager.KeyPair> keyPairs = mgr.exportPrivateKeys(password);
            for (int i = 0; i < keyPairs.size(); i++) {
                BitcoinManager.KeyPair keyPair = keyPairs.get(i);
                System.out.println(i + ")  " + keyPair);
            }
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
    }
}
