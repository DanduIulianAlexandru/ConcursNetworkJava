package concurs.networking.protobuffprotocol;

import concurs.model.Admin;
import concurs.model.Participant;
import concurs.model.Proba;
import concurs.networking.dto.AdminDTO;
import concurs.networking.dto.DTOUtils;
import concurs.networking.dto.ParticipantDTO;
import concurs.networking.dto.ProbaDTO;
import concurs.networking.rpcprotocol.Response;

import java.util.ArrayList;
import java.util.List;

public class ProtoUtils {
    public static ConcursProtobufs.ConcursRequest createLoginRequest(Admin admin){
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        ConcursProtobufs.Admin adminPB = ConcursProtobufs.Admin.newBuilder().setId(adminDTO.getId())
                .setUsername(adminDTO.getUsername()).setPassword(admin.getPassword()).build();

        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.Login)
                .setAdmin(adminPB).build();
    }

    public static ConcursProtobufs.ConcursRequest createLogoutRequest(Admin admin){
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        ConcursProtobufs.Admin adminPB = ConcursProtobufs.Admin.newBuilder().setId(adminDTO.getId())
                .setUsername(adminDTO.getUsername()).setPassword(admin.getPassword()).build();

        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.Logout)
                .setAdmin(adminPB).build();

    }

    public static ConcursProtobufs.ConcursRequest createGetProbeRequest(){
        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.GetProbe)
                .build();
    }

    public static ConcursProtobufs.ConcursRequest createCautaParticipantiRequest(String searchString){
        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.CautaParticipanti)
                .setSearchString(searchString).build();
    }

    public static ConcursProtobufs.ConcursRequest createAddParticipant(Participant participant){
        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        ConcursProtobufs.Participant participantPB = ConcursProtobufs.Participant.newBuilder().setId(participantDTO.getId())
                .setNume(participantDTO.getNume()).setVarsta(participantDTO.getVarsta()).setProbe(participantDTO.getProbe())
                .build();

        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.AddParticipant)
                .setParticipant(participantPB).build();
    }

    public static ConcursProtobufs.ConcursRequest createGetParticipanti(){
        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.GetParticipanti)
                .build();
    }

    public static ConcursProtobufs.ConcursRequest createGetProba(Integer id){
        return ConcursProtobufs.ConcursRequest.newBuilder().setType(ConcursProtobufs.ConcursRequest.Type.GetProba)
                .setIdProba(id.toString()).build();
    }

    public static ConcursProtobufs.ConcursResponse createOkResponse(){
        return ConcursProtobufs.ConcursResponse.newBuilder().setType(ConcursProtobufs.ConcursResponse.Type.Ok).build();
    }

    public static ConcursProtobufs.ConcursResponse createErrorResponse(String err){
        return ConcursProtobufs.ConcursResponse.newBuilder().setType(ConcursProtobufs.ConcursResponse.Type.Error)
                .setError(err).build();
    }

    public static ConcursProtobufs.ConcursResponse createAddedParticipantResponse(Participant participant){
        ParticipantDTO participantDTO = DTOUtils.getDTO(participant);
        ConcursProtobufs.Participant participantPB = ConcursProtobufs.Participant.newBuilder().setId(participantDTO.getId())
                .setNume(participantDTO.getNume()).setVarsta(participantDTO.getVarsta()).setProbe(participantDTO.getProbe())
                .build();

        return ConcursProtobufs.ConcursResponse.newBuilder().setType(ConcursProtobufs.ConcursResponse.Type.AddedParticipant)
                .setParticipant(participantPB).build();
    }

    public static ConcursProtobufs.ConcursResponse createOkForLoginResponse(Admin admin){
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        ConcursProtobufs.Admin adminPB = ConcursProtobufs.Admin.newBuilder().setId(adminDTO.getId())
                .setUsername(adminDTO.getUsername()).setPassword(admin.getPassword()).build();

        return ConcursProtobufs.ConcursResponse.newBuilder().setType(ConcursProtobufs.ConcursResponse.Type.Ok)
                .setAdmin(adminPB).build();
    }

    public static ConcursProtobufs.ConcursResponse createGetProbeResponse(List<Proba> probe) {
        ConcursProtobufs.ConcursResponse.Builder response = ConcursProtobufs.ConcursResponse.newBuilder()
                .setType(ConcursProtobufs.ConcursResponse.Type.GetProbe);

        List<ProbaDTO> probeDTO = DTOUtils.getProbeDTO(probe);

        for (ProbaDTO probaDTO : probeDTO) {
            ConcursProtobufs.Proba probaPB = ConcursProtobufs.Proba.newBuilder().setId(probaDTO.getId())
                    .setDistanta(probaDTO.getDistanta()).setStil(probaDTO.getStil()).
                    setNrParticipanti(probaDTO.getNrParticipanti()).build();
            response.addProbe(probaPB);
        }

        return response.build();
    }

    public static ConcursProtobufs.ConcursResponse createGetParticipantiResponse(List<Participant> participanti){
        ConcursProtobufs.ConcursResponse.Builder response = ConcursProtobufs.ConcursResponse.newBuilder()
                .setType(ConcursProtobufs.ConcursResponse.Type.GetParticipanti);

        List<ParticipantDTO> participantiDTO = DTOUtils.getParticipantiDTO(participanti);

        for(ParticipantDTO participantDTO : participantiDTO){
            ConcursProtobufs.Participant participantPB = ConcursProtobufs.Participant.newBuilder().setId(participantDTO.getId())
                    .setNume(participantDTO.getNume()).setVarsta(participantDTO.getVarsta()).
                    setProbe(participantDTO.getProbe()).build();
            response.addParticipanti(participantPB);
        }

        return response.build();
    }

    public static ConcursProtobufs.ConcursResponse createGetProbaResponse(Proba proba){
        ProbaDTO probaDTO = DTOUtils.getDTO(proba);
        ConcursProtobufs.Proba probaPB = ConcursProtobufs.Proba.newBuilder().setId(probaDTO.getId())
                .setDistanta(probaDTO.getDistanta()).setStil(probaDTO.getStil())
                .setNrParticipanti(probaDTO.getNrParticipanti()).build();

        return ConcursProtobufs.ConcursResponse.newBuilder().setType(ConcursProtobufs.ConcursResponse.Type.GetProba)
                .setProba(probaPB).build();
    }

    public static ConcursProtobufs.ConcursResponse createCautaParticipantiResponse(List<Participant> participanti){
        ConcursProtobufs.ConcursResponse.Builder response = ConcursProtobufs.ConcursResponse.newBuilder()
                .setType(ConcursProtobufs.ConcursResponse.Type.SearchedParticipanti);

        List<ParticipantDTO> participantiDTO = DTOUtils.getParticipantiDTO(participanti);

        for(ParticipantDTO participantDTO : participantiDTO){
            ConcursProtobufs.Participant participantPB = ConcursProtobufs.Participant.newBuilder().setId(participantDTO.getId())
                    .setNume(participantDTO.getNume()).setVarsta(participantDTO.getVarsta()).
                    setProbe(participantDTO.getProbe()).build();
            response.addParticipanti(participantPB);
        }

        return response.build();
    }

    public static String getError(ConcursProtobufs.ConcursResponse response){
        return response.getError();
    }

    public static Admin getAdmin(ConcursProtobufs.ConcursResponse response){
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(response.getAdmin().getId());
        adminDTO.setUsername(response.getAdmin().getUsername());
        adminDTO.setPasswd(response.getAdmin().getPassword());
        return DTOUtils.getFromDTO(adminDTO);
    }

    public static Proba getProba(ConcursProtobufs.ConcursResponse response){
        if (response.getProba().getId().equals("") && response.getProba().getDistanta().equals("")){
            return null;
        }
        return DTOUtils.getFromDTO(new ProbaDTO(response.getProba().getId(), response.getProba().getDistanta(),
                response.getProba().getStil(), response.getProba().getNrParticipanti()));
    }

    public static Participant getParticipant(ConcursProtobufs.ConcursResponse response){
        return DTOUtils.getFromDTO(new ParticipantDTO(response.getParticipant().getId(), response.getParticipant().getNume(),
                response.getParticipant().getVarsta(), response.getParticipant().getProbe()));
    }

    public static List<Proba> getProbe(ConcursProtobufs.ConcursResponse response){
        List<ProbaDTO> probeDTO = new ArrayList<>();
        for(int i = 0; i < response.getProbeCount(); ++i){
            ConcursProtobufs.Proba probaPB = response.getProbe(i);
            probeDTO.add(new ProbaDTO(probaPB.getId(), probaPB.getDistanta(), probaPB.getStil(), probaPB.getNrParticipanti()));
        }

        return DTOUtils.getProbeFromDTO(probeDTO);
    }

    public static List<Participant> getParticipanti(ConcursProtobufs.ConcursResponse response){
        List<ParticipantDTO> participantiDTO = new ArrayList<>();
        for(int i = 0; i < response.getParticipantiCount(); ++i){
            ConcursProtobufs.Participant participantPB = response.getParticipanti(i);
            participantiDTO.add(new ParticipantDTO(participantPB.getId(), participantPB.getNume(),
                    participantPB.getVarsta(), participantPB.getProbe()));
        }

        return DTOUtils.getParticipantiFromDTO(participantiDTO);
    }

    public static Admin getAdmin(ConcursProtobufs.ConcursRequest request){
        return DTOUtils.getFromDTO(new AdminDTO(request.getAdmin().getId(), request.getAdmin().getUsername(),
                request.getAdmin().getPassword()));
    }

    public static Integer getIdProba(ConcursProtobufs.ConcursRequest request){
        return Integer.parseInt(request.getIdProba());
    }

    public static Participant getParticipant(ConcursProtobufs.ConcursRequest request){
        return DTOUtils.getFromDTO(new ParticipantDTO(request.getParticipant().getId(), request.getParticipant().getNume(),
                request.getParticipant().getVarsta(), request.getParticipant().getProbe()));
    }

    public static String getSearchString(ConcursProtobufs.ConcursRequest request){
        return request.getSearchString();
    }
}
