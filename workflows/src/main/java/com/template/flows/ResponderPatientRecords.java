package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.TemplateContract;
import com.template.states.MedicalRecordsState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@InitiatedBy(RequestPatientRecords.class)
public class ResponderPatientRecords extends FlowLogic<SignedTransaction> {

    private FlowSession session;
    private Party requestHospital ;
    private Party receiverHospital;

    public ResponderPatientRecords(FlowSession session) {
        this.session = session;
        this.receiverHospital = this.session.getCounterparty();
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        String requestedPatientId = session.receive(String.class).unwrap(it -> it);

        //query vault for the patient
        VaultQueries vaultQueriesService = getServiceHub().cordaService(VaultQueries.class);
        final String medicalRecordsData = vaultQueriesService.queryVaultByPatient(requestedPatientId);

        if (medicalRecordsData == "") {
            session.send("");
            return null;
        } else {
            session.send(medicalRecordsData);
        }
        //it send medical record to Initiator, but then it countinues to run the flow. But it should wait for
        // the tx from Initiator

        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(session) {
            @Suspendable
            @Override
            protected void checkTransaction(SignedTransaction stx) throws FlowException {

            }
        });
        //Stored the transaction into data base.
        subFlow(new ReceiveFinalityFlow(session, signedTransaction.getId()));
        return null;
    }
}
