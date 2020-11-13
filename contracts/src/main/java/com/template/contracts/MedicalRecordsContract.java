package com.template.contracts;

import com.template.states.MedicalRecordsState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import java.util.ArrayList;
import java.util.Arrays;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class MedicalRecordsContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.MedicalRecordsContract";

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {

        /* We can use the requireSingleCommand function to extract command data from transaction.
         * However, it is possible to have multiple commands in a signle transaction.*/

        ArrayList<String> rejectCountries = new ArrayList<String>(Arrays.asList("RU"));

        final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        String requesterCountry = command.component2().get(0).getName().getCountry();
        requireThat(require -> {
            require.using("Initiator should be not from rejected countries.", !(rejectCountries.contains(requesterCountry)));
            return null;
        });
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        //In our hello-world app, We will only have one command.
        class Create implements Commands {}
        class Request implements Commands {}
    }
}