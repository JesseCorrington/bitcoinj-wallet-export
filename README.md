# bitcoinj-wallet-export
BTC Wallet decryptor for  


A tool for decrypting and exporting bitcoinj wallet private keys. This only works with non deterministic keys that were manually added to the wallet, and does not work for deterministic keys created from a seed.

There are plenty of tools already available for working with HD wallets, but I created this specifically for old Hive wallets. Hive ceaseed operations in April of 2016. I recently found an old hive wallet backup, and had to means of recovering the encrypted private key, so I write this small utility.

## Setup
1) clone this repo
1) Install Maven: https://maven.apache.org/install.html
2) `cd <repo path>`
3) `./build.sh`

## Usage
`./run.sh`