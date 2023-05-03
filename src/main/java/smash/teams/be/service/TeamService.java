package smash.teams.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smash.teams.be.core.exception.Exception400;
import smash.teams.be.core.exception.Exception500;
import smash.teams.be.dto.team.TeamRequest;
import smash.teams.be.model.team.Team;
import smash.teams.be.model.team.TeamRepository;
import smash.teams.be.model.user.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public Team add(TeamRequest.AddDTO addDTO) {
        Optional<Team> teamOP = teamRepository.findByTeamName(addDTO.getTeamName());
        if (teamOP.isPresent()) {
            throw new Exception400(addDTO.getTeamName(), "이미 존재하는 팀입니다.");
        }
        try {
            Team teamPS = teamRepository.save(addDTO.toEntity());
            return teamPS;
        } catch (Exception e) {
            throw new Exception500("팀 추가 실패 : " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) {
        Optional<Team> teamOP = teamRepository.findById(id);
        if (!teamOP.isPresent()) {
            throw new Exception400(id + "", "존재하지 않는 팀입니다.");
        }
        // 팀에 소속된 인원이 1명이라도 있을 경우 팀 삭제 불가
        int count = userRepository.calculateCountByTeamId(teamOP.get().getId());
        if (count > 0) {
            throw new Exception400(id + "", "팀에 소속된 인원이 1명 이상입니다.");
        }
        try {
            teamRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception500("팀 삭제 실패 : " + e.getMessage());
        }
    }
}
