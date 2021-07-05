package io.xdag.xdagwallet.config;

import android.os.Environment;



import java.security.spec.InvalidKeySpecException;

public class Constants {

    /**
     * Default data directory.
     */
    public static final String DEFAULT_DATA_DIR = ".";

    /**
     * Network versions.
     */
    public static final short MAINNET_VERSION = 0;
    public static final short TESTNET_VERSION = 0;
    public static final short DEVNET_VERSION = 0;

    /**
     * Name of this client.
     */
    public static final String CLIENT_NAME = "Smartx";

    /**
     * Version of this client.
     */
    public static final String CLIENT_VERSION = "2.1.1";

    /**
     * Algorithm name for the 256-bit hash.
     */
    public static final String HASH_ALGORITHM = "BLAKE2B-256";

    /**
     * Name of the config directory.
     */
    public static final String CONFIG_DIR = "config";

    /**
     * Name of the database directory.
     */
    public static final String DATABASE_DIR = "database";

    /**
     * The default IP port for p2p protocol
     */
    public static final int DEFAULT_P2P_PORT = 5161;

    /**
     * The default IP port for RESTful API.
     */
    public static final int DEFAULT_API_PORT = 5171;

    /**
     * The default user agent for HTTP requests.
     */
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0";

    /**
     * The default connect timeout.
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 4000;

    /**
     * The default read timeout.
     */
    public static final int DEFAULT_READ_TIMEOUT = 4000;

    /**
     * The number of blocks per day.
     */
    public static final long BLOCKS_PER_DAY = 2L * 60L * 24L;

    /**
     * The number of blocks per year.
     */
    public static final long BLOCKS_PER_YEAR = 2L * 60L * 24L * 365L;

    /**
     * The create wallet request code
     */
    public static final int REQUEST_CODE_CREATE_WALLET = 0;

    /**
     * The input password dialog result code
     */
    public static final int REQUEST_CODE_SET_PWD = 1;

    /**
     * The set password dialog result code
     */
    public static final int REQUEST_CODE_INPUT_PWD_LOGIN = 2;

    /**
     * The copy mnemonic dialog result code
     */
    public static final int REQUEST_CODE_COPY_MNEMONIC = 3;

    /**
     * The copy mnemonic dialog result code
     */
    public static final int REQUEST_CODE_IMPORT_WALLET = 4;

    /**
     * The set password dialog result code
     */
    public static final int REQUEST_CODE_SEND_TRANSACTION = 5;

    /**
     * The set password dialog result code
     */
    public static final int REQUEST_CODE_SHOW_MESSAGE = 6;

    /**
     * The public-private key pair for signing coinbase transactions.
     */


    /**
     * Address bytes of {@link this#COINBASE_KEY}. This is stored as a cache to
     * avoid redundant h160 calls.
     */


    /**
     * The public-private key pair of the genesis validator.
     */


    public static final String EXTRA_ADDRESS = "ADDRESS";
    public static final String EXTRA_CONTRACT_ADDRESS = "CONTRACT_ADDRESS";

    /**
     * The rpc url constants
     * */
    //public static final String HOST_TESTNET = "https://testnet2.smartx.one/v1.0.0/";
    public static final String HOST_TESTNET = "http://120.132.102.139:5171/v1.0.0/";
    public static final String URL_GET_NODE_INFO = HOST_TESTNET + "info";
    public static final String URL_GET_ACCOUNT_BALANCE = HOST_TESTNET + "balance";
    public static final String URL_GET_TX_LIST = HOST_TESTNET + "account/transactions";
    public static final String URL_GET_TRANSFERNONCE = HOST_TESTNET + "transaction/transfernonce";
    public static final String URL_SEND_RAW_TX = HOST_TESTNET + "transaction/raw";

    public static final String ExternalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String WalletDataFilePath = ExternalStorageDir + "/smartx/" + "wallet.data";


    private Constants() {
    }
}

