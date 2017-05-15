import java.util.List;

public class Main {
    public static void main(String[] args) {
        WalletReader reader = new WalletReader();

        if (args.length < 1) {
            System.out.println("Usage: <wallet-path> <password>");
            return;
        }

        String filename = args[0];
        String password = args.length >= 2? args[1] : "";

        try {
            reader.load(filename);

            List<WalletReader.KeyPair> keyPairs = reader.exportPrivateKeys(password);
            for (int i = 0; i < keyPairs.size(); i++) {
                WalletReader.KeyPair keyPair = keyPairs.get(i);
                System.out.println(i + ")  " + keyPair);
            }
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
    }
}
