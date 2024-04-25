package concurs.model.validators;

import concurs.model.Participant;

public class ParticipantValidator implements Validator<Participant>{
    public ParticipantValidator(){

    }

    @Override
    public void validate(Participant entity) throws ValidationException {
        String errMsg = "";
        if (entity.getNume().length() < 3 || entity.getNume() == null){
            errMsg += "Nume participant invalid";
        }
        if (entity.getVarsta() < 5 || entity.getVarsta() > 90){
            errMsg += "Varsta participant invalida";
        }
        if(entity.getProbe() == null){
            errMsg += "Probe array is null";
        }
        if (!errMsg.equals("")) {
            throw new ValidationException(errMsg);
        }
    }
}
