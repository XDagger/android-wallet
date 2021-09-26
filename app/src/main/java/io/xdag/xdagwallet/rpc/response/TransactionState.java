package io.xdag.xdagwallet.rpc.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.Response;


import java.io.IOException;


public class TransactionState extends Response<TransactionState.TransactionDTO> {

    @Override
    @JsonDeserialize(using = TransactionState.ResponseDeserialiser.class)
    public void setResult(TransactionDTO result) {
        super.setResult(result);
    }

    public TransactionDTO getTransactionDTO(){
        return getResult();
    }

    public static class TransactionDTO{
        public String hash;
        public String state;

        public TransactionDTO() {
        }

        public TransactionDTO(String hash, String state) {
            this.hash = hash;
            this.state = state;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static class ResponseDeserialiser extends JsonDeserializer<TransactionDTO> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public TransactionDTO deserialize(
                JsonParser jsonParser,
                DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, TransactionDTO.class);
            } else {
                return null;  // null is wrapped by Optional in above getter
            }
        }
    }

}
