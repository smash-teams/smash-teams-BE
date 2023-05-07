package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.annotation.Log;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception500;
import smash.teams.be.dto.admin.AdminRequest;
import smash.teams.be.dto.admin.AdminResponse;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserQueryRepository;
import smash.teams.be.model.user.UserRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    @Log
    @Transactional(readOnly = true)
    public AdminResponse.GetAdminPageOutDTO getAdminPage(String teamName, String keyword, int page) {
        // teamName이 DB에 있는 팀인지 확인(데이터 무결성)
        Team teamPS = null;
        if (!teamName.isBlank()) {
            teamPS = teamRepository.findByTeamName(teamName).orElseThrow(
                    () -> new Exception400(teamName, "존재하지 않는 팀입니다.")
            );
        }
        Page<User> userPGPS = null;
        try {
            if (teamName.isBlank()) {
                // keyword 존재 여부에 따른 쿼리 실행
                if (keyword.isBlank()) {
                    userPGPS = userQueryRepository.findAll(page);
                } else {
                    userPGPS = userQueryRepository.findAllByKeyword(keyword, page);
                }
            } else {
                // keyword 존재 여부에 따른 쿼리 실행
                if (keyword.isBlank()) {
                    userPGPS = userQueryRepository.findAllByTeamId(teamPS.getId(), page);
                } else {
                    userPGPS = userQueryRepository.findAllByKeywordAndTeamId(teamPS.getId(), keyword, page);
                }
            }

            return new AdminResponse.GetAdminPageOutDTO(
                    teamRepository.findAll().stream()
                            .map(team -> {
                                Integer teamCount = userRepository.calculateCountByTeamId(team.getId());
                                return new AdminResponse.TeamListDTO(team, teamCount);
                            })
                            .collect(Collectors.toList()),
                    userPGPS.getContent().stream()
                            .map(user -> {
                                String startWork = user.getStartWork().toLocalDate().toString();
                                String tempTeamName = user.getTeam() != null ? user.getTeam().getTeamName() : null;
                                return new AdminResponse.UserListDTO(user, startWork, tempTeamName);
                            })
                            .collect(Collectors.toList()),
                    userPGPS.getSize(),
                    userPGPS.getTotalElements(),
                    userPGPS.getTotalPages(),
                    userPGPS.getNumber(),
                    userPGPS.isFirst(),
                    userPGPS.isLast(),
                    userPGPS.isEmpty()
            );
        } catch (Exception e) {
            throw new Exception500("사용자 또는 팀 조회 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void updateAuthAndTeam(AdminRequest.UpdateAuthAndTeamInDTO updateAuthAndTeamInDTO) {
        User userPS = userRepository.findById(updateAuthAndTeamInDTO.getUserId()).orElseThrow(
                () -> new Exception400(String.valueOf(updateAuthAndTeamInDTO.getUserId()), "존재하지 않는 사용자입니다.")
        );
        Team teamPS = teamRepository.findByTeamName(updateAuthAndTeamInDTO.getTeamName()).orElseThrow(
                () -> new Exception400(updateAuthAndTeamInDTO.getTeamName(), "존재하지 않는 팀입니다.")
        );
        try {
            // 권한 변경
            if (!userPS.getRole().equals(updateAuthAndTeamInDTO.getRole())) {
                userPS.updateRole(updateAuthAndTeamInDTO.getRole());
            }
            // 소속팀 변경
            if (!userPS.getTeam().getTeamName().equals(updateAuthAndTeamInDTO.getTeamName())) {
                userPS.updateTeamName(teamPS);
            }
        } catch (Exception e) {
            throw new Exception500("권한 또는 팀 변경 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public AdminResponse.AddOutDTO add(AdminRequest.AddInDTO addInDTO) {
        if (teamRepository.findByTeamName(addInDTO.getTeamName()).isPresent()) {
            throw new Exception400(addInDTO.getTeamName(), "이미 존재하는 팀입니다.");
        }
        try {
            Team teamPS = teamRepository.save(addInDTO.toEntity());
            return new AdminResponse.AddOutDTO(teamPS);
        } catch (Exception e) {
            throw new Exception500("팀 추가 실패 : " + e.getMessage());
        }
    }

    @Log
    @Transactional
    public void delete(Long id) {
        Team teamPS = teamRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), "존재하지 않는 팀입니다.")
        );
        // 팀에 소속된 인원이 1명이라도 있을 경우 팀 삭제 불가
        int count = userRepository.calculateCountByTeamId(teamPS.getId());
        if (count > 0) {
            throw new Exception400(String.valueOf(id), "팀에 소속된 인원이 1명 이상입니다.");
        }
        try {
            teamRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception500("팀 삭제 실패 : " + e.getMessage());
        }
    }
}
