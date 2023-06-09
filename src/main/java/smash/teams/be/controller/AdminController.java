package smash.teams.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.dto.ResponseDTO;
import smash.teams.be.dto.admin.AdminRequest;
import smash.teams.be.dto.admin.AdminResponse;
import smash.teams.be.service.AdminService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(("/auth/admin"))
@RestController
public class AdminController {
    private final AdminService adminService;

    @Log
    @GetMapping("")
    public ResponseEntity<?> getAdminPage(@RequestParam(defaultValue = "") String teamName,
                                          @RequestParam(defaultValue = "") String keyword,
                                          @RequestParam(defaultValue = "0") int page) {
        AdminResponse.GetAdminPageOutDTO getAdminPageOutDTO = adminService.getAdminPage(teamName, keyword, page);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(getAdminPageOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Log
    @PatchMapping("/user")
    public ResponseEntity<?> updateAuthAndTeam(@RequestBody @Valid AdminRequest.UpdateAuthAndTeamInDTO updateAuthAndTeamInDTO, Errors errors) {
        adminService.updateAuthAndTeam(updateAuthAndTeamInDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }

    @Log
    @PostMapping("/team")
    public ResponseEntity<?> add(@RequestBody @Valid AdminRequest.AddInDTO addInDTO, Errors errors) {
        AdminResponse.AddOutDTO addOutDTO = adminService.add(addInDTO); // OSIV = false, 비영속

        ResponseDTO<?> responseDTO = new ResponseDTO<>(addOutDTO);
        return ResponseEntity.ok().body(responseDTO);
    }

    @Log
    @DeleteMapping("/team/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        adminService.delete(id);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        return ResponseEntity.ok().body(responseDTO);
    }
}
