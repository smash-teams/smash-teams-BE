package smash.teams.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.team.TeamRequest;
import smash.teams.be.model.team.Team;
import smash.teams.be.service.TeamService;

@RequiredArgsConstructor
@RestController("/auth/admin/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody TeamRequest.AddDTO addDTO) {
        Team team = teamService.add(addDTO); // OSIV = false, 비영속

        ResponseDTO<?> responseDTO = new ResponseDTO<>(team);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        teamService.delete(id);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }
}
