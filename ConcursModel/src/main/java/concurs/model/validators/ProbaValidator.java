package concurs.model.validators;

import concurs.model.Proba;

public class ProbaValidator implements Validator<Proba> {
    public ProbaValidator(){

    }

    @Override
    public void validate(Proba entity) throws ValidationException {
        String errMsg = "";
        if (entity.getDistanta() == null){
            errMsg += "Distanta is null";
        }
        if (entity.getStil() == null){
            errMsg += "Stil is null";
        }
        if (!errMsg.equals("")) {
            throw new ValidationException(errMsg);
        }
    }
}
