package com.template.flows;

import com.template.states.MedicalRecordsState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.node.AppServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.node.services.Vault;
import net.corda.core.serialization.SingletonSerializeAsToken;

@CordaService
public class VaultQueries extends SingletonSerializeAsToken {
    private final AppServiceHub serviceHub;

    public VaultQueries(AppServiceHub serviceHub) {
        this.serviceHub = serviceHub;
    }

    //query vault for the patient
    public String queryVaultByPatient(String requestedPatientId) {
        Vault.Page<MedicalRecordsState> results = serviceHub.getVaultService().queryBy(MedicalRecordsState.class);
        if (results.equals(null)) {
            return "";
        }

        StateAndRef<MedicalRecordsState> patient = results.getStates().stream().filter(p -> {
            return p.getState().getData().getPatientName().equals(requestedPatientId);
        }).findAny().orElse(null);
        String medicalRecordsData = patient.getState().component1().getPatientData();

        return medicalRecordsData;
    }
}
