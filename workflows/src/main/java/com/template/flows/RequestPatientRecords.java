package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.MedicalRecordsContract;
import com.template.states.MedicalRecordsState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;

import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class RequestPatientRecords extends FlowLogic<SignedTransaction> {

    private final ProgressTracker progressTracker = new ProgressTracker();
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private String patientName;
    private Party initiatingHospital ;
    private Party respondingHospital;

    public RequestPatientRecords(Party from, String patientName){
        this.respondingHospital = from;
        this.patientName = patientName;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        final FlowSession recipientSession = initiateFlow(respondingHospital);
        final UntrustworthyData<String> recipientData = recipientSession.sendAndReceive(String.class, patientName);
        String medicalRecordsData = (String) recipientData.unwrap(it -> it);
        if (medicalRecordsData.equals(""))
            //throw  new patientNotFoundException();
            throw new FlowException();

        initiatingHospital = getOurIdentity();
        // Step 1. Get a reference to the notary service on our network and our key pair.
        // Note: ongoing work to support multiple notary identities is still in progress.
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Compose the State that carries the Hello World message
        final MedicalRecordsState output = new MedicalRecordsState(patientName,medicalRecordsData,initiatingHospital, respondingHospital);

        // Step 3. Create a new TransactionBuilder object.
        final TransactionBuilder builder = new TransactionBuilder(notary);

        // Step 4. Add the iou as an output state, as well as a command to the transaction builder.
        builder.addOutputState(output);
        builder.addCommand(new MedicalRecordsContract.Commands.Request(), Arrays.asList(this.initiatingHospital.getOwningKey(),this.respondingHospital.getOwningKey()) );


        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(recipientSession)));

        // Step 7. Assuming no exceptions, we can now finalise the transaction
        return subFlow(new FinalityFlow(stx, Arrays.asList(recipientSession)));


    }
}
