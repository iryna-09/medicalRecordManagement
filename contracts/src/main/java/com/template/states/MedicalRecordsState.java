package com.template.states;

import com.template.contracts.MedicalRecordsContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(MedicalRecordsContract.class)
public class MedicalRecordsState implements ContractState {

    //private variables
    private String patientName;
    private String patientData;
    private Party receiverHospital;
    private Party requestHospital;


    @ConstructorForDeserialization
    public MedicalRecordsState(String patientName,
                               String patientData,
                               Party requestHospital, Party receiverHospital) {
        this.patientName = patientName;
        this.patientData = patientData;
        this.requestHospital = requestHospital;
        this.receiverHospital = receiverHospital;
    }


    //getters
    public String getPatientName() { return patientName; }
    public String getPatientData() {return patientData; }

    public Party getRequestHospital() { return requestHospital; }
    public Party getReceiverHospital() { return receiverHospital; }
   @Override
   public String toString() {
       return "patientName : " + patientName+ "patientData : " + patientData;
   }

    /* This method will indicate who are the participants and required signers when
     * this state is used in a transaction. */
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(requestHospital, receiverHospital);
    }
}