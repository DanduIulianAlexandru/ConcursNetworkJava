package concurs.networking.dto;

import concurs.model.*;

import java.util.ArrayList;
import java.util.List;

public class DTOUtils {
    public static Admin getFromDTO(AdminDTO adminDTO){
        String id = adminDTO.getId();
        String username = adminDTO.getUsername();
        String password = adminDTO.getPasswd();
        Admin admin = new Admin(username, password);
        admin.setId(Integer.parseInt(id));
        return admin;
    }

    public static Participant getFromDTO(ParticipantDTO participantDTO){
        String id = participantDTO.getId();
        String nume = participantDTO.getNume();
        String varsta = participantDTO.getVarsta();
        String probeS = participantDTO.getProbe();

        List<Integer> probe = new ArrayList<>();
        String[] parts = probeS.split("\u0020", 0);
        for(String part : parts){
            probe.add(Integer.parseInt(part));
        }

        Participant participant = new Participant(nume, Integer.parseInt(varsta), probe);
        participant.setId(Integer.parseInt(id));
        return participant;
    }

    public static Proba getFromDTO(ProbaDTO probaDTO){
        String id = probaDTO.getId();
        String stilS = probaDTO.getStil();
        String distantaS = probaDTO.getDistanta();

        Distanta distanta = switch (distantaS){
            case "M50" -> Distanta.M50;
            case "M200" -> Distanta.M200;
            case "M800" -> Distanta.M800;
            case "M1500" -> Distanta.M1500;
            default -> Distanta.M50;
        };

        Stil stil = switch (stilS){
            case "LIBER" -> Stil.LIBER;
            case "FLUTURE" -> Stil.FLUTURE;
            case "MIXT" -> Stil.MIXT;
            case "SPATE" -> Stil.SPATE;
            default -> Stil.LIBER;
        };

        Proba proba = new Proba(distanta, stil);
        proba.setId(Integer.parseInt(id));
        proba.setNrParticipanti(Integer.parseInt(probaDTO.getNrParticipanti()));
        return proba;
    }

    public static List<ProbaDTO> getProbeDTO(List<Proba> probe){
        List<ProbaDTO> probeDTO = new ArrayList<>();
        for(Proba p : probe){
            probeDTO.add(getDTO(p));
        }

        return probeDTO;
    }

    public static List<Proba> getProbeFromDTO(List<ProbaDTO> probeDTO){
        List<Proba> probe = new ArrayList<>();
        for(ProbaDTO p : probeDTO){
            probe.add(getFromDTO(p));
        }

        return probe;
    }

    public static List<ParticipantDTO> getParticipantiDTO(List<Participant> participants){
        List<ParticipantDTO> participantDTO = new ArrayList<>();
        for(Participant p : participants){
            participantDTO.add(getDTO(p));
        }

        return participantDTO;
    }

    public static List<Participant> getParticipantiFromDTO(List<ParticipantDTO> participantDTO){
        List<Participant> participants = new ArrayList<>();
        for(ParticipantDTO p : participantDTO){
            participants.add(getFromDTO(p));
        }

        return participants;
    }

    public static AdminDTO getDTO(Admin admin){
        String id = admin.getId().toString();
        return new AdminDTO(id, admin.getUsername(), admin.getPassword());
    }

    public static ParticipantDTO getDTO(Participant participant){
        String id = participant.getId().toString();
        String varsta = participant.getVarsta().toString();
        String probe = "";
        int contor = 0;

        for(Integer nr : participant.getProbe()){
            ++contor;
            probe += nr.toString();
            if(contor != participant.getProbe().size()){
                probe += " ";
            }
        }

        return new ParticipantDTO(id, participant.getNume(), varsta, probe);
    }

    public static ProbaDTO getDTO(Proba proba){
        String id = proba.getId().toString();
        String distanta = switch (proba.getDistanta()){
            case M50 -> "M50";
            case M200 -> "M200";
            case M800 -> "M800";
            case M1500 -> "M1500";
        };

        String stil = switch (proba.getStil()){
            case MIXT -> "MIXT";
            case LIBER -> "LIBER";
            case SPATE -> "SPATE";
            case FLUTURE -> "FLUTURE";
        };

        return new ProbaDTO(id, distanta, stil, proba.getNrParticipanti().toString());
    }
}
