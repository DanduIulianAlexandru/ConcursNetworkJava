package concurs.model.validators;

import concurs.model.Admin;

public class AdminValidator implements Validator<Admin> {
    public AdminValidator(){

    }

    @Override
    public void validate(Admin entity) throws ValidationException {
        String errMsg = "";
        if (entity.getPassword().equals("") || entity.getPassword() == null){
            errMsg += "Admin password cannot be empty";
        }
        if (entity.getUsername().equals("") || entity.getUsername() == null){
            errMsg += "Admin username cannot be empty";
        }
        if (!errMsg.equals("")) {
            throw new ValidationException(errMsg);
        }
    }
}
