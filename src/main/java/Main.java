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

            String pk = mgr.getPubKey();
            System.out.println("PK: " + pk);

            String sk = mgr.exportPrivateKey(password);
            System.out.println("SK: " + sk);
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
    }
}
