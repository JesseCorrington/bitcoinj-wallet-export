# bitcoinj-wallet-export
This is a tool for decrypting and exporting [bitcoinj](https://bitcoinj.github.io/) wallet private keys. This only works with non deterministic keys that were manually added to the wallet, and does not work for deterministic keys created from a seed.

There are plenty of tools already available for working with HD wallets, but I created this specifically for exporting old [Hive](https://hivewallet.com/) wallets, as Hive ceaseed operations in April of 2016. I recently found an old hive wallet backup, and had no means of recovering the encrypted private key, so I wrote this small utility. Hive used bitcoinj and this tool does as well for compatibility.

This has only been minimally tested on OS X for my single use case, but there should be nothing keeping it from running under Windows or Linux.

## Setup
1) clone this repo
1) Install [Maven](https://maven.apache.org/install.html)
2) `cd <repo path>`
3) `./build.sh`

## Usage
`./run.sh <wallet path> <decryption password>`

In the case of a backed up Hive wallet, the wallet is named `bitcoinkit.wallet`
