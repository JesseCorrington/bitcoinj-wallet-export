

public class Main {
    public static void main(String[] args) {
        BitcoinManager mgr = new BitcoinManager();

        if (args.length < 2) {
            System.out.println("Usage: <wallet-file> <password>");
            return;
        }

        String filename = args[0];
        String password = args[1];

        //String filename = System.getProperty("user.home") + "/dev" + "/bitcoinkit.wallet";

        try {
            mgr.load(filename);
            String sk = mgr.exportPrivateKey(password);
            System.out.print("SK: " + sk);
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
    }
}
